package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
    entries.put("tools", _createToolsItem());
    entries.put("maxsize", _createMaxSizeItem());
    entries.put("enabled", _createEnabledItem());
    entries.put("sound", _createSoundItem());
    entries.put("blocks", _createBlocksItem());

    Map<String, Integer> customSlots = new HashMap<>();
    customSlots.put("tools", 0);
    customSlots.put("maxsize", 2);
    customSlots.put("enabled", 4);
    customSlots.put("sound", 6);
    customSlots.put("blocks", 8);

    CustomGUI gui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Vein Chopper Settings",
        entries, 2, customSlots, parent,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    actions.put("tools", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        _toolsGUI.displayGUI(player, gui);
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
    actions.put("enabled", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        boolean newState = !_settingsManager.getVeinChopperEnabled();
        _settingsManager.setVeinChopperEnabled(newState);
        displayGUI(player, parent);
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
    boolean state = _settingsManager.getVeinChopperEnabled();
    ItemStack item = new ItemStack(Material.DIAMOND_AXE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Vein Chopper"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Toggle global vein miner state.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createMaxSizeItem() {
    int veinSize = _settingsManager.getVeinChopperMaxVeinSize();
    ItemStack item = new ItemStack(Material.PAPER);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Maximum Vein Size"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Set the maximum size of veins that can be chopped.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Current: " + ChatColor.YELLOW + veinSize,
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
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Toggle sound effects for vein chopper.",
          "",
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
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Open menu to manage allowed tools.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Open").stream().filter(Objects::nonNull)
          .map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createBlocksItem() {
    ItemStack item = new ItemStack(Material.OAK_LOG);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Allowed Blocks"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Open menu to manage allowed blocks.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Open").stream().filter(Objects::nonNull)
          .map(Component::text).collect(Collectors.toList()));
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
        int veinSize = Integer.parseInt(content);
        if (veinSize < 1 || veinSize > 1024) {
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "Value must be between 1 and 1024.");
          return;
        }

        _plugin.getServer().getScheduler().runTask(_plugin, () -> {
          _settingsManager.setVeinChopperMaxVeinSize(veinSize);

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
