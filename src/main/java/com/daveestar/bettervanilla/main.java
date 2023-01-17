package com.daveestar.bettervanilla;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * bettervanilla java plugin
 */
public class Main extends JavaPlugin {
  private static Main mainInstance;
  private static final Logger LOGGER = Logger.getLogger("bettervanilla");

  // private Config waypoints;

  public void onEnable() {
    mainInstance = this;

    LOGGER.info("bettervanilla enabled");

    // register commands
    getCommand("waypoints").setExecutor(new WaypointsCommand());
    getCommand("ping").setExecutor(new PingCommand());
    getCommand("invsee").setExecutor(new InvseeCommand());

    PluginManager manager = getServer().getPluginManager();
    manager.registerEvents(new DeathChest(), this);
  }

  public void onDisable() {
    mainInstance = null;

    LOGGER.info("bettervanilla disabled");
  }

  public static Main getInstance() {
    return mainInstance;
  }

  public static String getPrefix() {
    return ChatColor.GRAY + "[" + ChatColor.YELLOW + "BetterVanilla" + ChatColor.GRAY + "] ";
  }
}
