package com.daveestar.bettervanilla.crafting;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Light;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.enums.CraftingRecipe;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class InvisibleLightCrafting extends CustomCraftingRecipe {

  public InvisibleLightCrafting() {
    super(CraftingRecipe.INVISIBLE_LIGHT);
  }

  @Override
  protected ItemStack buildResultItem() {
    ItemStack item = new ItemStack(Material.LIGHT);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      CraftingRecipe recipe = getRecipe();
      meta.displayName(Component.text(ChatColor.YELLOW + recipe.getName()));
      meta.lore(Arrays.asList(
          ChatColor.GRAY + recipe.getDescription(),
          ChatColor.DARK_GRAY + "Crafted with BetterVanilla.")
          .stream()
          .filter(Objects::nonNull)
          .map(Component::text)
          .collect(Collectors.toList()));

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
}
