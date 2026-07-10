package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

  private final Main _plugin;
  private final SettingsManager _settingsManager;

  public ThemeSettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
  }

  public void displayGUI(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    Map<String, ItemStack> entries = new HashMap<>();
    entries.put("primaryFontColor", _createColorItem(
        Material.LIGHT_GRAY_DYE,
        "Primary Font Color",
        "The color used for normal text.",
        _settingsManager.getPrimaryFontColor(),
        Theme.primary()));
    entries.put("highlightFontColor", _createColorItem(
        Material.YELLOW_DYE,
        "Highlight Font Color",
        "The color used for highlighted text and values.",
        _settingsManager.getHighlightFontColor(),
        Theme.highlight()));
    entries.put("errorFontColor", _createColorItem(
        Material.RED_DYE,
        "Error Font Color",
        "The color used for errors and warnings.",
        _settingsManager.getErrorFontColor(),
        Theme.error()));
    entries.put("titleSymbolColor", _createColorItem(
        Material.REDSTONE,
        "Title Symbol Color",
        "The color used for symbols before titles.",
        _settingsManager.getTitleSymbolColor(),
        Theme.titleSymbol()));
    entries.put("textSymbolColor", _createColorItem(
        Material.GLOWSTONE_DUST,
        "Text Symbol Color",
        "The color used for symbols before normal text.",
        _settingsManager.getTextSymbolColor(),
        Theme.textSymbol()));
    entries.put("glassPaneColor", _createColorItem(
        Theme.glassPaneMaterial(),
        "Glass Pane Color",
        "The glass pane color used in the last GUI row.",
        _settingsManager.getGlassPaneColor(),
        Theme.glassPaneColor()));
    entries.put("name", _createNameItem());

    Map<String, Integer> customSlots = new HashMap<>();
    customSlots.put("primaryFontColor", 1);
    customSlots.put("highlightFontColor", 4);
    customSlots.put("errorFontColor", 7);
    customSlots.put("titleSymbolColor", 10);
    customSlots.put("textSymbolColor", 13);
    customSlots.put("glassPaneColor", 16);
    customSlots.put("name", 22);

    CustomGUI gui = new CustomGUI(_plugin, p,
        Theme.titlePrefix() + "Theming",
        entries, 4, customSlots, parentMenu,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    if (backAction != null) {
      gui.setBackAction(backAction);
    }

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    actions.put("primaryFontColor", _colorAction(
        "Primary Font Color",
        _settingsManager.getPrimaryFontColor(),
        _settingsManager::setPrimaryFontColor,
        parentMenu, backAction));
    actions.put("highlightFontColor", _colorAction(
        "Highlight Font Color",
        _settingsManager.getHighlightFontColor(),
        _settingsManager::setHighlightFontColor,
        parentMenu, backAction));
    actions.put("errorFontColor", _colorAction(
        "Error Font Color",
        _settingsManager.getErrorFontColor(),
        _settingsManager::setErrorFontColor,
        parentMenu, backAction));
    actions.put("titleSymbolColor", _colorAction(
        "Title Symbol Color",
        _settingsManager.getTitleSymbolColor(),
        _settingsManager::setTitleSymbolColor,
        parentMenu, backAction));
    actions.put("textSymbolColor", _colorAction(
        "Text Symbol Color",
        _settingsManager.getTextSymbolColor(),
        _settingsManager::setTextSymbolColor,
        parentMenu, backAction));
    actions.put("glassPaneColor", _colorAction(
        "Glass Pane Color",
        _settingsManager.getGlassPaneColor(),
        _settingsManager::setGlassPaneColor,
        parentMenu, backAction));
    actions.put("name", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        _openNameDialog(player, parentMenu, backAction, null, _settingsManager.getThemeName());
      }
    });

    gui.setClickActions(actions);
    gui.open(p);
  }

  private CustomGUI.ClickAction _colorAction(String title, String currentValue, Consumer<String> setter,
      CustomGUI parentMenu, Consumer<Player> backAction) {
    return new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _openColorDialog(p, title, currentValue, setter, parentMenu, backAction);
      }
    };
  }

  private ItemStack _createColorItem(Material material, String title, String description, String configuredValue,
      ChatColor previewColor) {
    ItemStack item = new ItemStack(material);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(Theme.titlePrefix() + title));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + description,
          "",
          Theme.textPrefix() + "Current: " + Theme.highlight() + configuredValue,
          Theme.textPrefix() + "Preview: " + previewColor + "Sample Text",
          "",
          Theme.textPrefix() + "Left-Click: Set value")
          .stream().filter(Objects::nonNull).map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createNameItem() {
    ItemStack item = new ItemStack(Material.NAME_TAG);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(Theme.titlePrefix() + "Name"));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + "The name used in plugin messages and branding.",
          "",
          Theme.textPrefix() + "Current: " + Theme.highlight() + Theme.name(),
          "",
          Theme.textPrefix() + "Left-Click: Set value")
          .stream().filter(Objects::nonNull).map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private void _openColorDialog(Player p, String title, String initialValue, Consumer<String> setter,
      CustomGUI parentMenu, Consumer<Player> backAction) {
    DialogInput input = CustomDialog.createSelectInput(
        "color",
        Theme.textPrefix() + title,
        _buildMinecraftColorOptions(),
        initialValue);

    Dialog dialog = CustomDialog.createConfirmationDialog(
        title,
        "Select a Minecraft color.",
        null,
        List.of(input),
        (view, audience) -> _setColorDialogCB(view, audience, title, setter, parentMenu, backAction),
        null);

    p.showDialog(dialog);
  }

  private void _setColorDialogCB(DialogResponseView view, Audience audience, String title, Consumer<String> setter,
      CustomGUI parentMenu, Consumer<Player> backAction) {
    Player p = (Player) audience;
    String colorKey = view.getText("color");
    setter.accept(colorKey);
    _applyThemeChange();
    ChatColor selectedColor = Theme.minecraftColors().get(colorKey);
    p.sendMessage(Component.text(Main.getPrefix() + title + " set to " + selectedColor + colorKey + "."));
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
    displayGUI(p, parentMenu, backAction);
  }

  private Map<String, String> _buildMinecraftColorOptions() {
    Map<String, String> options = new LinkedHashMap<>();
    for (Map.Entry<String, ChatColor> entry : Theme.minecraftColors().entrySet()) {
      options.put(entry.getKey(), entry.getValue() + _formatColorName(entry.getKey()));
    }

    return options;
  }

  private String _formatColorName(String name) {
    return Arrays.stream(name.split("_"))
        .map(part -> part.substring(0, 1) + part.substring(1).toLowerCase())
        .collect(Collectors.joining(" "));
  }

  private void _openNameDialog(Player p, CustomGUI parentMenu, Consumer<Player> backAction, String errorMessage,
      String initialValue) {
    DialogInput input = CustomDialog.createTextInput(
        "name",
        Theme.textPrefix() + "Name",
        initialValue);

    Dialog dialog = CustomDialog.createConfirmationDialog(
        "Theme Name",
        "Set the name displayed in plugin messages and branding.",
        errorMessage,
        List.of(input),
        (view, audience) -> _setNameDialogCB(view, audience, parentMenu, backAction),
        null);

    p.showDialog(dialog);
  }

  private void _setNameDialogCB(DialogResponseView view, Audience audience, CustomGUI parentMenu,
      Consumer<Player> backAction) {
    Player p = (Player) audience;
    String name = Optional.ofNullable(view.getText("name")).map(String::trim).orElse("");

    if (name.isEmpty()) {
      _openNameDialog(p, parentMenu, backAction, "Name cannot be empty.", name);
      return;
    }

    if (name.length() > MAX_NAME_LENGTH) {
      _openNameDialog(p, parentMenu, backAction,
          "Name is too long. Maximum length is " + MAX_NAME_LENGTH + " characters.", name);
      return;
    }

    if (name.indexOf(ChatColor.COLOR_CHAR) >= 0 || name.matches("(?i).*&[0-9A-FK-ORX].*")
        || name.contains("\n") || name.contains("\r")) {
      _openNameDialog(p, parentMenu, backAction, "Name cannot contain color codes or line breaks.", name);
      return;
    }

    _settingsManager.setThemeName(name);
    _applyThemeChange();
    p.sendMessage(Component.text(Main.getPrefix() + "Theme name set to " + Theme.highlight() + Theme.name() + "."));
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
