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
import com.daveestar.bettervanilla.commands.ToggleCompassCommand;
import com.daveestar.bettervanilla.commands.ToggleLocationCommand;
import com.daveestar.bettervanilla.commands.WaypointsCommand;
import com.daveestar.bettervanilla.events.ChatMessages;
import com.daveestar.bettervanilla.events.DeathChest;
import com.daveestar.bettervanilla.events.PlayerMove;
import com.daveestar.bettervanilla.events.SittableStairs;
import com.daveestar.bettervanilla.models.AFKManager;
import com.daveestar.bettervanilla.models.CompassManager;
import com.daveestar.bettervanilla.models.MaintenanceManager;
import com.daveestar.bettervanilla.models.TimerManager;
import com.daveestar.bettervanilla.models.WaypointsManager;
import com.daveestar.bettervanilla.utils.Config;

/*
 * Bettervanilla Java Plugin
 */
public class Main extends JavaPlugin {
  private static Main _mainInstance;
  private static final Logger _LOGGER = Logger.getLogger("bettervanilla");

  private WaypointsManager _waypointsManager;
  private TimerManager _timerManager;
  private MaintenanceManager _maintenanceManager;
  private AFKManager _afkManager;
  private CompassManager _compassManager;

  public void onEnable() {
    _mainInstance = this;

    Config waypointsConfig = new Config("waypoints.yml", getDataFolder());
    _waypointsManager = new WaypointsManager(waypointsConfig);

    Config timerConfig = new Config("timer.yml", getDataFolder());
    _timerManager = new TimerManager(timerConfig);

    Config maintenanceConfig = new Config("maintenance.yml", getDataFolder());
    _maintenanceManager = new MaintenanceManager(maintenanceConfig);

    _afkManager = new AFKManager();
    _compassManager = new CompassManager();

    _LOGGER.info("BetterVanilla ENABLED");

    // register commands
    getCommand("waypoints").setExecutor(new WaypointsCommand());
    getCommand("ping").setExecutor(new PingCommand());
    getCommand("invsee").setExecutor(new InvseeCommand());
    getCommand("help").setExecutor(new HelpCommand());
    getCommand("adminhelp").setExecutor(new HelpCommand());
    getCommand("togglelocation").setExecutor(new ToggleLocationCommand());
    getCommand("togglecompass").setExecutor(new ToggleCompassCommand());
    getCommand("playerhead").setExecutor(new PlayerHeadCommand());
    getCommand("lastdeath").setExecutor(new LastDeathCommand());
    getCommand("timer").setExecutor(new TimerCommand());
    getCommand("maintenance").setExecutor(new MaintenanceCommand());

    // register events
    PluginManager manager = getServer().getPluginManager();
    manager.registerEvents(new DeathChest(), this);
    manager.registerEvents(new ChatMessages(), this);
    manager.registerEvents(new PlayerMove(), this);
    manager.registerEvents(new SittableStairs(), this);
  }

  public void onDisable() {
    _mainInstance = null;

    // remove all deathchest blocks on plugin reload
    for (Block block : DeathChest.deathChest.keySet()) {
      block.setType(Material.AIR);
    }

    _compassManager.destroy();

    _LOGGER.info("BetterVanilla DISABLED");
  }

  public static String getPrefix() {
    return ChatColor.GRAY + "[" + ChatColor.YELLOW + "BetterVanilla" + ChatColor.GRAY + "]" + ChatColor.YELLOW + " » "
        + ChatColor.GRAY;
  }

  public static Main getInstance() {
    return _mainInstance;
  }

  public WaypointsManager get_waypointsManager() {
    return _waypointsManager;
  }

  public TimerManager get_timerManager() {
    return _timerManager;
  }

  public MaintenanceManager get_maintenanceManager() {
    return _maintenanceManager;
  }

  public AFKManager getAFKManager() {
    return _afkManager;
  }

  public CompassManager get_compassManager() {
    return _compassManager;
  }
}
