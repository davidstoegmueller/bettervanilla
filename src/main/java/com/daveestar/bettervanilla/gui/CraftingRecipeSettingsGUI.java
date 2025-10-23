package com.daveestar.bettervanilla.gui;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.CraftingRecipe;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.CustomGUI;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class CraftingRecipeSettingsGUI {
  private static final int[] _DEFAULT_SLOTS = {
      4, 3, 5, 2, 6, 1, 7, 0, 8,
      13, 12, 14, 11, 15, 10, 16, 9, 17
  };

  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final CraftingRecipeEditorGUI _craftingRecipeEditorGUI;
  private final Map<CraftingRecipe, RecipeConfig> _recipeConfigs;

  public CraftingRecipeSettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _craftingRecipeEditorGUI = new CraftingRecipeEditorGUI();
    _recipeConfigs = new EnumMap<>(CraftingRecipe.class);

    _plugin.getCraftingRecipes().forEach((recipe, handler) -> {
      if (handler == null) {
        return;
      }

      ItemStack resultItem = handler.createResultItem();
      Material iconMaterial = resultItem.getType();
      if (iconMaterial == Material.AIR) {
        iconMaterial = Material.CRAFTING_TABLE;
      }

      _recipeConfigs.put(recipe, new RecipeConfig(
          recipe,
          iconMaterial,
          () -> handler.createResultItem(),
          handler::getConfiguredRecipeMatrix,
          handler::getDefaultRecipeMatrix,
          handler::applyRecipe));
    });
  }

  public void displayGUI(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    if (_recipeConfigs.isEmpty()) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "No crafting recipes are currently registered.");
      return;
    }

    Map<String, ItemStack> entries = new HashMap<>();
    Map<String, Integer> customSlots = new HashMap<>();

    int index = 0;
    for (RecipeConfig config : _recipeConfigs.values()) {
      String key = config.recipe().getKey();
      entries.put(key, _createRecipeItem(config));

      if (index < _DEFAULT_SLOTS.length) {
        customSlots.put(key, _DEFAULT_SLOTS[index]);
      }

      index++;
    }

    CustomGUI gui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "A� Crafting Recipes",
        entries, 2, customSlots, parentMenu,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    if (backAction != null) {
      gui.setBackAction(backAction);
    }

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    for (RecipeConfig config : _recipeConfigs.values()) {
      String key = config.recipe().getKey();
      actions.put(key, new CustomGUI.ClickAction() {
        @Override
        public void onLeftClick(Player player) {
          _toggleRecipe(player, config);
          displayGUI(player, parentMenu, backAction);
        }

        @Override
        public void onRightClick(Player player) {
          _displayCraftingEditorGUI(player, config, viewer -> displayGUI(viewer, parentMenu, backAction));
        }
      });
    }

    gui.setClickActions(actions);
    gui.open(p);
  }

  private ItemStack _createRecipeItem(RecipeConfig config) {
    boolean enabled = _settingsManager.getCraftingRecipeEnabled(config.recipe().getKey());
    ItemStack icon = new ItemStack(config.recipeIcon());
    ItemMeta meta = icon.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "A� " + ChatColor.YELLOW + config.recipe().getName()));

      List<String> loreLines = new ArrayList<>();
      loreLines.add(ChatColor.YELLOW + "A� " + ChatColor.GRAY + "Manage this crafting recipe.");
      loreLines.add("");
      loreLines.add(ChatColor.YELLOW + "A� " + ChatColor.GRAY + config.recipe().getDescription());
      loreLines.add("");
      loreLines.add(ChatColor.YELLOW + "A� " + ChatColor.GRAY + "State: "
          + (enabled ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
      loreLines.add(ChatColor.YELLOW + "A� " + ChatColor.GRAY + "Left-Click: Toggle");
      loreLines.add(ChatColor.YELLOW + "A� " + ChatColor.GRAY + "Right-Click: Edit recipe");

      meta.lore(loreLines.stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      icon.setItemMeta(meta);
    }

    return icon;
  }

  private void _toggleRecipe(Player p, RecipeConfig config) {
    boolean newState = !_settingsManager.getCraftingRecipeEnabled(config.recipe().getKey());
    _settingsManager.setCraftingRecipeEnabled(config.recipe().getKey(), newState);
    config.applyRecipeAction().run();

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix()
        + ChatColor.YELLOW + config.recipe().getName()
        + ChatColor.GRAY + " recipe is now "
        + ChatColor.YELLOW + ChatColor.BOLD + stateText);
    p.playSound(p, Sound.UI_BUTTON_CLICK, 0.5F, 1);
  }

  private void _displayCraftingEditorGUI(Player p, RecipeConfig config, Consumer<Player> backAction) {
    _craftingRecipeEditorGUI.displayGUI(p, config, backAction);
  }

  public static record RecipeConfig(
      CraftingRecipe recipe,
      Material recipeIcon,
      Supplier<ItemStack> recipeResultItemSupplier,
      Supplier<List<ItemStack>> recipeMatrixSupplier,
      Supplier<List<ItemStack>> defaultRecipeMatrixSupplier,
      Runnable applyRecipeAction) {
  }
}
