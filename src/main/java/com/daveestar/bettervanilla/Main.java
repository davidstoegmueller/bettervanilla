package com.daveestar.bettervanilla;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.daveestar.bettervanilla.commands.HelpCommands;
import com.daveestar.bettervanilla.commands.HereCommand;
import com.daveestar.bettervanilla.commands.InvseeCommand;
import com.daveestar.bettervanilla.commands.DeathPointsCommand;
import com.daveestar.bettervanilla.commands.PermissionsCommand;
import com.daveestar.bettervanilla.commands.PingCommand;
import com.daveestar.bettervanilla.commands.PlayTimeCommand;
import com.daveestar.bettervanilla.commands.SettingsCommand;
import com.daveestar.bettervanilla.commands.TimerCommand;
import com.daveestar.bettervanilla.commands.WaypointsCommand;
import com.daveestar.bettervanilla.crafting.CustomCraftingRecipe;
import com.daveestar.bettervanilla.enums.CraftingRecipe;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.commands.BackpackCommand;
import com.daveestar.bettervanilla.commands.MessageCommand;
import com.daveestar.bettervanilla.commands.ReplyCommand;
import com.daveestar.bettervanilla.commands.VanishCommand;
import com.daveestar.bettervanilla.commands.ModerationCommands;
import com.daveestar.bettervanilla.commands.SitCommand;
import com.daveestar.bettervanilla.events.ChatMessages;
import com.daveestar.bettervanilla.events.DeathChest;
import com.daveestar.bettervanilla.events.PlayerMove;
import com.daveestar.bettervanilla.events.PreventDimension;
import com.daveestar.bettervanilla.events.ServerMOTD;
import com.daveestar.bettervanilla.events.SittableStairs;
import com.daveestar.bettervanilla.events.SleepingRain;
import com.daveestar.bettervanilla.events.VeinMiningChopping;
import com.daveestar.bettervanilla.events.RightClickCropHarvest;
import com.daveestar.bettervanilla.events.DoubleDoorSync;
import com.daveestar.bettervanilla.events.ChestSort;
import com.daveestar.bettervanilla.events.CropProtection;
import com.daveestar.bettervanilla.events.ItemRestock;
import com.daveestar.bettervanilla.events.SignColors;
import com.daveestar.bettervanilla.events.VanishEvents;
import com.daveestar.bettervanilla.events.ModerationEvents;
import com.daveestar.bettervanilla.events.CreeperProtection;
import com.daveestar.bettervanilla.manager.AFKManager;
import com.daveestar.bettervanilla.manager.BackpackManager;
import com.daveestar.bettervanilla.manager.CompassManager;
import com.daveestar.bettervanilla.manager.DeathPointsManager;
import com.daveestar.bettervanilla.manager.MaintenanceManager;
import com.daveestar.bettervanilla.manager.MessageManager;
import com.daveestar.bettervanilla.manager.ModerationManager;
import com.daveestar.bettervanilla.manager.NavigationManager;
import com.daveestar.bettervanilla.manager.PermissionsManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.manager.SittingManager;
import com.daveestar.bettervanilla.manager.RecipeSyncManager;
import com.daveestar.bettervanilla.manager.TabListManager;
import com.daveestar.bettervanilla.manager.TimerManager;
import com.daveestar.bettervanilla.manager.VanishManager;
import com.daveestar.bettervanilla.manager.WaypointsManager;
import com.daveestar.bettervanilla.utils.ActionBar;
import com.daveestar.bettervanilla.utils.Config;

import net.md_5.bungee.api.ChatColor;

/*
 * Bettervanilla Java Plugin
 */
public class Main extends JavaPlugin {
  private static Main _mainInstance;
  private static final Logger _LOGGER = Logger.getLogger("bettervanilla");

  private ActionBar _actionBar;
  private NavigationManager _navigationManager;
  private AFKManager _afkManager;
  private CompassManager _compassManager;
  private SettingsManager _settingsManager;
  private PermissionsManager _permissionsManager;
  private DeathPointsManager _deathPointManager;
  private WaypointsManager _waypointsManager;
  private TimerManager _timerManager;
  private MaintenanceManager _maintenanceManager;
  private BackpackManager _backpackManager;
  private MessageManager _messageManager;
  private VanishManager _vanishManager;
  private ModerationManager _moderationManager;
  private SittingManager _sittingManager;
  private TabListManager _tabListManager;
  private RecipeSyncManager _recipeSyncManager;
  private Map<CraftingRecipe, CustomCraftingRecipe> _craftingRecipes;

