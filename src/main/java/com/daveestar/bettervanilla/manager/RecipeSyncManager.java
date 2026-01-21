package com.daveestar.bettervanilla.manager;

import io.netty.buffer.Unpooled;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.recipesync.FabricRecipeSync;
import com.daveestar.bettervanilla.recipesync.NeoForgeRecipeSync;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RecipeSyncManager implements Listener {
  private final Main _plugin;

  public RecipeSyncManager() {
    _plugin = Main.getInstance();
    Bukkit.getPluginManager().registerEvents(this, _plugin);
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    final Player originalPlayer = e.getPlayer();
    final ServerPlayer p = ((CraftPlayer) originalPlayer).getHandle();
    final MinecraftServer server = p.level().getServer();
    final RecipeManager recipeManager = server.getRecipeManager();

    RecipeMap recipeMap = recipeManager.recipes;

    RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), server.registryAccess());
    String brand = originalPlayer.getClientBrandName();

    if (brand == null) {
      return; // Unknown brand, do not send any custom payload
    }

    if (brand.equalsIgnoreCase("fabric")) {
      sendFabricPayload(p, recipeMap, buffer);
    } else if (brand.equalsIgnoreCase("neoforge")) {
      sendNeoForgePayload(p, server, recipeMap, buffer);
    }
  }

  private static void sendNeoForgePayload(ServerPlayer p, MinecraftServer server, RecipeMap recipeMap,
      RegistryFriendlyByteBuf buffer) {
    List<RecipeType<?>> allRecipeTypes = BuiltInRegistries.RECIPE_TYPE.stream().toList();
    var payload = NeoForgeRecipeSync.create(allRecipeTypes, recipeMap);
    NeoForgeRecipeSync.STREAM_CODEC.encode(buffer, payload);

    byte[] bytes = new byte[buffer.writerIndex()];
    buffer.getBytes(0, bytes);

    sendPayload(p, Identifier.fromNamespaceAndPath("neoforge", "recipe_content"), bytes);

    p.connection
        .send(new ClientboundUpdateTagsPacket(TagNetworkSerialization.serializeTagsToNetwork(server.registries())));
  }

  private static void sendFabricPayload(ServerPlayer p, RecipeMap recipeMap, RegistryFriendlyByteBuf buffer) {
    var list = new ArrayList<FabricRecipeSync.Entry>();
    var seen = new HashSet<RecipeSerializer<?>>();

    for (RecipeSerializer<?> serializer : BuiltInRegistries.RECIPE_SERIALIZER) {
      if (!seen.add(serializer))
        continue; // skip duplicates

      List<RecipeHolder<?>> recipes = new ArrayList<>();
      for (RecipeHolder<?> holder : recipeMap.values()) {
        if (holder.value().getSerializer() == serializer) {
          recipes.add(holder);
        }
      }

      if (!recipes.isEmpty()) {
        RecipeSerializer<?> entrySerializer = recipes.get(0).value().getSerializer();
        list.add(new FabricRecipeSync.Entry(entrySerializer, recipes));
      }
    }

    var payload = new FabricRecipeSync(list);
    FabricRecipeSync.CODEC.encode(buffer, payload);

    byte[] bytes = new byte[buffer.writerIndex()];
    buffer.getBytes(0, bytes);

    sendPayload(p, Identifier.fromNamespaceAndPath("fabric", "recipe_sync"), bytes);
  }

  private static void sendPayload(ServerPlayer p, Identifier id, byte[] bytes) {
    p.connection.send(new ClientboundCustomPayloadPacket(new DiscardedPayload(id, bytes)));
  }
}
