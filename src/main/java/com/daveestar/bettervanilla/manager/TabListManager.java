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
import com.daveestar.bettervanilla.utils.Theme;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.md_5.bungee.api.ChatColor;

public class TabListManager {
  private static final double DEFAULT_TPS = 20.0D;
  private static final double DEFAULT_MSPT = 0.0D;

  private final Main _plugin;

  private TimerManager _timerManager;
  private SettingsManager _settingsManager;
  private AFKManager _afkManager;
  private VanishManager _vanishManager;
  private TagManager _tagManager;

  private ScheduledTask _task;

  public TabListManager() {
    _plugin = Main.getInstance();
    _timerManager = _plugin.getTimerManager();
    _settingsManager = _plugin.getSettingsManager();
    _afkManager = _plugin.getAFKManager();
    _vanishManager = _plugin.getVanishManager();
    _tagManager = _plugin.getTagManager();

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

    if (_vanishManager.isVanished(p)) {
      return;
    }

    p.playerListName(_buildPlayerListName(p));
  }

  public String buildPlayerListName(Player p) {
    if (p == null) {
      return "";
    }

    boolean isAfk = _afkManager.isPlayerMarkedAFK(p);
    String tagSuffix = _tagManager.getFormattedTag(p);

    String baseName = (isAfk
        ? Theme.primary() + "[" + Theme.error() + Main.tr(p, "common-status-afk-short") + Theme.primary() + "] "
        : "")
        + Theme.highlight() + p.getName()
        + tagSuffix;

    int deaths = p.getStatistic(Statistic.DEATHS);
    return baseName + Theme.primary() + Main.tr(p, "tab-list-player-deaths",
        "deaths", Theme.highlight() + String.valueOf(deaths));
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
    String timePeriod = _getTimeOfDayText(p, world);
    String moonPhase = _getMoonPhaseText(p, world);
    String weather = _getWeatherText(p, world);

    boolean isMaintenance = _settingsManager.getMaintenanceState();

    List<String> lines = Arrays.asList(
        "",
        placeholder + Theme.highlight() + "" + ChatColor.BOLD
            + Main.tr(p, "tab-list-header-server-name", "server", Theme.name())
            + ChatColor.RESET + placeholder,
        isMaintenance ? Main.getShortPrefix() + Theme.error() + "" + ChatColor.BOLD
            + Main.tr(p, "tab-list-header-maintenance-enabled") : null,
        "",
        Main.getShortPrefix() + Theme.primary() + Main.tr(p, "tab-list-header-day-time",
            "day", Theme.highlight() + String.valueOf(day) + Theme.primary(),
            "time", Theme.highlight() + timeStr + Theme.primary(),
            "period", Theme.highlight() + timePeriod + Theme.primary()),
        Main.getShortPrefix() + Theme.primary() + Main.tr(p, "tab-list-header-weather-moon",
            "weather", Theme.highlight() + weather + Theme.primary(),
            "moon", Theme.highlight() + moonPhase),
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
    int onlinePlayersCount = _plugin.getServer().getOnlinePlayers().size() - _vanishManager.getVanishedCount();
    int maxPlayerCount = _plugin.getServer().getMaxPlayers();

    List<String> lines = Arrays.asList(
        "",
        Main.getShortPrefix() + Theme.primary() + Main.tr(p, "tab-list-footer-player-count",
            "online", Theme.highlight() + String.valueOf(onlinePlayersCount) + Theme.primary(),
            "maximum", Theme.highlight() + String.valueOf(maxPlayerCount)),
        Main.getShortPrefix() + Theme.primary() + Main.tr(p, "tab-list-footer-playtime",
            "time", Theme.highlight() + _timerManager.formatTime(p, playTimeSeconds)),
        "",
        "",
        Main.getShortPrefix() + Theme.primary() + Main.tr(p, "tab-list-footer-server-performance",
            "ping", Theme.highlight() + _formatPing(p) + Theme.primary(),
            "tps", Theme.highlight() + _formatTps() + Theme.primary(),
            "mspt", Theme.highlight() + _formatMspt()),
        "",
        Theme.primary() + Main.tr(p, "tab-list-footer-help",
            "command", Theme.highlight() + "/help"),
        "");

    return Component.join(
        JoinConfiguration.newlines(),
        lines.stream()
            .filter(Objects::nonNull)
            .map(Component::text)
            .toList());
  }

  private Component _buildPlayerListName(Player p) {
    return Component.text(buildPlayerListName(p));
  }

  private String _getTimeOfDayText(Player viewer, World world) {
    long time = (world.getTime() + 6000L) % 24000L;
    int hours = (int) (time / 1000L);

    if (hours >= 6 && hours < 12) {
      return Main.tr(viewer, "time-period-morning");
    } else if (hours >= 12 && hours < 13.5) {
      return Main.tr(viewer, "time-period-midday");
    } else if (hours >= 13.5 && hours < 18) {
      return Main.tr(viewer, "time-period-afternoon");
    } else if (hours >= 18 && hours < 21) {
      return Main.tr(viewer, "time-period-evening");
    } else {
      return Main.tr(viewer, "time-period-night");
    }
  }

  private String _getWeatherText(Player viewer, World world) {
    Environment env = world.getEnvironment();
    if (env == Environment.NETHER || env == Environment.THE_END) {
      return Main.tr(viewer, "weather-none");
    } else if (world.isThundering()) {
      return Main.tr(viewer, "weather-thunder");
    } else if (world.hasStorm()) {
      return Main.tr(viewer, viewer.isInRain() ? "weather-raining" : "weather-storm");
    }
    return Main.tr(viewer, "weather-clear");
  }

  private String _getMoonPhaseText(Player viewer, World world) {
    if (world == null) {
      return Main.tr(viewer, "common-value-unknown");
    }

    Environment env = world.getEnvironment();
    if (env == Environment.NETHER || env == Environment.THE_END) {
      return Main.tr(viewer, "moon-phase-none");
    }

    long day = world.getFullTime() / 24000L;
    int phase = (int) (day % 8L);

    String name = switch (phase) {
      case 0 -> Main.tr(viewer, "moon-phase-full-moon");
      case 1 -> Main.tr(viewer, "moon-phase-waning-gibbous");
      case 2 -> Main.tr(viewer, "moon-phase-last-quarter");
      case 3 -> Main.tr(viewer, "moon-phase-waning-crescent");
      case 4 -> Main.tr(viewer, "moon-phase-new-moon");
      case 5 -> Main.tr(viewer, "moon-phase-waxing-crescent");
      case 6 -> Main.tr(viewer, "moon-phase-first-quarter");
      case 7 -> Main.tr(viewer, "moon-phase-waxing-gibbous");
      default -> Main.tr(viewer, "common-value-unknown");
    };

    return Main.tr(viewer, "moon-phase-with-index", "phase", name, "index", phase);
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
    double tpsValue = DEFAULT_TPS;

    try {
      double[] tpsValues = _plugin.getServer().getTPS();
      if (tpsValues != null && tpsValues.length > 0) {
        tpsValue = tpsValues[0];
      }
    } catch (RuntimeException ex) {
      // Folia's TPS access is not thread-safe; default to the fallback when the API
      // throws
    }

    if (Double.isNaN(tpsValue) || Double.isInfinite(tpsValue) || tpsValue < 0.0D) {
      tpsValue = DEFAULT_TPS;
    }

    return String.format(Locale.US, "%.1f", tpsValue);
  }

  private String _formatMspt() {
    double mspt = DEFAULT_MSPT;

    try {
      mspt = _plugin.getServer().getAverageTickTime();
    } catch (RuntimeException ex) {
      // Folia may throw when accessed off-thread; fall back to the default
      mspt = DEFAULT_MSPT;
    }

    if (Double.isNaN(mspt) || Double.isInfinite(mspt) || mspt < 0.0D) {
      mspt = DEFAULT_MSPT;
    }

    return String.format(Locale.US, "%.1f", mspt);
  }
}