  public void onEnable() {
    _mainInstance = this;

    Config settingsConfig = new Config("settings.yml", getDataFolder());
    Config permissionsConfig = new Config("permissions.yml", getDataFolder());
    Config timerConfig = new Config("timer.yml", getDataFolder());
    Config deathPointConfig = new Config("deathpoints.yml", getDataFolder());
    Config waypointsConfig = new Config("waypoints.yml", getDataFolder());
    Config backpackConfig = new Config("backpacks.yml", getDataFolder());
    Config moderationConfig = new Config("moderations.yml", getDataFolder());

    // initialize managers with configs
    _settingsManager = new SettingsManager(settingsConfig);
    _permissionsManager = new PermissionsManager(permissionsConfig);
    _timerManager = new TimerManager(timerConfig);
    _deathPointManager = new DeathPointsManager(deathPointConfig);
    _waypointsManager = new WaypointsManager(waypointsConfig);
    _backpackManager = new BackpackManager(backpackConfig);
    _moderationManager = new ModerationManager(moderationConfig);

    // initialize managers without configs
    _messageManager = new MessageManager();
    _sittingManager = new SittingManager();
    _vanishManager = new VanishManager();
    _maintenanceManager = new MaintenanceManager();
    _actionBar = new ActionBar();
    _navigationManager = new NavigationManager();
    _afkManager = new AFKManager();
    _compassManager = new CompassManager();
    _tabListManager = new TabListManager();
    _recipeSyncManager = new RecipeSyncManager();

    // initialize managers with dependencies
    _afkManager.initManagers();
    _compassManager.initManagers();
    _maintenanceManager.initManagers();
    _navigationManager.initManagers();
    _timerManager.initManagers();

    // crafting recipes
    _craftingRecipes = new EnumMap<>(CraftingRecipe.class);
    for (CraftingRecipe recipe : CraftingRecipe.values()) {
      _registerCraftingRecipe(recipe);
    }

    _LOGGER.info("BetterVanilla - ENABLED");

    // register commands
    getCommand("help").setExecutor(new HelpCommands.HelpCommand());
    getCommand("adminhelp").setExecutor(new HelpCommands.AdminHelpCommand());
    getCommand("permissions").setExecutor(new PermissionsCommand());
    getCommand("settings").setExecutor(new SettingsCommand());
    getCommand("vanish").setExecutor(new VanishCommand());
    getCommand("invsee").setExecutor(new InvseeCommand());
    getCommand("kick").setExecutor(new ModerationCommands.KickCommand());
    getCommand("ban").setExecutor(new ModerationCommands.BanCommand());
    getCommand("unban").setExecutor(new ModerationCommands.UnbanCommand());
    getCommand("mute").setExecutor(new ModerationCommands.MuteCommand());
    getCommand("unmute").setExecutor(new ModerationCommands.UnmuteCommand());
    getCommand("timer").setExecutor(new TimerCommand());
    getCommand("playtime").setExecutor(new PlayTimeCommand());
    getCommand("waypoints").setExecutor(new WaypointsCommand());
    getCommand("deathpoints").setExecutor(new DeathPointsCommand());
    getCommand("ping").setExecutor(new PingCommand());
    getCommand("sit").setExecutor(new SitCommand());
    getCommand("backpack").setExecutor(new BackpackCommand());
    getCommand("message").setExecutor(new MessageCommand());
    getCommand("reply").setExecutor(new ReplyCommand());
    getCommand("here").setExecutor(new HereCommand());

    // register events
    PluginManager manager = getServer().getPluginManager();
    manager.registerEvents(new ServerMOTD(), this);
    manager.registerEvents(new DeathChest(), this);
    manager.registerEvents(new CreeperProtection(), this);
    manager.registerEvents(new ChatMessages(), this);
    manager.registerEvents(new PlayerMove(), this);
    manager.registerEvents(new SittableStairs(), this);
    manager.registerEvents(new PreventDimension(), this);
    manager.registerEvents(new SleepingRain(), this);
    manager.registerEvents(new CropProtection(), this);
    manager.registerEvents(new RightClickCropHarvest(), this);
    manager.registerEvents(new DoubleDoorSync(), this);
    manager.registerEvents(new ChestSort(), this);
    manager.registerEvents(new ItemRestock(), this);
    manager.registerEvents(new VeinMiningChopping(), this);
    manager.registerEvents(new SignColors(), this);
    manager.registerEvents(new VanishEvents(), this);
    manager.registerEvents(new ModerationEvents(), this);

    _settingsManager.applyLocatorBarSetting();
    _settingsManager.applyPlayersSleepingPercentageSetting();
  }

