package com.daveestar.bettervanilla.crafting;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.CraftingRecipe;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class InvisibleItemFrameCrafting extends CustomCraftingRecipe implements Listener {
  private static final String ITEM_TAG_KEY = "invisible_item_frame";
  private static final String ENTITY_TAG_KEY = "invisible_item_frame_entity";
  private static final byte _TAG_PRESENT = 1;

  private final Main _plugin;
  private final NamespacedKey _itemKey;
  private final NamespacedKey _entityKey;

  public InvisibleItemFrameCrafting() {
    super(CraftingRecipe.INVISIBLE_ITEM_FRAME);

    _plugin = Main.getInstance();
    if (_plugin == null) {
      throw new IllegalStateException("BetterVanilla plugin instance is not available.");
    }

    _itemKey = new NamespacedKey(_plugin, ITEM_TAG_KEY);
    _entityKey = new NamespacedKey(_plugin, ENTITY_TAG_KEY);

    _plugin.getServer().getPluginManager().registerEvents(this, _plugin);
  }

  @Override
  protected ItemStack buildResultItem() {
    ItemStack item = new ItemStack(Material.ITEM_FRAME);
    ItemMeta meta = item.getItemMeta();

    if (meta == null) {
      return item;
    }

    CraftingRecipe recipe = getRecipe();
    meta.displayName(Component.text(ChatColor.YELLOW + recipe.getName()));

    List<Component> lore = Arrays
        .asList(
            ChatColor.GRAY + recipe.getDescription(),
            ChatColor.DARK_GRAY + "Crafted with BetterVanilla.")
        .stream()
        .filter(Objects::nonNull)
        .map(Component::text)
        .collect(Collectors.toList());

    meta.lore(lore);
    meta.getPersistentDataContainer().set(_itemKey, PersistentDataType.BYTE, _TAG_PRESENT);

    item.setItemMeta(meta);
    return item;
  }

  // ---------------
  // PRIVATE METHODS
  // ---------------

  private boolean _isInvisibleItemFrameItem(ItemStack item) {
    if (item == null || item.getType() != Material.ITEM_FRAME || !item.hasItemMeta()) {
      return false;
    }

    PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
    return container.has(_itemKey, PersistentDataType.BYTE);
  }

  private boolean _isInvisibleItemFrameEntity(ItemFrame frame) {
    if (frame == null) {
      return false;
    }

    PersistentDataContainer container = frame.getPersistentDataContainer();
    return container.has(_entityKey, PersistentDataType.BYTE);
  }

  private void _markInvisibleItemFrameEntity(ItemFrame frame) {
    if (frame == null) {
      return;
    }

    PersistentDataContainer container = frame.getPersistentDataContainer();
    container.set(_entityKey, PersistentDataType.BYTE, _TAG_PRESENT);
    _updateFrameVisibility(frame);
  }

  private void _updateFrameVisibility(ItemFrame frame) {
    if (frame == null || !frame.isValid() || frame.isDead()) {
      return;
    }

    ItemStack displayed = frame.getItem();
    boolean hasItem = displayed != null && displayed.getType() != Material.AIR;
    frame.setVisible(!hasItem);
  }

  private void _scheduleVisibilityUpdate(ItemFrame frame) {
    _plugin.getServer().getScheduler().runTask(_plugin, () -> {
      if (!frame.isValid() || frame.isDead()) {
        return;
      }

      _updateFrameVisibility(frame);
    });
  }

  // ------
  // EVENTS
  // ------

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onFramePlaced(HangingPlaceEvent event) {
    if (!(event.getEntity() instanceof ItemFrame frame)) {
      return;
    }

    if (!_isInvisibleItemFrameItem(event.getItemStack())) {
      return;
    }

    _markInvisibleItemFrameEntity(frame);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onFrameInteract(PlayerInteractEntityEvent event) {
    if (!(event.getRightClicked() instanceof ItemFrame frame)) {
      return;
    }

    if (!_isInvisibleItemFrameEntity(frame)) {
      return;
    }

    _scheduleVisibilityUpdate(frame);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onFrameDamaged(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof ItemFrame frame)) {
      return;
    }

    if (!_isInvisibleItemFrameEntity(frame)) {
      return;
    }

    _scheduleVisibilityUpdate(frame);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onFrameDrop(EntityDropItemEvent event) {
    if (!(event.getEntity() instanceof ItemFrame frame)) {
      return;
    }

    if (!_isInvisibleItemFrameEntity(frame)) {
      return;
    }

    ItemStack drop = event.getItemDrop().getItemStack();
    if (drop.getType() != Material.ITEM_FRAME) {
      return;
    }

    event.setCancelled(true);

    Location dropLocation = event.getItemDrop().getLocation();
    frame.getWorld().dropItem(dropLocation, createResultItem());
  }
}
