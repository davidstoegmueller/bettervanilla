package com.daveestar.bettervanilla.manager;

import java.util.Collection;

import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class MaintenanceManager {
  private Boolean _isMaintenance;

  private final Main _plugin;
  private SettingsManager _settingsManager;

  public MaintenanceManager() {
    _plugin = Main.getInstance();
  }

  public void initManagers() {
    _settingsManager = _plugin.getSettingsManager();
    _isMaintenance = _settingsManager.getMaintenance();
  }

  public void setState(boolean isMaintenance, String message) {
    _isMaintenance = isMaintenance;

    _settingsManager.setMaintenance(isMaintenance, isMaintenance ? message : null);
  }

  public boolean getState() {
    return _isMaintenance;
  }

  public void kickAll(Collection<? extends Player> players) {

    for (Player p : players) {
      sendMaintenance(p);
    }
  }

  public void sendMaintenance(Player p) {
    if (_isMaintenance) {
      if (!p.hasPermission("bettervanilla.maintenance.bypass")) {

        String message = _settingsManager.getMaintenanceMessage();

        String maintenanceMsg = ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + "MAINTENANCE\n\n"
            + ChatColor.GRAY
            + "The server is currently in maintenance mode.\nCheck back later or notify the admin of the server.\n\n"
            + (message != null ? ChatColor.YELLOW + "" + ChatColor.BOLD + "Message: " + ChatColor.GRAY + message
                : "");
        p.kick(Component.text(maintenanceMsg));
      } else {
        p.sendMessage(Main.getPrefix() + "You bypassed maintenance mode.");
      }
    }
  }
}
