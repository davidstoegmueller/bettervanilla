package com.daveestar.bettervanilla.manager;

import io.netty.buffer.Unpooled;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.recipesync.FabricRecipeSync;
import com.daveestar.bettervanilla.recipesync.NeoForgeRecipeSync;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecipeSyncManager implements Listener {
  private final Main _plugin;

  public RecipeSyncManager() {
    _plugin = Main.getInstance();
    Bukkit.getPluginManager().registerEvents(this, _plugin);
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    if (!_plugin.getSettingsManager().getRecipeSyncEnabled()) {
      return;
    }

    final Player originalPlayer = e.getPlayer();
    final ServerPlayer p = ((CraftPlayer) originalPlayer).getHandle();
    final MinecraftServer server = p.level().getServer();
    final RecipeManager recipeManager = server.getRecipeManager();
    String brand = originalPlayer.getClientBrandName();

    if (brand == null) {
      return; // Unknown brand, do not send any custom payload
    }

    if (brand.equalsIgnoreCase("fabric")) {
      sendFabricPayload(p, server, recipeManager);
    } else if (brand.equalsIgnoreCase("neoforge")) {
      sendNeoForgePayload(p, server, recipeManager);
    }
  }

  private static void sendNeoForgePayload(ServerPlayer p, MinecraftServer server, RecipeManager recipeManager) {
    List<RecipeHolder<?>> recipes = collectAllRecipes(recipeManager);
    List<RecipeType<?>> allRecipeTypes = BuiltInRegistries.RECIPE_TYPE.stream().toList();
    Set<RecipeType<?>> recipeTypeSet = Set.copyOf(allRecipeTypes);
    List<RecipeHolder<?>> filtered = recipes.stream()
        .filter(holder -> recipeTypeSet.contains(holder.value().getType()))
        .toList();

    var payload = new NeoForgeRecipeSync(recipeTypeSet, filtered);
    RegistryFriendlyByteBuf buffer = createBuffer(server);
    NeoForgeRecipeSync.STREAM_CODEC.encode(buffer, payload);

    sendPayload(p, Identifier.fromNamespaceAndPath("neoforge", "recipe_content"), toBytes(buffer));
    p.connection
        .send(new ClientboundUpdateTagsPacket(TagNetworkSerialization.serializeTagsToNetwork(server.registries())));
  }

  private static void sendFabricPayload(ServerPlayer p, MinecraftServer server, RecipeManager recipeManager) {
    List<RecipeHolder<?>> recipes = collectAllRecipes(recipeManager);
    var bySerializer = new HashMap<RecipeSerializer<?>, List<RecipeHolder<?>>>();

    for (RecipeHolder<?> holder : recipes) {
      RecipeSerializer<?> serializer = holder.value().getSerializer();
      if (BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer) == null) {
        continue;
      }

      bySerializer.computeIfAbsent(serializer, key -> new ArrayList<>()).add(holder);
    }

    var list = new ArrayList<FabricRecipeSync.Entry>();
    for (Map.Entry<RecipeSerializer<?>, List<RecipeHolder<?>>> entry : bySerializer.entrySet()) {
      list.add(new FabricRecipeSync.Entry(entry.getKey(), entry.getValue()));
    }

    var payload = new FabricRecipeSync(list);
    RegistryFriendlyByteBuf buffer = createBuffer(server);
    FabricRecipeSync.CODEC.encode(buffer, payload);

    sendPayload(p, Identifier.fromNamespaceAndPath("fabric", "recipe_sync"), toBytes(buffer));
  }

  private static List<RecipeHolder<?>> collectAllRecipes(RecipeManager recipeManager) {
    RecipeMap recipeMap = recipeManager.recipes;
    Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> holdersByKey = new HashMap<>();

    for (RecipeHolder<?> holder : recipeMap.values()) {
      holdersByKey.put(holder.id(), holder);
    }

    addBukkitRecipes(holdersByKey);
    return new ArrayList<>(holdersByKey.values());
  }

  private static void addBukkitRecipes(Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> holdersByKey) {
    Iterator<org.bukkit.inventory.Recipe> iterator = Bukkit.recipeIterator();

    while (iterator.hasNext()) {
      org.bukkit.inventory.Recipe recipe = iterator.next();
      if (!(recipe instanceof Keyed keyed)) {
        continue;
      }

      NamespacedKey key = keyed.getKey();
      if (key == null) {
        continue;
      }

      Recipe<?> nmsRecipe = BukkitRecipeConverter.toNmsRecipe(recipe);
      if (nmsRecipe == null) {
        continue;
      }

      Identifier id = Identifier.fromNamespaceAndPath(key.getNamespace(), key.getKey());
      ResourceKey<Recipe<?>> resourceKey = ResourceKey.create(Registries.RECIPE, id);

      holdersByKey.putIfAbsent(resourceKey, new RecipeHolder<>(resourceKey, nmsRecipe));
    }
  }

  private static RegistryFriendlyByteBuf createBuffer(MinecraftServer server) {
    return new RegistryFriendlyByteBuf(Unpooled.buffer(), server.registryAccess());
  }

  private static byte[] toBytes(RegistryFriendlyByteBuf buffer) {
    byte[] bytes = new byte[buffer.writerIndex()];
    buffer.getBytes(0, bytes);
    return bytes;
  }

  private static final class BukkitRecipeConverter {
    private static final String CRAFT_RECIPE_CLASS = "org.bukkit.craftbukkit.inventory.CraftRecipe";
    private static Method _toNmsMethod;

    private static Recipe<?> toNmsRecipe(org.bukkit.inventory.Recipe recipe) {
      if (recipe == null) {
        return null;
      }

      Method method = resolveToNmsMethod();
      if (method == null) {
        return null;
      }

      try {
        Object result = method.invoke(null, recipe);
        if (result instanceof Recipe<?> nmsRecipe) {
          return nmsRecipe;
        }
      } catch (ReflectiveOperationException ignored) {
      }

      return null;
    }

    private static Method resolveToNmsMethod() {
      if (_toNmsMethod != null) {
        return _toNmsMethod;
      }

      try {
        Class<?> craftRecipe = Class.forName(CRAFT_RECIPE_CLASS);
        for (Method method : craftRecipe.getDeclaredMethods()) {
          if (!method.getName().equals("toNMS") && !method.getName().equals("toNMSRecipe")) {
            continue;
          }

          Class<?>[] params = method.getParameterTypes();
          if (params.length == 1 && org.bukkit.inventory.Recipe.class.isAssignableFrom(params[0])) {
            method.setAccessible(true);
            _toNmsMethod = method;
            return _toNmsMethod;
          }
        }
      } catch (ClassNotFoundException ignored) {
      }

      return null;
    }
  }

  private static void sendPayload(ServerPlayer p, Identifier id, byte[] bytes) {
    p.connection.send(new ClientboundCustomPayloadPacket(new DiscardedPayload(id, bytes)));
  }
}
