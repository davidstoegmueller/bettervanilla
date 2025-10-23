package com.daveestar.bettervanilla.enums;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Light;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public enum CraftingRecipe {
  INVISIBLE_LIGHT(
      "bettervanilla_invisible_light",
      "Invisible Light",
      "Invisible block emitting light level 15.",
      new Material[] {
          null, Material.TORCH, null,
          Material.TORCH, Material.GLOWSTONE, Material.TORCH,
          null, Material.TORCH, null
      }) {
    @Override
    public ItemStack createResultItem() {
      ItemStack item = new ItemStack(Material.LIGHT);
      ItemMeta meta = item.getItemMeta();

      if (meta != null) {
        meta.displayName(Component.text(ChatColor.YELLOW + getName()));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(ChatColor.GRAY + getDescription()));
        lore.add(Component.text(ChatColor.DARK_GRAY + "Crafted with BetterVanilla."));
        meta.lore(lore);

        if (meta instanceof BlockDataMeta blockDataMeta) {
          BlockData data = Bukkit.createBlockData(Material.LIGHT);

          if (data instanceof Light lightData) {
            lightData.setLevel(15);
            blockDataMeta.setBlockData(lightData);
          }
        }

        item.setItemMeta(meta);
      }

      return item;
    }
  };

  private final String _key;
  private final String _name;
  private final String _description;
  private final Material[] _defaultPattern;

  private static final int _GRID_WIDTH = 3;
  private static final int _GRID_SIZE = 9;
  private static final char[] _SHAPE_KEYS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I' };

  CraftingRecipe(String key, String name, String description, Material[] defaultPattern) {
    _key = key;
    _name = name;
    _description = description;
    _defaultPattern = _copyPattern(defaultPattern);
  }

  private static Material[] _copyPattern(Material[] source) {
    Material[] pattern = new Material[_GRID_SIZE];
    if (source != null) {
      for (int i = 0; i < Math.min(source.length, _GRID_SIZE); i++) {
        pattern[i] = source[i];
      }
    }

    return pattern;
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

  public List<ItemStack> buildDefaultMatrix() {
    List<ItemStack> matrix = new ArrayList<>(_GRID_SIZE);

    for (Material material : _defaultPattern) {
      if (material == null || material == Material.AIR) {
        matrix.add(null);
      } else {
        matrix.add(new ItemStack(material));
      }
    }

    return matrix;
  }

  public abstract ItemStack createResultItem();

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
