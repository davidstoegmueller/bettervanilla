package com.daveestar.bettervanilla;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.daveestar.bettervanilla.commands.HelpCommand;
import com.daveestar.bettervanilla.commands.InvseeCommand;
import com.daveestar.bettervanilla.commands.LastDeathCommand;
import com.daveestar.bettervanilla.commands.PingCommand;
import com.daveestar.bettervanilla.commands.PlayTimeCommand;
import com.daveestar.bettervanilla.commands.SettingsCommand;
import com.daveestar.bettervanilla.commands.TimerCommand;
import com.daveestar.bettervanilla.commands.ToggleCompassCommand;
import com.daveestar.bettervanilla.commands.ToggleLocationCommand;
import com.daveestar.bettervanilla.commands.WaypointsCommand;
import com.daveestar.bettervanilla.events.ChatMessages;
import com.daveestar.bettervanilla.events.DeathChest;
import com.daveestar.bettervanilla.events.PlayerMove;
import com.daveestar.bettervanilla.events.PreventEnd;
import com.daveestar.bettervanilla.events.SittableStairs;
import com.daveestar.bettervanilla.models.AFKManager;
import com.daveestar.bettervanilla.models.CompassManager;
import com.daveestar.bettervanilla.models.MaintenanceManager;
import com.daveestar.bettervanilla.models.NavigationManager;
import com.daveestar.bettervanilla.models.SettingsManager;
import com.daveestar.bettervanilla.models.TimerManager;
import com.daveestar.bettervanilla.models.WaypointsManager;
import com.daveestar.bettervanilla.utils.ActionBarManager;
import com.daveestar.bettervanilla.utils.Config;

/*
 * Bettervanilla Java Plugin
 */
public class Main extends JavaPlugin {
  private static Main _mainInstance;
  private static final Logger _LOGGER = Logger.getLogger("bettervanilla");

  private ActionBarManager _actionBarManager;
  private NavigationManager _navigationManager;
  private AFKManager _afkManager;
  private CompassManager _compassManager;

  private SettingsManager _settingsManager;
  private WaypointsManager _waypointsManager;
  private TimerManager _timerManager;
  private MaintenanceManager _maintenanceManager;

  public void onEnable() {
    _mainInstance = this;

    _actionBarManager = new ActionBarManager();
    _navigationManager = new NavigationManager();
    _afkManager = new AFKManager();
    _compassManager = new CompassManager();

    Config settingsConfig = new Config("settings.yml", getDataFolder());
    _settingsManager = new SettingsManager(settingsConfig);

    Config waypointsConfig = new Config("waypoints.yml", getDataFolder());
    _waypointsManager = new WaypointsManager(waypointsConfig);

    Config timerConfig = new Config("timer.yml", getDataFolder());
    _timerManager = new TimerManager(timerConfig);

    Config maintenanceConfig = new Config("maintenance.yml", getDataFolder());
    _maintenanceManager = new MaintenanceManager(maintenanceConfig);

    _LOGGER.info("BetterVanilla ENABLED");

    // register commands
    getCommand("waypoints").setExecutor(new WaypointsCommand());
    getCommand("ping").setExecutor(new PingCommand());
    getCommand("invsee").setExecutor(new InvseeCommand());
    getCommand("help").setExecutor(new HelpCommand());
    getCommand("adminhelp").setExecutor(new HelpCommand());
    getCommand("togglelocation").setExecutor(new ToggleLocationCommand());
    getCommand("togglecompass").setExecutor(new ToggleCompassCommand());
    getCommand("lastdeath").setExecutor(new LastDeathCommand());
    getCommand("timer").setExecutor(new TimerCommand());
    getCommand("playtime").setExecutor(new PlayTimeCommand());
    getCommand("settings").setExecutor(new SettingsCommand());

    // register events
    PluginManager manager = getServer().getPluginManager();
    manager.registerEvents(new DeathChest(), this);
    manager.registerEvents(new ChatMessages(), this);
    manager.registerEvents(new PlayerMove(), this);
    manager.registerEvents(new SittableStairs(), this);
    manager.registerEvents(new PreventEnd(), this);
  }

  @Override
  public void onDisable() {
    _mainInstance = null;

    // prepare all features for plugin disable
    DeathChest.deathChest.keySet().forEach(block -> block.setType(Material.AIR));
    _timerManager.setRunning(false);
    getServer().getOnlinePlayers().forEach(_timerManager::onPlayerLeft);
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

  public ActionBarManager getActionBarManager() {
    return _actionBarManager;
  }

  public NavigationManager getNavigationManager() {
    return _navigationManager;
  }

  public AFKManager getAFKManager() {
    return _afkManager;
  }

  public CompassManager getCompassManager() {
    return _compassManager;
  }

  public SettingsManager getSettingsManager() {
    return _settingsManager;
  }

  public WaypointsManager getWaypointsManager() {
    return _waypointsManager;
  }

  public TimerManager getTimerManager() {
    return _timerManager;
  }

  public MaintenanceManager getMaintenanceManager() {
    return _maintenanceManager;
  }

}
