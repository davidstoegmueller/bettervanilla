package com.daveestar.bettervanilla;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.daveestar.bettervanilla.commands.HelpCommand;
import com.daveestar.bettervanilla.commands.InvseeCommand;
import com.daveestar.bettervanilla.commands.LastDeathCommand;
import com.daveestar.bettervanilla.commands.MaintenanceCommand;
import com.daveestar.bettervanilla.commands.PingCommand;
import com.daveestar.bettervanilla.commands.PlayerHeadCommand;
import com.daveestar.bettervanilla.commands.TimerCommand;
import com.daveestar.bettervanilla.commands.ToggleLocationCommand;
import com.daveestar.bettervanilla.commands.WaypointsCommand;
import com.daveestar.bettervanilla.events.ChatMessages;
import com.daveestar.bettervanilla.events.DeathChest;
import com.daveestar.bettervanilla.events.PlayerMove;
import com.daveestar.bettervanilla.models.AFKManager;
import com.daveestar.bettervanilla.models.MaintenanceManager;
import com.daveestar.bettervanilla.models.TimerManager;
import com.daveestar.bettervanilla.models.WaypointsManager;
import com.daveestar.bettervanilla.utils.Config;

/*
 * Bettervanilla Java Plugin
 */
public class Main extends JavaPlugin {
  private static Main mainInstance;
  private static final Logger LOGGER = Logger.getLogger("bettervanilla");

  private WaypointsManager waypointsManager;
  private TimerManager timerManager;
  private MaintenanceManager maintenanceManager;
  private AFKManager afkManager;

  public void onEnable() {
    mainInstance = this;

    Config waypointsConfig = new Config("waypoints.yml", getDataFolder());
    waypointsManager = new WaypointsManager(waypointsConfig);

    Config timerConfig = new Config("timer.yml", getDataFolder());
    timerManager = new TimerManager(timerConfig);

    Config maintenanceConfig = new Config("maintenance.yml", getDataFolder());
    maintenanceManager = new MaintenanceManager(maintenanceConfig);

    afkManager = new AFKManager();

    LOGGER.info("BETTERVANILLA ENABLED");

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

    // register events
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

    LOGGER.info("BETTERVANILLA DISABLED");
  }

  public static String getPrefix() {
    return ChatColor.GRAY + "[" + ChatColor.YELLOW + "BetterVanilla" + ChatColor.GRAY + "] ";
  }

  public static Main getInstance() {
    return mainInstance;
  }

  public WaypointsManager getWaypointsManager() {
    return waypointsManager;
  }

  public TimerManager getTimerManager() {
    return timerManager;
  }

  public MaintenanceManager getMaintenanceManager() {
    return maintenanceManager;
  }

  public AFKManager getAFKManager() {
    return afkManager;
  }
}
