package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;

public class VeinChopperSettingsGUI {
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

  public VeinChopperSettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();

    _toolsGUI = _createToggleGUI("gui-vein-chopper-tools-title",
        SettingsManager.VEIN_CHOPPER_TOOLS,
        _settingsManager::getVeinChopperAllowedTools,
        _settingsManager::setVeinChopperAllowedTools);

    _blocksGUI = _createToggleGUI("gui-vein-chopper-blocks-title",
        SettingsManager.VEIN_CHOPPER_BLOCKS,
        _settingsManager::getVeinChopperAllowedBlocks,
        _settingsManager::setVeinChopperAllowedBlocks);
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
        Theme.titlePrefix() + Main.tr(p, "gui-vein-chopper-settings-title"),
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
        _openVeinChopperMaxSizeDialog(p, parent, backAction);
      }
    });
    actions.put("enabled", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        boolean newState = !_settingsManager.getVeinChopperEnabled();
        _settingsManager.setVeinChopperEnabled(newState);
        displayGUI(p, parent, backAction);
      }
    });
    actions.put("sound", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        boolean newState = !_settingsManager.getVeinChopperSound();
        _settingsManager.setVeinChopperSound(newState);
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
    boolean state = _settingsManager.getVeinChopperEnabled();
    ItemStack item = new ItemStack(Material.DIAMOND_AXE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(Component.text(Theme.titlePrefix() + Main.tr(viewer, "gui-vein-chopper-enabled-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + Main.tr(viewer, "gui-vein-chopper-enabled-description"),
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
    int veinSize = _settingsManager.getVeinChopperMaxVeinSize();
    ItemStack item = new ItemStack(Material.PAPER);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + Main.tr(viewer, "gui-vein-chopper-max-size-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + Main.tr(viewer, "gui-vein-chopper-max-size-description"),
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
    boolean state = _settingsManager.getVeinChopperSound();
    ItemStack item = new ItemStack(Material.NOTE_BLOCK);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(Theme.titlePrefix() + Main.tr(viewer, "gui-vein-chopper-sound-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + Main.tr(viewer, "gui-vein-chopper-sound-description"),
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
    ItemStack item = new ItemStack(Material.IRON_AXE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(Component.text(Theme.titlePrefix() + Main.tr(viewer, "gui-vein-chopper-tools-item-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + Main.tr(viewer, "gui-vein-chopper-tools-description"),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-common-action-open")).stream().filter(Objects::nonNull)
          .map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createBlocksItem(Player viewer) {
    ItemStack item = new ItemStack(Material.OAK_LOG);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + Main.tr(viewer, "gui-vein-chopper-blocks-item-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + Main.tr(viewer, "gui-vein-chopper-blocks-description"),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-common-action-open")).stream().filter(Objects::nonNull)
          .map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  // -------
  // DIALOGS
  // -------

  private void _openVeinChopperMaxSizeDialog(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    int size = _settingsManager.getVeinChopperMaxVeinSize();

    DialogInput inputSize = CustomDialog.createNumberInput("size",
        Theme.textPrefix() + Main.tr(p, "gui-vein-chopper-max-size-dialog-input"), 1, 1024, 1, (float) size);

    Dialog dialog = CustomDialog.createConfirmationDialog(
        Main.tr(p, "gui-vein-chopper-max-size-dialog-title"),
        Main.tr(p, "gui-vein-chopper-max-size-dialog-description"),
        null,
        List.of(inputSize),
        (view, audience) -> _setVeinChopperMaxSizeDialogCB(view, audience, parentMenu, backAction),
        null,
        Main.tr(p, "dialog-button-apply"), Main.tr(p, "dialog-button-cancel"));

    p.showDialog(dialog);
  }

  // ----------------
  // DIALOG CALLBACKS
  // ----------------

  private void _setVeinChopperMaxSizeDialogCB(DialogResponseView view, Audience audience, CustomGUI parentMenu,
      Consumer<Player> backAction) {
    Player p = (Player) audience;
    int veinSize = Math.round(view.getFloat("size"));

    _settingsManager.setVeinChopperMaxVeinSize(veinSize);

    p.sendMessage(Main.getPrefix() + Main.tr(p, "gui-vein-chopper-max-size-changed",
        "size", Theme.highlight() + Integer.toString(veinSize)));
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

    displayGUI(p, parentMenu, backAction);
  }

  private String _stateText(Player viewer, boolean state) {
    return (state ? Theme.highlight() : Theme.error())
        + Main.tr(viewer, state ? "common-state-enabled" : "common-state-disabled");
  }
}
