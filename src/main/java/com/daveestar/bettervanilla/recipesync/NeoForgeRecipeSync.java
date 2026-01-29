package com.daveestar.bettervanilla.recipesync;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record NeoForgeRecipeSync(
    Set<RecipeType<?>> recipeTypes,
    List<RecipeHolder<?>> recipes) implements CustomPacketPayload {
  public static final Type<NeoForgeRecipeSync> TYPE = new Type<>(
      Identifier.fromNamespaceAndPath("neoforge", "recipe_content"));

  public static final StreamCodec<RegistryFriendlyByteBuf, NeoForgeRecipeSync> STREAM_CODEC = StreamCodec
      .composite(
          ByteBufCodecs.registry(Registries.RECIPE_TYPE).apply(ByteBufCodecs.collection(HashSet::new)),
          NeoForgeRecipeSync::recipeTypes,
          RecipeHolder.STREAM_CODEC.apply(ByteBufCodecs.list()), NeoForgeRecipeSync::recipes,
          NeoForgeRecipeSync::new);

  public static NeoForgeRecipeSync create(Collection<RecipeType<?>> recipeTypes, RecipeMap recipes) {
    var recipeTypeSet = Set.copyOf(recipeTypes);
    // Fast-path for empty recipe type set (if no mod wants to sync anything)
    if (recipeTypeSet.isEmpty()) {
      return new NeoForgeRecipeSync(recipeTypeSet, List.of());
    } else {
      var recipeSubset = recipes.values().stream().filter(h -> recipeTypeSet.contains(h.value().getType())).toList();
      return new NeoForgeRecipeSync(recipeTypeSet, recipeSubset);
    }
  }

  @NonNull
  @Override
  public Type<NeoForgeRecipeSync> type() {
    return TYPE;
  }
}