  @Override
  public void onDisable() {
    _mainInstance = null;

    // prepare all features for plugin disable
    _timerManager.destroy();
    _compassManager.destroy();
    _backpackManager.destroy();
    _tabListManager.destroy();

    if (_craftingRecipes != null) {
      _craftingRecipes.values().forEach(CustomCraftingRecipe::destroyRecipe);
      _craftingRecipes.clear();
    }

    _LOGGER.info("BetterVanilla - DISABLED");

    if (_sittingManager != null) {
      _sittingManager.destroy();
    }
  }

  private void _registerCraftingRecipe(CraftingRecipe recipe) {
    if (_craftingRecipes == null) {
      _craftingRecipes = new EnumMap<>(CraftingRecipe.class);
    }

    CustomCraftingRecipe customCraftingRecipe = recipe.createHandler();
    if (customCraftingRecipe == null) {
      return;
    }

    customCraftingRecipe.applyRecipe();
    _craftingRecipes.put(recipe, customCraftingRecipe);
  }

  public static String getPrefix() {
    return ChatColor.GRAY + "[" + ChatColor.YELLOW + "BetterVanilla" + ChatColor.GRAY + "]"
        + ChatColor.YELLOW + " » "
        + ChatColor.GRAY;
  }

  public static String getShortPrefix() {
    return ChatColor.YELLOW + " » " + ChatColor.GRAY;
  }

  public static String getNoPermissionMessage(Permissions permission) {
    return getPrefix() + ChatColor.RED + "Sorry! You do not have permission to use this. " + ChatColor.GRAY + "("
        + ChatColor.RED + permission.getName() + ChatColor.GRAY + ")";
  }

  public static String getShortNoPermissionMessage(Permissions permission) {
    return ChatColor.RED + "You do not have permission to use this. " + ChatColor.GRAY + "("
        + ChatColor.RED + permission.getName() + ChatColor.GRAY + ")";
  }

  public static String getNoPlayerMessage() {
    return getPrefix() + ChatColor.RED + "This command can only be run by a player.";
  }

  public static Main getInstance() {
    return _mainInstance;
  }

  public ActionBar getActionBar() {
    return _actionBar;
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

  public PermissionsManager getPermissionsManager() {
    return _permissionsManager;
  }

  public DeathPointsManager getDeathPointsManager() {
    return _deathPointManager;
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

  public BackpackManager getBackpackManager() {
    return _backpackManager;
  }

  public MessageManager getMessageManager() {
    return _messageManager;
  }

  public VanishManager getVanishManager() {
    return _vanishManager;
  }

  public ModerationManager getModerationManager() {
    return _moderationManager;
  }

  public SittingManager getSittingManager() {
    return _sittingManager;
  }

  public TabListManager getTabListManager() {
    return _tabListManager;
  }

  public RecipeSyncManager getRecipeSyncManager() {
    return _recipeSyncManager;
  }

  public CustomCraftingRecipe getCraftingRecipe(CraftingRecipe recipe) {
    if (_craftingRecipes == null) {
      return null;
    }

    return _craftingRecipes.get(recipe);
  }

  public Map<CraftingRecipe, CustomCraftingRecipe> getCraftingRecipes() {
    if (_craftingRecipes == null) {
      return Collections.emptyMap();
    }

    return Collections.unmodifiableMap(_craftingRecipes);
  }
}
