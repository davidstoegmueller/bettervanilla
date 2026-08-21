package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.CustomGUI;
import com.daveestar.bettervanilla.utils.Theme;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class MobProtectionsSettingsGUI {
  private final Main _plugin;
  private final SettingsManager _settingsManager;

  public MobProtectionsSettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
  }

  public void displayGUI(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    Map<String, ItemStack> entries = new HashMap<>();
    entries.put("creeperblockdamage", _createToggleItem(p, Material.CREEPER_HEAD,
        "admin-creeper-block-damage-title", "admin-creeper-block-damage-description",
        _settingsManager.getCreeperBlockDamage()));
    entries.put("creeperentitydamage", _createToggleItem(p, Material.TNT,
        "admin-creeper-entity-damage-title", "admin-creeper-entity-damage-description",
        _settingsManager.getCreeperEntityDamage()));
    entries.put("endermanblocksteal", _createToggleItem(p, Material.GRASS_BLOCK,
        "admin-enderman-block-steal-title", "admin-enderman-block-steal-description",
        _settingsManager.getEndermanBlockSteal()));

    Map<String, Integer> customSlots = new HashMap<>();
    customSlots.put("creeperblockdamage", 2);
    customSlots.put("creeperentitydamage", 4);
    customSlots.put("endermanblocksteal", 6);

    CustomGUI gui = new CustomGUI(_plugin, p,
        Theme.titlePrefix() + Main.tr(p, "gui-protections-settings-title"),
        entries, 2, customSlots, parentMenu,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    if (backAction != null) {
      gui.setBackAction(backAction);
    }

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    actions.put("creeperblockdamage", _toggleAction(parentMenu, backAction,
        _settingsManager.getCreeperBlockDamage(), _settingsManager::setCreeperBlockDamage,
        "admin-creeper-block-damage-toggle-message"));
    actions.put("creeperentitydamage", _toggleAction(parentMenu, backAction,
        _settingsManager.getCreeperEntityDamage(), _settingsManager::setCreeperEntityDamage,
        "admin-creeper-entity-damage-toggle-message"));
    actions.put("endermanblocksteal", _toggleAction(parentMenu, backAction,
        _settingsManager.getEndermanBlockSteal(), _settingsManager::setEndermanBlockSteal,
        "admin-enderman-block-steal-toggle-message"));

    gui.setClickActions(actions);
    gui.open(p);
  }

  private CustomGUI.ClickAction _toggleAction(CustomGUI parentMenu, Consumer<Player> backAction,
      boolean currentState, Consumer<Boolean> setter, String messageKey) {
    return new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        boolean newState = !currentState;
        setter.accept(newState);
        p.sendMessage(Main.getPrefix() + Main.tr(p, messageKey, "state",
            Theme.highlight().toString() + ChatColor.BOLD
                + Main.tr(p, newState ? "common-state-enabled" : "common-state-disabled")));
        displayGUI(p, parentMenu, backAction);
      }
    };
  }

  private ItemStack _createToggleItem(Player viewer, Material material, String titleKey,
      String descriptionKey, boolean state) {
    ItemStack item = new ItemStack(material);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(Theme.titlePrefix() + Main.tr(viewer, titleKey)));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + Main.tr(viewer, descriptionKey),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-common-state", "state", _state(viewer, state)),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-common-action-toggle"))
          .stream().filter(Objects::nonNull).map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private String _state(Player viewer, boolean enabled) {
    return (enabled ? Theme.highlight() : Theme.error())
        + Main.tr(viewer, enabled ? "common-state-enabled" : "common-state-disabled");
  }
}
