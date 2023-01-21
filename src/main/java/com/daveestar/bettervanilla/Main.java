package com.daveestar.bettervanilla;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
    getCommand("help").setExecutor(new HelpCommand());
    getCommand("togglelocation").setExecutor(new ToggleLocationCommand());

    PluginManager manager = getServer().getPluginManager();
    manager.registerEvents(new DeathChest(), this);
    manager.registerEvents(new ChatMessages(), this);
    manager.registerEvents(new PlayerMove(), this);
  }

  public void onDisable() {
    mainInstance = null;

    // remove all deathchest blocks on plugin reload
    for (Block block : DeathChest.deathChest.keySet()) {
      block.setType(Material.AIR);
    }

    LOGGER.info("bettervanilla disabled");
  }

  public static Main getInstance() {
    return mainInstance;
  }

  public static String getPrefix() {
    return ChatColor.GRAY + "[" + ChatColor.YELLOW + "BetterVanilla" + ChatColor.GRAY + "] ";
  }
}
