package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.CustomDialog;
import com.daveestar.bettervanilla.utils.CustomGUI;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class VeinMinerSettingsGUI {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final MaterialToggleGUI _toolsGUI;
  private final MaterialToggleGUI _blocksGUI;

  private MaterialToggleGUI _createToggleGUI(String title,
      List<Material> defaults,
      Supplier<List<String>> getter,
      Consumer<List<String>> setter) {
    return new MaterialToggleGUI(title, defaults, getter, setter);
  }

  public VeinMinerSettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();

    _toolsGUI = _createToggleGUI("Vein Miner Tools",
        SettingsManager.VEIN_MINER_TOOLS,
        _settingsManager::getVeinMinerAllowedTools,
        _settingsManager::setVeinMinerAllowedTools);

    _blocksGUI = _createToggleGUI("Vein Miner Blocks",
        SettingsManager.VEIN_MINER_BLOCKS,
        _settingsManager::getVeinMinerAllowedBlocks,
        _settingsManager::setVeinMinerAllowedBlocks);
  }

  public void displayGUI(Player p, CustomGUI parent, Consumer<Player> backAction) {
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
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Vein Miner Settings",
        entries, 2, customSlots, parent,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    if (backAction != null) {
      gui.setBackAction(backAction);
    }

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    actions.put("tools", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toolsGUI.displayGUI(p, gui);
      }
    });
    actions.put("maxsize", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _openVeinMinerMaxSizeDialog(p, parent, backAction);
      }
    });
    actions.put("enabled", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        boolean newState = !_settingsManager.getVeinMinerEnabled();
        _settingsManager.setVeinMinerEnabled(newState);
        displayGUI(p, parent, backAction);
      }
    });
    actions.put("sound", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        boolean newState = !_settingsManager.getVeinMinerSound();
        _settingsManager.setVeinMinerSound(newState);
        displayGUI(p, parent, backAction);
      }
    });

    actions.put("blocks", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _blocksGUI.displayGUI(p, gui);
      }
    });

    gui.setClickActions(actions);
    gui.open(p);
  }

  private ItemStack _createEnabledItem() {
    boolean state = _settingsManager.getVeinMinerEnabled();
    ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Vein Miner"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Toggle global vein miner state.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createMaxSizeItem() {
    int veinSize = _settingsManager.getVeinMinerMaxVeinSize();
    ItemStack item = new ItemStack(Material.PAPER);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Maximum Vein Size"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Set the maximum size of veins that can be mined.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Current: " + ChatColor.YELLOW + veinSize,
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Set value")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createSoundItem() {
    boolean state = _settingsManager.getVeinMinerSound();
    ItemStack item = new ItemStack(Material.NOTE_BLOCK);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Sound"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Toggle sound effects for vein miner.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createToolsItem() {
    ItemStack item = new ItemStack(Material.IRON_PICKAXE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Allowed Tools"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Open menu to manage allowed tools.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Open")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createBlocksItem() {
    ItemStack item = new ItemStack(Material.COAL_ORE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Allowed Blocks"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Open menu to manage allowed blocks.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Open")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  // -------
  // DIALOGS
  // -------

  private void _openVeinMinerMaxSizeDialog(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    int size = _settingsManager.getVeinMinerMaxVeinSize();

    DialogInput inputSize = CustomDialog.createNumberInput("size",
        ChatColor.YELLOW + "» " + ChatColor.GRAY + "Maximum Vein Miner Size", 1,
        1024, 1, (float) size);

    Dialog dialog = CustomDialog.createConfirmationDialog(
        "Set Maximum Vein Miner Size",
        "Set the maximum size of veins that can be mined.",
        null,
        List.of(inputSize),
        (view, audience) -> _setVeinMinerMaxSizeDialogCB(view, audience, parentMenu, backAction),
        null);

    p.showDialog(dialog);
  }

  // ----------------
  // DIALOG CALLBACKS
  // ----------------

  private void _setVeinMinerMaxSizeDialogCB(DialogResponseView view, Audience audience, CustomGUI parentMenu,
      Consumer<Player> backAction) {
    Player p = (Player) audience;
    int veinSize = Math.round(view.getFloat("size"));

    _settingsManager.setVeinMinerMaxVeinSize(veinSize);

    p.sendMessage(Main.getPrefix() + "Maximum vein miner size set to: " + ChatColor.YELLOW + veinSize);
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

    displayGUI(p, parentMenu, backAction);
  }
}
