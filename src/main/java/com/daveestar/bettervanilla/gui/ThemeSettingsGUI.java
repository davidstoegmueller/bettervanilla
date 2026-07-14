package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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
import net.md_5.bungee.api.ChatColor;

public class ThemeSettingsGUI {
  private static final int MAX_NAME_LENGTH = 32;
  private static final String KEY_RESET_THEME = "resetTheme";

  private final Main _plugin;
  private final SettingsManager _settingsManager;

  public ThemeSettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
  }

  public void displayGUI(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    Map<String, ItemStack> entries = new HashMap<>();
    entries.put("primaryFontColor", _createColorItem(p,
        Material.LIGHT_GRAY_DYE,
        "gui-theme-primary-font-color-title",
        "gui-theme-primary-font-color-description",
        _settingsManager.getPrimaryFontColor(),
        Theme.primary(),
        Main.tr(p, "gui-theme-color-preview-sample")));
    entries.put("highlightFontColor", _createColorItem(p,
        Material.YELLOW_DYE,
        "gui-theme-highlight-font-color-title",
        "gui-theme-highlight-font-color-description",
        _settingsManager.getHighlightFontColor(),
        Theme.highlight(),
        Main.tr(p, "gui-theme-color-preview-sample")));
    entries.put("errorFontColor", _createColorItem(p,
        Material.RED_DYE,
        "gui-theme-error-font-color-title",
        "gui-theme-error-font-color-description",
        _settingsManager.getErrorFontColor(),
        Theme.error(),
        Main.tr(p, "gui-theme-color-preview-sample")));
    entries.put("titleSymbolColor", _createColorItem(p,
        Material.REDSTONE,
        "gui-theme-title-symbol-color-title",
        "gui-theme-title-symbol-color-description",
        _settingsManager.getTitleSymbolColor(),
        Theme.titleSymbol(),
        ChatColor.BOLD + "»"));
    entries.put("textSymbolColor", _createColorItem(p,
        Material.GLOWSTONE_DUST,
        "gui-theme-text-symbol-color-title",
        "gui-theme-text-symbol-color-description",
        _settingsManager.getTextSymbolColor(),
        Theme.textSymbol(),
        "»"));
    entries.put("glassPaneColor", _createColorItem(p,
        Theme.glassPaneMaterial(),
        "gui-theme-glass-pane-color-title",
        "gui-theme-glass-pane-color-description",
        _settingsManager.getGlassPaneColor(),
        Theme.glassPaneColor(),
        Main.tr(p, "gui-theme-color-preview-sample")));
    entries.put("name", _createNameItem(p));
    entries.put(KEY_RESET_THEME, _createResetItem(p));

    Map<String, Integer> customSlots = new HashMap<>();
    customSlots.put("primaryFontColor", 1);
    customSlots.put("highlightFontColor", 4);
    customSlots.put("errorFontColor", 7);
    customSlots.put("titleSymbolColor", 10);
    customSlots.put("textSymbolColor", 13);
    customSlots.put("glassPaneColor", 16);
    customSlots.put("name", 22);
    customSlots.put(KEY_RESET_THEME, 31);

    CustomGUI gui = new CustomGUI(_plugin, p,
        Theme.titlePrefix() + Main.tr(p, "gui-theme-title"),
        entries, 4, customSlots, parentMenu,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    if (backAction != null) {
      gui.setBackAction(backAction);
    }

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    actions.put("primaryFontColor", _colorAction(
        "gui-theme-primary-font-color-title",
        _settingsManager.getPrimaryFontColor(),
        _settingsManager::setPrimaryFontColor,
        parentMenu, backAction));
    actions.put("highlightFontColor", _colorAction(
        "gui-theme-highlight-font-color-title",
        _settingsManager.getHighlightFontColor(),
        _settingsManager::setHighlightFontColor,
        parentMenu, backAction));
    actions.put("errorFontColor", _colorAction(
        "gui-theme-error-font-color-title",
        _settingsManager.getErrorFontColor(),
        _settingsManager::setErrorFontColor,
        parentMenu, backAction));
    actions.put("titleSymbolColor", _colorAction(
        "gui-theme-title-symbol-color-title",
        _settingsManager.getTitleSymbolColor(),
        _settingsManager::setTitleSymbolColor,
        parentMenu, backAction));
    actions.put("textSymbolColor", _colorAction(
        "gui-theme-text-symbol-color-title",
        _settingsManager.getTextSymbolColor(),
        _settingsManager::setTextSymbolColor,
        parentMenu, backAction));
    actions.put("glassPaneColor", _colorAction(
        "gui-theme-glass-pane-color-title",
        _settingsManager.getGlassPaneColor(),
        _settingsManager::setGlassPaneColor,
        parentMenu, backAction));
    actions.put("name", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        _openNameDialog(player, parentMenu, backAction, null, _settingsManager.getThemeName());
      }
    });
    actions.put(KEY_RESET_THEME, new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        _settingsManager.resetTheme();
        _applyThemeChange();
        player.sendMessage(Component.text(Main.getPrefix() + Main.tr(player, "gui-theme-reset-message")));
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
        displayGUI(player, parentMenu, backAction);
      }
    });

    gui.setClickActions(actions);
    gui.open(p);
  }

  private CustomGUI.ClickAction _colorAction(String titleKey, String currentValue, Consumer<String> setter,
      CustomGUI parentMenu, Consumer<Player> backAction) {
    return new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _openColorDialog(p, titleKey, currentValue, setter, parentMenu, backAction);
      }
    };
  }

  private ItemStack _createColorItem(Player viewer, Material material, String titleKey, String descriptionKey,
      String configuredValue, ChatColor previewColor, String previewSample) {
    ItemStack item = new ItemStack(material);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(Theme.titlePrefix() + Main.tr(viewer, titleKey)));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + Main.tr(viewer, descriptionKey),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-common-current-value",
              "value", Theme.highlight() + _translatedColorName(viewer, configuredValue)),
          Theme.textPrefix() + Main.tr(viewer, "gui-theme-color-preview",
              "sample", previewColor + previewSample),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-common-action-set-value"))
          .stream().filter(Objects::nonNull).map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createNameItem(Player viewer) {
    ItemStack item = new ItemStack(Material.NAME_TAG);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(Theme.titlePrefix() + Main.tr(viewer, "gui-theme-name-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + Main.tr(viewer, "gui-theme-name-description"),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-common-current-value",
              "value", Theme.highlight() + Theme.name()),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-common-action-set-value"))
          .stream().filter(Objects::nonNull).map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createResetItem(Player viewer) {
    ItemStack item = new ItemStack(Material.BARRIER);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(Theme.titlePrefix() + Main.tr(viewer, "gui-theme-reset-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + Main.tr(viewer, "gui-theme-reset-description"),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-theme-reset-action"))
          .stream().map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private void _openColorDialog(Player p, String titleKey, String initialValue, Consumer<String> setter,
      CustomGUI parentMenu, Consumer<Player> backAction) {
    DialogInput input = CustomDialog.createSelectInput(
        "color",
        Theme.textPrefix() + Main.tr(p, titleKey),
        _buildMinecraftColorOptions(p),
        initialValue);

    Dialog dialog = CustomDialog.createConfirmationDialog(
        Main.tr(p, titleKey),
        Main.tr(p, "gui-theme-color-dialog-description"),
        null,
        List.of(input),
        (view, audience) -> _setColorDialogCB(view, audience, titleKey, setter, parentMenu, backAction),
        null,
        Main.tr(p, "dialog-button-apply"), Main.tr(p, "dialog-button-cancel"));

    p.showDialog(dialog);
  }

  private void _setColorDialogCB(DialogResponseView view, Audience audience, String titleKey,
      Consumer<String> setter,
      CustomGUI parentMenu, Consumer<Player> backAction) {
    Player p = (Player) audience;
    String colorKey = view.getText("color");
    ChatColor selectedColor = Theme.minecraftColors().get(colorKey);
    if (colorKey == null || selectedColor == null) {
      p.sendMessage(Component.text(Main.getPrefix() + Theme.error()
          + Main.tr(p, "gui-theme-error-invalid-color")));
      displayGUI(p, parentMenu, backAction);
      return;
    }

    setter.accept(colorKey);
    _applyThemeChange();
    p.sendMessage(Component.text(Main.getPrefix() + Main.tr(p, "gui-theme-color-changed-message",
        "setting", Main.tr(p, titleKey),
        "color", selectedColor + _translatedColorName(p, colorKey) + Theme.primary())));
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
    displayGUI(p, parentMenu, backAction);
  }

  private Map<String, String> _buildMinecraftColorOptions(Player viewer) {
    Map<String, String> options = new LinkedHashMap<>();
    for (Map.Entry<String, ChatColor> entry : Theme.minecraftColors().entrySet()) {
      options.put(entry.getKey(), entry.getValue() + _translatedColorName(viewer, entry.getKey()));
    }

    return options;
  }

  private String _translatedColorName(Player viewer, String colorKey) {
    String key = "enum-chat-color-" + colorKey.toLowerCase(Locale.ROOT).replace('_', '-');
    return Main.tr(viewer, key);
  }

  private void _openNameDialog(Player p, CustomGUI parentMenu, Consumer<Player> backAction, String errorMessage,
      String initialValue) {
    DialogInput input = CustomDialog.createTextInput(
        "name",
        Theme.textPrefix() + Main.tr(p, "gui-theme-name-dialog-input"),
        initialValue);

    Dialog dialog = CustomDialog.createConfirmationDialog(
        Main.tr(p, "gui-theme-name-dialog-title"),
        Main.tr(p, "gui-theme-name-dialog-description"),
        errorMessage,
        List.of(input),
        (view, audience) -> _setNameDialogCB(view, audience, parentMenu, backAction),
        null,
        Main.tr(p, "dialog-button-apply"), Main.tr(p, "dialog-button-cancel"));

    p.showDialog(dialog);
  }

  private void _setNameDialogCB(DialogResponseView view, Audience audience, CustomGUI parentMenu,
      Consumer<Player> backAction) {
    Player p = (Player) audience;
    String name = Optional.ofNullable(view.getText("name")).map(String::trim).orElse("");

    if (name.isEmpty()) {
      _openNameDialog(p, parentMenu, backAction, Main.tr(p, "gui-theme-name-error-empty"), name);
      return;
    }

    if (name.length() > MAX_NAME_LENGTH) {
      _openNameDialog(p, parentMenu, backAction, Main.tr(p, "gui-theme-name-error-too-long",
          "max", Integer.toString(MAX_NAME_LENGTH)), name);
      return;
    }

    if (name.indexOf(ChatColor.COLOR_CHAR) >= 0 || name.matches("(?i).*&[0-9A-FK-ORX].*")
        || name.contains("\n") || name.contains("\r")) {
      _openNameDialog(p, parentMenu, backAction, Main.tr(p, "gui-theme-name-error-invalid-characters"), name);
      return;
    }

    _settingsManager.setThemeName(name);
    _applyThemeChange();
    p.sendMessage(Component.text(Main.getPrefix() + Main.tr(p, "gui-theme-name-changed-message",
        "name", Theme.highlight() + Theme.name() + Theme.primary())));
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
    displayGUI(p, parentMenu, backAction);
  }

  private void _applyThemeChange() {
    for (Player online : _plugin.getServer().getOnlinePlayers()) {
      _plugin.getNameTagManager().updateNameTag(online);
      _plugin.getTabListManager().refreshPlayer(online);
    }

    _plugin.refreshCraftingRecipes();
  }
}
