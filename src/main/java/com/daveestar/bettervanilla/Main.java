package com.daveestar.bettervanilla;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.daveestar.bettervanilla.utils.Config;

/*
 * bettervanilla java plugin
 */
public class Main extends JavaPlugin {
  private static Main mainInstance;
  private static final Logger LOGGER = Logger.getLogger("bettervanilla");

  private Timer timer;

  private Maintenance maintenance;

  public void onEnable() {
    mainInstance = this;

    Config timerConfig = new Config("timer.yml", getDataFolder());
    timer = new Timer(timerConfig);

    Config maintenanceConfig = new Config("maintenance.yml", getDataFolder());
    maintenance = new Maintenance(maintenanceConfig);

    LOGGER.info("bettervanilla enabled");

    // register commands
    getCommand("waypoints").setExecutor(new WaypointsCommand());
    getCommand("ping").setExecutor(new PingCommand());
    getCommand("invsee").setExecutor(new InvseeCommand());
    getCommand("help").setExecutor(new HelpCommand());
    getCommand("adminhelp").setExecutor(new HelpCommand());
    getCommand("togglelocation").setExecutor(new ToggleLocationCommand());
    getCommand("playerhead").setExecutor(new PlayerHeadCommand());
    getCommand("lastdeath").setExecutor(new LastDeathCommand());
    getCommand("timer").setExecutor(new TimerCommand());
    getCommand("maintenance").setExecutor(new MaintenanceCommand());

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

  public Timer getTimer() {
    return timer;
  }

  public Maintenance getMaintenance() {
    return maintenance;
  }
}
