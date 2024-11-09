package com.daveestar.bettervanilla.models;

import java.util.Collection;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.Config;

import net.md_5.bungee.api.ChatColor;

public class MaintenanceManager {
  private Boolean _isMaintenance;

  private Config _config;
  private FileConfiguration _fileCfgn;

  public MaintenanceManager(Config config) {
    this._config = config;
    this._fileCfgn = config.getFileCfgrn();

    this._isMaintenance = _fileCfgn.getBoolean("isMaintenance");
  }

  public void setState(boolean isMaintenance) {
    this._isMaintenance = isMaintenance;

    _fileCfgn.set("isMaintenance", isMaintenance);
    _config.save();
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

        String maintenanceMsg = ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + "MAINTENANCE\n\n"
            + ChatColor.GRAY
            + "The server is currently in maintenance mode.\nCheck back later or notify the admin of the server.";
        p.kickPlayer(maintenanceMsg);
      } else {
        p.sendMessage(Main.getPrefix() + "You bypassed maintenance mode.");
      }
    }
  }
}
