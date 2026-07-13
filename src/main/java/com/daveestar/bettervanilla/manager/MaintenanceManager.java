package com.daveestar.bettervanilla.manager;

import java.util.Collection;

import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.utils.Theme;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class MaintenanceManager {
  private final Main _plugin;
  private SettingsManager _settingsManager;

  public MaintenanceManager() {
    _plugin = Main.getInstance();
  }

  public void initManagers() {
    _settingsManager = _plugin.getSettingsManager();
  }

  public void kickAll(Collection<? extends Player> players) {

    for (Player p : players) {
      sendMaintenance(p);
    }
  }

  public boolean sendMaintenance(Player p) {
    if (_settingsManager.getMaintenanceState()) {
      if (!p.hasPermission(Permissions.MAINTENANCE_BYPASS.getName())) {
        String message = _settingsManager.getMaintenanceMessage();

        String maintenanceMsg = Theme.highlight() + "" + ChatColor.BOLD
            + Main.tr(p, "maintenance-kick-title") + "\n\n"
            + Theme.primary() + Main.tr(p, "maintenance-kick-description") + "\n\n"
            + (message != null && !message.isBlank()
                ? Theme.highlight() + "" + ChatColor.BOLD + Main.tr(p, "maintenance-kick-custom-message",
                    "message", Theme.primary() + message)
                : "");
        p.kick(Component.text(maintenanceMsg));
        return false;
      } else {
        p.sendMessage(Main.getPrefix() + Main.tr(p, "maintenance-bypass-message"));
        return true;
      }
    }

    return true;
  }
}
