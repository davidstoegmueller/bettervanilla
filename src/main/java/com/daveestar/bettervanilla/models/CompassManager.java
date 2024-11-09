package com.daveestar.bettervanilla.models;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.daveestar.bettervanilla.Main;

import java.util.HashMap;
import java.util.Map;

public class CompassManager {
  private static final int SCALE_LENGTH = 80;
  private static final int ARROW_POSITION = SCALE_LENGTH / 2;
  private static final ChatColor DIRECTION_COLOR = ChatColor.YELLOW; // Static color for all directions
  private static final DirectionInfo[] DIRECTIONS = {
      new DirectionInfo("N"), // N
      new DirectionInfo("NE"), // NE
      new DirectionInfo("E"), // E
      new DirectionInfo("SE"), // SE
      new DirectionInfo("S"), // S
      new DirectionInfo("SW"), // SW
      new DirectionInfo("W"), // W
      new DirectionInfo("NW") // NW
  }; // Directions with text only
  private static final int UPDATE_INTERVAL = 1; // Tick interval for compass updates
  private static final char FILL_CHARACTER = '·'; // Enhanced visual fill character
  private static final String ARROW_CHARACTER = "▲"; // Enhanced arrow character without color
  private static final int FILL_CHAR_AMOUNT = 20; // Number of fill characters between directions

  private final Map<Player, BossBar> activeCompass = new HashMap<>();

  public CompassManager() {
    startCompassUpdateTask();
  }

  public void destroy() {
    activeCompass.values().forEach(BossBar::removeAll);
    activeCompass.clear();
  }

  public Boolean checkPlayerActiveCompass(Player p) {
    return activeCompass.containsKey(p);
  }

  public void addPlayerToCompass(Player player) {
    activeCompass.computeIfAbsent(player, p -> {
      BossBar compassBossBar = Bukkit.createBossBar("", BarColor.YELLOW, BarStyle.SOLID);
      compassBossBar.addPlayer(player);
      return compassBossBar;
    });
  }

  public void removePlayerFromCompass(Player player) {
    BossBar compassBossBar = activeCompass.remove(player);
    if (compassBossBar != null) {
      compassBossBar.removePlayer(player);
    }
  }

  private void startCompassUpdateTask() {
    new BukkitRunnable() {
      @Override
      public void run() {
        activeCompass.forEach((player, compassBossBar) -> updateCompassDirection(player, compassBossBar));
      }
    }.runTaskTimer(Main.getInstance(), 0, UPDATE_INTERVAL); // Updates every tick (0.05 seconds)
  }

  private void updateCompassDirection(Player player, BossBar compassBossBar) {
    float yaw = player.getLocation().getYaw();
    yaw = (yaw + 180) % 360; // Adjust to align North and South correctly // Normalize yaw to the 0-360 range

    String compassScale = getDynamicCompassScale(yaw);
    compassBossBar.setTitle(compassScale);
  }

  private String getDynamicCompassScale(double yaw) {
    StringBuilder fullScale = new StringBuilder();
    for (DirectionInfo direction : DIRECTIONS) {
      fullScale.append(direction.name);
      for (int i = 0; i < FILL_CHAR_AMOUNT; i++) {
        fullScale.append(FILL_CHARACTER);
      }
    }

    // Create a full rotation of the compass and wrap around if needed
    String completeCompass = fullScale.toString() + fullScale.toString();

    // Calculate the starting point based on the yaw, centering the arrow position
    int startIndex = (int) Math.round((yaw / 360) * fullScale.length()) - ARROW_POSITION;
    if (startIndex < 0) {
      startIndex += fullScale.length();
    }

    // Extract the visible portion of the compass
    String visibleCompass = completeCompass.substring(startIndex, startIndex + SCALE_LENGTH);

    // Insert the arrow at the center
    StringBuilder finalCompass = new StringBuilder(visibleCompass);
    finalCompass.replace(ARROW_POSITION, ARROW_POSITION + 1, ChatColor.RED + ARROW_CHARACTER + ChatColor.RESET);

    // Build the final string with colors applied
    StringBuilder coloredCompass = new StringBuilder();
    int currentIndex = 0;
    while (currentIndex < finalCompass.length()) {
      boolean matched = false;
      for (DirectionInfo direction : DIRECTIONS) {
        if (finalCompass.indexOf(direction.name, currentIndex) == currentIndex) {
          // Apply static color to the direction name
          coloredCompass.append(DIRECTION_COLOR).append(direction.name).append(ChatColor.RESET);
          currentIndex += direction.name.length();
          matched = true;
          break;
        }
      }
      if (!matched) {
        coloredCompass.append(finalCompass.charAt(currentIndex));
        currentIndex++;
      }
    }

    return coloredCompass.toString();
  }

  private static class DirectionInfo {
    String name;

    DirectionInfo(String name) {
      this.name = name;
    }
  }
}
