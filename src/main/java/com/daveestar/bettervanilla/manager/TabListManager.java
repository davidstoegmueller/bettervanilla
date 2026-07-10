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
        ? Theme.primary() + "[" + Theme.error() + "AFK" + Theme.primary() + "] "
        : "")
        + Theme.highlight() + p.getName()
        + tagSuffix;

    int deaths = p.getStatistic(Statistic.DEATHS);
    return baseName + Theme.primary() + " | Deaths: " + Theme.highlight() + deaths;
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
    String timTextStr = _getTimeOfDayText(world);
    String moonPhaseText = _getMoonPhaseText(world);
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
        placeholder + Theme.highlight() + "" + ChatColor.BOLD + Theme.name() + " SMP" + ChatColor.RESET
            + placeholder,
        isMaintenance ? Main.getShortPrefix() + Theme.error() + "" + ChatColor.BOLD + "Maintenance-Mode: ON" : null,
        "",
        Main.getShortPrefix() + Theme.primary() + "Day: " + Theme.highlight() + day + Theme.primary() + " | "
            + "Time: " + Theme.highlight() + timeStr + Theme.primary() + " (" + Theme.highlight()
            + timTextStr
            + Theme.primary() + ")",
        Main.getShortPrefix() + Theme.primary() + "Weather: " + Theme.highlight() + weatherStr
            + Theme.primary() + " | Moon: " + Theme.highlight() + moonPhaseText,
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
        Main.getShortPrefix() + Theme.primary() + "Players: " + Theme.highlight() + onlinePlayersCount
            + Theme.primary() + "/" + Theme.highlight()
            + maxPlayerCount,
        Main.getShortPrefix() + Theme.primary() + "Playtime: " + Theme.highlight()
            + _timerManager.formatTime(playTimeSeconds),
        "",
        "",
        Main.getShortPrefix() + Theme.primary() + "Ping: " + Theme.highlight() + _formatPing(p) + Theme.primary()
            + " | TPS: " + Theme.highlight() + _formatTps()
            + Theme.primary() + " | MSPT: " + Theme.highlight() + _formatMspt(),
        "",
        Theme.primary() + "Need some help? " + Theme.highlight() + "/help",
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

  private String _getTimeOfDayText(World world) {
    long time = (world.getTime() + 6000L) % 24000L;
    int hours = (int) (time / 1000L);

    if (hours >= 6 && hours < 12) {
      return "Morning";
    } else if (hours >= 12 && hours < 13.5) {
      return "Midday";
    } else if (hours >= 13.5 && hours < 18) {
      return "Afternoon";
    } else if (hours >= 18 && hours < 21) {
      return "Evening";
    } else {
      return "Night";
    }
  }

  private String _getMoonPhaseText(World world) {
    if (world == null) {
      return "Unknown";
    }

    Environment env = world.getEnvironment();
    if (env == Environment.NETHER || env == Environment.THE_END) {
      return "None";
    }

    long day = world.getFullTime() / 24000L;
    int phase = (int) (day % 8L);

    String name = switch (phase) {
      case 0 -> "Full Moon";
      case 1 -> "Waning Gibbous";
      case 2 -> "Last Quarter";
      case 3 -> "Waning Crescent";
      case 4 -> "New Moon";
      case 5 -> "Waxing Crescent";
      case 6 -> "First Quarter";
      case 7 -> "Waxing Gibbous";
      default -> "Unknown";
    };

    return name + " #" + phase + "/8";
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
