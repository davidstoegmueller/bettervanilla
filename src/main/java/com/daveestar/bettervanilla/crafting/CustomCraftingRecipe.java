package com.daveestar.bettervanilla.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.CraftingRecipe;
import com.daveestar.bettervanilla.manager.SettingsManager;

public class CustomCraftingRecipe {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final CraftingRecipe _recipe;
  private final NamespacedKey _recipeKey;

  public CustomCraftingRecipe(CraftingRecipe recipe) {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _recipe = recipe;
    _recipeKey = new NamespacedKey(_plugin, recipe.getKey());
  }

  public CraftingRecipe getRecipe() {
    return _recipe;
  }

  public void applyRecipe() {
    Bukkit.removeRecipe(_recipeKey);

    if (!isRecipeEnabled()) {
      return;
    }

    List<ItemStack> recipeMatrix = getConfiguredRecipeMatrix();
    if (!_hasIngredients(recipeMatrix)) {
      recipeMatrix = getDefaultRecipeMatrix();
    }

    ShapedRecipe recipe = _createRecipe(recipeMatrix);
    if (recipe != null) {
      Bukkit.addRecipe(recipe);
    }
  }

  public void destroyRecipe() {
    Bukkit.removeRecipe(_recipeKey);
  }

  public boolean isRecipeEnabled() {
    return _settingsManager.getCraftingRecipeEnabled(_recipe.getKey());
  }

  public List<ItemStack> getConfiguredRecipeMatrix() {
    List<ItemStack> configured = _settingsManager.getCraftingRecipeMatrix(_recipe.getKey(), getDefaultRecipeMatrix());
    return _sanitizeMatrix(configured);
  }

  public List<ItemStack> getDefaultRecipeMatrix() {
    return _sanitizeMatrix(_recipe.buildDefaultMatrix());
  }

  public ItemStack createResultItem() {
    ItemStack result = _recipe.createResultItem();
    if (result == null) {
      return new ItemStack(Material.AIR);
    }

    return result.clone();
  }

  private ShapedRecipe _createRecipe(List<ItemStack> matrix) {
    List<ItemStack> layout = _sanitizeMatrix(matrix);
    if (!_hasIngredients(layout)) {
      return null;
    }

    StringBuilder[] rows = {
        new StringBuilder("   "),
        new StringBuilder("   "),
        new StringBuilder("   ")
    };

    Map<Character, ItemStack> ingredients = new HashMap<>();
    for (int i = 0; i < CraftingRecipe.getGridSize(); i++) {
      ItemStack item = layout.get(i);
      if (item == null || item.getType() == Material.AIR) {
        continue;
      }

      int row = i / CraftingRecipe.getGridWidth();
      int column = i % CraftingRecipe.getGridWidth();
      char key = CraftingRecipe.getShapeKeys()[i];

      rows[row].setCharAt(column, key);
      ingredients.put(key, item);
    }

    if (ingredients.isEmpty()) {
      return null;
    }

    ShapedRecipe recipe = new ShapedRecipe(_recipeKey, createResultItem());
    recipe.shape(rows[0].toString(), rows[1].toString(), rows[2].toString());
    ingredients.forEach((key, item) -> recipe.setIngredient(key, new RecipeChoice.ExactChoice(item)));

    return recipe;
  }

  private boolean _hasIngredients(List<ItemStack> matrix) {
    if (matrix == null) {
      return false;
    }

    for (ItemStack item : matrix) {
      if (item != null && item.getType() != Material.AIR) {
        return true;
      }
    }

    return false;
  }

  private List<ItemStack> _sanitizeMatrix(List<ItemStack> matrix) {
    List<ItemStack> sanitized = new ArrayList<>(CraftingRecipe.getGridSize());

    for (int i = 0; i < CraftingRecipe.getGridSize(); i++) {
      ItemStack item = (matrix != null && i < matrix.size()) ? matrix.get(i) : null;
      if (item == null || item.getType() == Material.AIR) {
        sanitized.add(null);
        continue;
      }

      ItemStack clone = item.clone();
      clone.setAmount(1);
      sanitized.add(clone);
    }

    return sanitized;
  }
}
