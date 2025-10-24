package com.daveestar.bettervanilla.enums;

import java.util.function.Supplier;

import org.bukkit.Material;

import com.daveestar.bettervanilla.crafting.CustomCraftingRecipe;
import com.daveestar.bettervanilla.crafting.InvisibleItemFrameCrafting;
import com.daveestar.bettervanilla.crafting.InvisibleLightCrafting;

public enum CraftingRecipe {
  INVISIBLE_ITEM_FRAME(
      "bettervanilla_invisible_item_frame",
      "Invisible Item Frame",
      "Item frame that hides when it holds an item.",
      new Material[] {
          Material.GLASS, Material.GLASS, Material.GLASS,
          Material.GLASS, Material.ITEM_FRAME, Material.GLASS,
          Material.GLASS, Material.GLASS, Material.GLASS
      },
      InvisibleItemFrameCrafting::new),
  INVISIBLE_LIGHT(
      "bettervanilla_invisible_light",
      "Invisible Light",
      "Invisible block emitting light level 15.",
      new Material[] {
          null, Material.TORCH, null,
          Material.TORCH, Material.GLOWSTONE, Material.TORCH,
          null, Material.TORCH, null
      },
      InvisibleLightCrafting::new);

  private final String _key;
  private final String _name;
  private final String _description;
  private final Material[] _defaultPattern;
  private final Supplier<CustomCraftingRecipe> _factory;

  private static final int _GRID_WIDTH = 3;
  private static final int _GRID_SIZE = 9;
  private static final char[] _SHAPE_KEYS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I' };

  CraftingRecipe(String key, String name, String description, Material[] defaultPattern,
      Supplier<CustomCraftingRecipe> factory) {
    _key = key;
    _name = name;
    _description = description;
    _defaultPattern = defaultPattern != null ? defaultPattern.clone() : new Material[_GRID_SIZE];
    _factory = factory;
  }

  public String getKey() {
    return _key;
  }

  public String getName() {
    return _name;
  }

  public String getDescription() {
    return _description;
  }

  public Material[] getDefaultPattern() {
    return _defaultPattern.clone();
  }

  public CustomCraftingRecipe createHandler() {
    return _factory != null ? _factory.get() : null;
  }

  public static int getGridWidth() {
    return _GRID_WIDTH;
  }

  public static int getGridSize() {
    return _GRID_SIZE;
  }

  public static char[] getShapeKeys() {
    return _SHAPE_KEYS.clone();
  }
}
