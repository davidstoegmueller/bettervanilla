package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.CustomGUI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.md_5.bungee.api.ChatColor;

public class VeinChopperSettingsGUI implements Listener {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final MaterialToggleGUI _toolsGUI;
  private final MaterialToggleGUI _blocksGUI;
  private final Map<UUID, CustomGUI> _sizePending;

  public VeinChopperSettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _toolsGUI = new MaterialToggleGUI("Vein Chopper Tools",
        SettingsManager.DEFAULT_VEIN_CHOPPER_TOOLS.stream()
            .map(Material::matchMaterial).filter(Objects::nonNull).toList(),
        () -> _settingsManager.getVeinChopperAllowedTools(),
        list -> _settingsManager.setVeinChopperAllowedTools(list));
    _blocksGUI = new MaterialToggleGUI("Vein Chopper Blocks",
        SettingsManager.DEFAULT_VEIN_CHOPPER_BLOCKS.stream()
            .map(Material::matchMaterial).filter(Objects::nonNull).toList(),
        () -> _settingsManager.getVeinChopperAllowedBlocks(),
        list -> _settingsManager.setVeinChopperAllowedBlocks(list));
    _sizePending = new HashMap<>();
    _plugin.getServer().getPluginManager().registerEvents(this, _plugin);
  }

  public void displayGUI(Player p, CustomGUI parent) {
    Map<String, ItemStack> entries = new HashMap<>();
    entries.put("enabled", _createEnabledItem());
    entries.put("maxsize", _createMaxSizeItem());
    entries.put("sound", _createSoundItem());
    entries.put("tools", _createToolsItem());
    entries.put("blocks", _createBlocksItem());

    Map<String, Integer> customSlots = new HashMap<>();
    customSlots.put("enabled", 1);
    customSlots.put("maxsize", 3);
    customSlots.put("sound", 5);
    customSlots.put("tools", 11);
    customSlots.put("blocks", 15);

    CustomGUI gui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Vein Chopper Settings",
        entries, 3, customSlots, parent,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    actions.put("enabled", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        boolean newState = !_settingsManager.getVeinChopper();
        _settingsManager.setVeinChopper(newState);
        displayGUI(player, parent);
      }
    });
    actions.put("maxsize", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        player.sendMessage(Main.getPrefix() + "Enter max vein size (1-1024):");
        _sizePending.put(player.getUniqueId(), parent);
        player.closeInventory();
      }
    });
    actions.put("sound", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        boolean newState = !_settingsManager.getVeinChopperSound();
        _settingsManager.setVeinChopperSound(newState);
        displayGUI(player, parent);
      }
    });
    actions.put("tools", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        _toolsGUI.displayGUI(player, gui);
      }
    });
    actions.put("blocks", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        _blocksGUI.displayGUI(player, gui);
      }
    });

    gui.setClickActions(actions);
    gui.open(p);
  }

  private ItemStack _createEnabledItem() {
    boolean state = _settingsManager.getVeinChopper();
    ItemStack item = new ItemStack(Material.DIAMOND_AXE);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Enabled"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }
    return item;
  }

  private ItemStack _createMaxSizeItem() {
    int val = _settingsManager.getVeinChopperMaxVeinSize();
    ItemStack item = new ItemStack(Material.PAPER);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Max Vein Size"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Current: " + ChatColor.YELLOW + val,
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Set value")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }
    return item;
  }

  private ItemStack _createSoundItem() {
    boolean state = _settingsManager.getVeinChopperSound();
    ItemStack item = new ItemStack(Material.NOTE_BLOCK);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Sound"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }
    return item;
  }

  private ItemStack _createToolsItem() {
    ItemStack item = new ItemStack(Material.IRON_AXE);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Allowed Tools"));
      meta.lore(List.of(Component.text(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Open")));
      item.setItemMeta(meta);
    }
    return item;
  }

  private ItemStack _createBlocksItem() {
    ItemStack item = new ItemStack(Material.OAK_LOG);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Allowed Blocks"));
      meta.lore(List.of(Component.text(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Open")));
      item.setItemMeta(meta);
    }
    return item;
  }

  @EventHandler
  public void onPlayerChat(AsyncChatEvent e) {
    Player p = e.getPlayer();
    UUID id = p.getUniqueId();

    if (_sizePending.containsKey(id)) {
      e.setCancelled(true);
      String content = ((TextComponent) e.message()).content();

      try {
        int val = Integer.parseInt(content);
        if (val < 1 || val > 1024) {
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "Value must be between 1 and 1024.");
          return;
        }

        _plugin.getServer().getScheduler().runTask(_plugin, () -> {
          _settingsManager.setVeinChopperMaxVeinSize(val);
          p.sendMessage(Main.getPrefix() + "Max vein size set to: " + ChatColor.YELLOW + val);
          p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
          CustomGUI parent = _sizePending.remove(id);
          displayGUI(p, parent);
        });
      } catch (NumberFormatException ex) {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please provide a valid number.");
      }
    }
  }
}
