package com.daveestar.bettervanilla.manager;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.md_5.bungee.api.ChatColor;

public class TabListManager {
  private final Main _plugin;

  private TimerManager _timerManager;
  private SettingsManager _settingsManager;
  private AFKManager _afkManager;
  private VanishManager _vanishManager;

  private ScheduledTask _task;

  public TabListManager() {
    _plugin = Main.getInstance();
  }

  public void initManagers() {
    _timerManager = _plugin.getTimerManager();
    _settingsManager = _plugin.getSettingsManager();
    _afkManager = _plugin.getAFKManager();
    _vanishManager = _plugin.getVanishManager();

    _startTask();
  }

  public void destroy() {
    if (_task != null) {
      _task.cancel();
      _task = null;
    }
  }

  public void refreshPlayer(Player p) {
    if (p == null) {
      return;
    }

    refreshPlayerListEntry(p);
    p.sendPlayerListHeaderAndFooter(_buildHeader(p), _buildFooter(p));
  }

  public void refreshPlayerListEntry(Player p) {
    if (p == null) {
      return;
    }

    if (_vanishManager != null && _vanishManager.isVanished(p)) {
      return;
    }

    p.playerListName(_buildPlayerListName(p));
  }

  private void _startTask() {
    if (_task != null) {
      _task.cancel();
    }

    AsyncScheduler scheduler = Main.getInstance().getServer().getAsyncScheduler();
    _task = scheduler.runAtFixedRate(Main.getInstance(), task -> {
      for (Player p : _plugin.getServer().getOnlinePlayers()) {
        refreshPlayer(p);
      }
    }, 0, 1, TimeUnit.SECONDS);
  }

  private Component _buildHeader(Player p) {
    String placeholder = "                         "; // 25 spaces
    World world = p.getWorld();
    long day = world.getFullTime() / 24000L;
    String timeStr = _formatWorldTime(world);
    Environment env = world.getEnvironment();

    String weatherStr;
    if (env == Environment.NETHER || env == Environment.THE_END) {
      weatherStr = "None";
    } else if (world.isThundering()) {
      weatherStr = "Thunder";
    } else if (world.hasStorm()) {
      weatherStr = p.isInRain() ? "Raining" : "Storm";
    } else {
      weatherStr = "Clear";
    }

    boolean isMaintenance = _settingsManager.getMaintenanceState();

    List<String> lines = Arrays.asList(
        "",
        placeholder + ChatColor.YELLOW + "" + ChatColor.BOLD + "BetterVanilla SMP" + ChatColor.RESET + placeholder,
        isMaintenance ? Main.getShortPrefix() + ChatColor.RED + "" + ChatColor.BOLD + "Maintenance-Mode: ON" : null,
        "",
        Main.getShortPrefix() + ChatColor.GRAY + "Day: " + ChatColor.YELLOW + day + ChatColor.GRAY + " | "
            + ChatColor.GRAY + "Time: " + ChatColor.YELLOW + timeStr + ChatColor.GRAY,
        Main.getShortPrefix() + ChatColor.GRAY + "Weather: " + ChatColor.YELLOW + weatherStr,
        "");

    return Component.join(
        JoinConfiguration.newlines(),
        lines.stream()
            .filter(Objects::nonNull)
            .map(Component::text)
            .toList());
  }

  private Component _buildFooter(Player p) {
    int playTimeSeconds = _timerManager.getPlayTime(p);
    int onlinePlayersCount = _plugin.getServer().getOnlinePlayers().size();
    int maxPlayerCount = _plugin.getServer().getMaxPlayers();

    List<String> lines = Arrays.asList(
        "",
        Main.getShortPrefix() + ChatColor.GRAY + "Players: " + ChatColor.YELLOW + onlinePlayersCount + ChatColor.GRAY
            + "/" + ChatColor.YELLOW
            + maxPlayerCount,
        Main.getShortPrefix() + ChatColor.GRAY + "Playtime: " + ChatColor.YELLOW
            + _timerManager.formatTime(playTimeSeconds),
        "",
        "",
        Main.getShortPrefix() + ChatColor.GRAY + "Ping: " + ChatColor.YELLOW + _formatPing(p) + ChatColor.GRAY
            + " | TPS: " + ChatColor.YELLOW + _formatTps()
            + ChatColor.GRAY + " | MSPT: " + ChatColor.YELLOW + _formatMspt(),
        "",
        ChatColor.GRAY + "Need some help? " + ChatColor.YELLOW + "/help",
        "");

    return Component.join(
        JoinConfiguration.newlines(),
        lines.stream()
            .filter(Objects::nonNull)
            .map(Component::text)
            .toList());
  }

  private Component _buildPlayerListName(Player p) {
    boolean isAfk = _afkManager != null && _afkManager.isPlayerMarkedAFK(p);
    int deaths = p.getStatistic(Statistic.DEATHS);

    Component playerNameComponent = isAfk
        ? Component.text(ChatColor.GRAY + "[" + ChatColor.YELLOW + "AFK" + ChatColor.GRAY + "] "
            + ChatColor.YELLOW + p.getName() + ChatColor.GRAY + " | " + ChatColor.GRAY + "Deaths: "
            + ChatColor.YELLOW + deaths)
        : Component.text(ChatColor.YELLOW + p.getName() + ChatColor.GRAY + " | " + ChatColor.GRAY + "Deaths: "
            + ChatColor.YELLOW + deaths);

    return playerNameComponent;
  }

  private String _formatWorldTime(World world) {
    long time = (world.getTime() + 6000L) % 24000L;
    int hours = (int) (time / 1000L);
    int minutes = (int) ((time % 1000L) * 60L / 1000L);

    return String.format(Locale.US, "%02d:%02d", hours, minutes);
  }

  private String _formatPing(Player p) {
    return String.format(Locale.US, "%d", p.getPing());
  }

  private String _formatTps() {
    double[] tpsValues = _plugin.getServer().getTPS();
    double tps = tpsValues.length > 0 ? tpsValues[0] : 20.0D;
    double capped = Math.min(20.0D, tps);

    return String.format(Locale.US, "%.1f", capped);
  }

  private String _formatMspt() {
    double mspt = _plugin.getServer().getAverageTickTime();
    return String.format(Locale.US, "%.1f", mspt);
  }
}
