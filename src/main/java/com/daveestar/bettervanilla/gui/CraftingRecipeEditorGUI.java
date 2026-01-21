package com.daveestar.bettervanilla.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.gui.CraftingRecipeSettingsGUI.RecipeConfig;
import com.daveestar.bettervanilla.manager.SettingsManager;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class CraftingRecipeEditorGUI implements Listener {
  private static final int RESULT_SLOT = 0;
  private static final List<Integer> GRID_SLOTS = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

  private final Main plugin;
  private final SettingsManager settingsManager;

  public CraftingRecipeEditorGUI() {
    plugin = Main.getInstance();
    settingsManager = plugin.getSettingsManager();

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  public void displayGUI(Player p, RecipeConfig recipeConfig, Consumer<Player> backAction) {
    RecipeEditorSession session = new RecipeEditorSession(recipeConfig, backAction);
    Inventory inventory = Bukkit.createInventory(
        session,
        InventoryType.WORKBENCH,
        Component.text(ChatColor.YELLOW + "" + ChatColor.BOLD + "» " + recipeConfig.recipe().getName()));

    session.bindInventory(inventory);
    inventory.setItem(RESULT_SLOT, _createResultItem(recipeConfig));

    p.openInventory(inventory);
    p.sendMessage(Main.getPrefix()
        + ChatColor.GRAY + "Place items in the crafting grid, then click the result to save.");
  }

  // ---------------
  // PRIVATE METHODS
  // ---------------

  private void _handleSave(Player p, RecipeEditorSession session) {
    Inventory inventory = session.getInventory();
    List<ItemStack> matrix = _collectMatrix(inventory);

    if (!_hasIngredients(matrix)) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please add at least one ingredient before saving.");
      p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
      return;
    }

    settingsManager.setCraftingRecipeMatrix(session._recipeConfig().recipe().getKey(), matrix);
    session._recipeConfig().applyRecipeAction().run();

    _returnItemsToPlayer(p, inventory);
    _clearGridItems(inventory);
    session._markSaved();

    p.sendMessage(Main.getPrefix()
        + ChatColor.GRAY + "Saved recipe for "
        + ChatColor.YELLOW + session._recipeConfig().recipe().getName() + ChatColor.GRAY + ".");
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
    p.closeInventory();
  }

  private ItemStack _createResultItem(RecipeConfig recipeConfig) {
    ItemStack baseItem = recipeConfig.recipeResultItemSupplier().get();
    ItemStack result = baseItem == null ? new ItemStack(Material.BARRIER) : baseItem.clone();

    ItemMeta meta = result.getItemMeta();
    if (meta != null) {
      List<Component> lore = meta.lore() != null ? new ArrayList<>(meta.lore()) : new ArrayList<>();
      lore.add(Component.text(""));
      lore.add(Component.text(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Save Recipe"));
      meta.lore(lore);
      result.setItemMeta(meta);
    }

    return result;
  }

  private void _sanitizeGrid(Player p, Inventory inventory) {
    boolean updated = false;

    for (int slot : GRID_SLOTS) {
      ItemStack item = inventory.getItem(slot);
      if (item == null || item.getType() == Material.AIR || item.getAmount() <= 1) {
        continue;
      }

      ItemStack overflow = item.clone();
      overflow.setAmount(item.getAmount() - 1);
      item.setAmount(1);
      inventory.setItem(slot, item);
      _giveOrDrop(p, overflow);
      updated = true;
    }

    if (updated) {
      p.updateInventory();
    }
  }

  private List<ItemStack> _collectMatrix(Inventory inventory) {
    List<ItemStack> matrix = new ArrayList<>(GRID_SLOTS.size());

    for (int slot : GRID_SLOTS) {
      ItemStack item = inventory.getItem(slot);
      if (item == null || item.getType() == Material.AIR) {
        matrix.add(null);
        continue;
      }

      ItemStack clone = item.clone();
      clone.setAmount(1);
      matrix.add(clone);
    }

    return matrix;
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

  private void _clearGridItems(Inventory inventory) {
    for (int slot : GRID_SLOTS) {
      inventory.setItem(slot, null);
    }
  }

  private void _returnItemsToPlayer(Player p, Inventory inventory) {
    for (int slot : GRID_SLOTS) {
      ItemStack item = inventory.getItem(slot);
      if (item == null || item.getType() == Material.AIR) {
        continue;
      }

      _giveOrDrop(p, item.clone());
      inventory.setItem(slot, null);
    }
  }

  private void _giveOrDrop(Player p, ItemStack item) {
    if (item == null || item.getType() == Material.AIR || item.getAmount() <= 0) {
      return;
    }

    Map<Integer, ItemStack> leftovers = p.getInventory().addItem(item);
    if (!leftovers.isEmpty()) {
      leftovers.values()
          .forEach(left -> p.getWorld().dropItemNaturally(p.getLocation(), left));
    }
  }

  // ------
  // EVENTS
  // ------

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    RecipeEditorSession session = RecipeEditorSession.from(e.getView().getTopInventory());
    if (session == null) {
      return;
    }

    if (!(e.getWhoClicked() instanceof Player p)) {
      return;
    }

    if (e.getRawSlot() == RESULT_SLOT) {
      e.setCancelled(true);
      if (e.getSlot() == RESULT_SLOT && e.isLeftClick()) {
        _handleSave(p, session);
      }
      return;
    }

    if (e.getClickedInventory() != null && e.getClickedInventory().equals(session.getInventory())) {
      Bukkit.getScheduler().runTask(plugin, () -> _sanitizeGrid(p, session.getInventory()));
    }
  }

  @EventHandler
  public void onInventoryDrag(InventoryDragEvent e) {
    RecipeEditorSession session = RecipeEditorSession.from(e.getView().getTopInventory());
    if (session == null) {
      return;
    }

    if (!(e.getWhoClicked() instanceof Player p)) {
      return;
    }

    if (e.getRawSlots().contains(RESULT_SLOT)) {
      e.setCancelled(true);
      return;
    }

    Bukkit.getScheduler().runTask(plugin, () -> _sanitizeGrid(p, session.getInventory()));
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent e) {
    RecipeEditorSession session = RecipeEditorSession.from(e.getInventory());
    if (session == null || !(e.getPlayer() instanceof Player p)) {
      return;
    }

    if (!session._isSaved()) {
      _returnItemsToPlayer(p, session.getInventory());
    }

    Consumer<Player> backAction = session._backAction();
    if (backAction != null) {
      Bukkit.getScheduler().runTask(plugin, () -> backAction.accept(p));
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent e) {
    Player p = e.getPlayer();
    RecipeEditorSession session = RecipeEditorSession.from(p.getOpenInventory().getTopInventory());
    if (session == null) {
      return;
    }

    if (!session._isSaved()) {
      _returnItemsToPlayer(p, session.getInventory());
    }
  }

  // ------------
  // HELPER CLASS
  // ------------

  private static final class RecipeEditorSession implements InventoryHolder {
    private final RecipeConfig recipeConfig;
    private final Consumer<Player> backAction;
    private Inventory inventory;
    private boolean saved;

    private RecipeEditorSession(RecipeConfig recipeConfig, Consumer<Player> backAction) {
      this.recipeConfig = recipeConfig;
      this.backAction = backAction;
    }

    static RecipeEditorSession from(Inventory inventory) {
      if (inventory == null) {
        return null;
      }

      InventoryHolder holder = inventory.getHolder();
      if (holder instanceof RecipeEditorSession session) {
        return session;
      }

      return null;
    }

    private void bindInventory(Inventory inventory) {
      this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
      return inventory;
    }

    private RecipeConfig _recipeConfig() {
      return recipeConfig;
    }

    private Consumer<Player> _backAction() {
      return backAction;
    }

    private boolean _isSaved() {
      return saved;
    }

    private void _markSaved() {
      saved = true;
    }
  }
}
