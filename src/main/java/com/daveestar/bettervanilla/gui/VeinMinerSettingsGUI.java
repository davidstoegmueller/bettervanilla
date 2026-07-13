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
import com.daveestar.bettervanilla.utils.Theme;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class VeinMinerSettingsGUI {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final MaterialToggleGUI _toolsGUI;
  private final MaterialToggleGUI _blocksGUI;

  private MaterialToggleGUI _createToggleGUI(String titleKey,
      List<Material> defaults,
      Supplier<List<String>> getter,
      Consumer<List<String>> setter) {
    return new MaterialToggleGUI(titleKey, defaults, getter, setter);
  }

  public VeinMinerSettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();

    _toolsGUI = _createToggleGUI("gui-vein-miner-tools-title",
        SettingsManager.VEIN_MINER_TOOLS,
        _settingsManager::getVeinMinerAllowedTools,
        _settingsManager::setVeinMinerAllowedTools);

    _blocksGUI = _createToggleGUI("gui-vein-miner-blocks-title",
        SettingsManager.VEIN_MINER_BLOCKS,
        _settingsManager::getVeinMinerAllowedBlocks,
        _settingsManager::setVeinMinerAllowedBlocks);
  }

  public void displayGUI(Player p, CustomGUI parent, Consumer<Player> backAction) {
    Map<String, ItemStack> entries = new HashMap<>();
    entries.put("tools", _createToolsItem(p));
    entries.put("maxsize", _createMaxSizeItem(p));
    entries.put("enabled", _createEnabledItem(p));
    entries.put("sound", _createSoundItem(p));
    entries.put("blocks", _createBlocksItem(p));

    Map<String, Integer> customSlots = new HashMap<>();
    customSlots.put("tools", 0);
    customSlots.put("maxsize", 2);
    customSlots.put("enabled", 4);
    customSlots.put("sound", 6);
    customSlots.put("blocks", 8);

    CustomGUI gui = new CustomGUI(_plugin, p,
        Theme.titlePrefix() + Main.tr(p, "gui-vein-miner-settings-title"),
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

  private ItemStack _createEnabledItem(Player viewer) {
    boolean state = _settingsManager.getVeinMinerEnabled();
    ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(Component.text(Theme.titlePrefix() + Main.tr(viewer, "gui-vein-miner-enabled-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + Main.tr(viewer, "gui-vein-miner-enabled-description"),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-common-state", "state", _stateText(viewer, state)),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-common-action-toggle"))
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createMaxSizeItem(Player viewer) {
    int veinSize = _settingsManager.getVeinMinerMaxVeinSize();
    ItemStack item = new ItemStack(Material.PAPER);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + Main.tr(viewer, "gui-vein-miner-max-size-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + Main.tr(viewer, "gui-vein-miner-max-size-description"),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-common-current-value",
              "value", Theme.highlight() + Integer.toString(veinSize)),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-common-action-set-value"))
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createSoundItem(Player viewer) {
    boolean state = _settingsManager.getVeinMinerSound();
    ItemStack item = new ItemStack(Material.NOTE_BLOCK);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(Theme.titlePrefix() + Main.tr(viewer, "gui-vein-miner-sound-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + Main.tr(viewer, "gui-vein-miner-sound-description"),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-common-state", "state", _stateText(viewer, state)),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-common-action-toggle"))
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createToolsItem(Player viewer) {
    ItemStack item = new ItemStack(Material.IRON_PICKAXE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(Component.text(Theme.titlePrefix() + Main.tr(viewer, "gui-vein-miner-tools-item-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + Main.tr(viewer, "gui-vein-miner-tools-description"),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-common-action-open"))
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createBlocksItem(Player viewer) {
    ItemStack item = new ItemStack(Material.COAL_ORE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + Main.tr(viewer, "gui-vein-miner-blocks-item-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + Main.tr(viewer, "gui-vein-miner-blocks-description"),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-common-action-open"))
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
        Theme.textPrefix() + Main.tr(p, "gui-vein-miner-max-size-dialog-input"), 1,
        1024, 1, (float) size);

    Dialog dialog = CustomDialog.createConfirmationDialog(
        Main.tr(p, "gui-vein-miner-max-size-dialog-title"),
        Main.tr(p, "gui-vein-miner-max-size-dialog-description"),
        null,
        List.of(inputSize),
        (view, audience) -> _setVeinMinerMaxSizeDialogCB(view, audience, parentMenu, backAction),
        null,
        Main.tr(p, "dialog-button-apply"), Main.tr(p, "dialog-button-cancel"));

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

    p.sendMessage(Main.getPrefix() + Main.tr(p, "gui-vein-miner-max-size-changed",
        "size", Theme.highlight() + Integer.toString(veinSize)));
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

    displayGUI(p, parentMenu, backAction);
  }

  private String _stateText(Player viewer, boolean state) {
    return (state ? Theme.highlight() : Theme.error())
        + Main.tr(viewer, state ? "common-state-enabled" : "common-state-disabled");
  }
}
