package com.daveestar.bettervanilla;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import com.daveestar.bettervanilla.utils.LocationName;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerMove implements Listener {
  public static BukkitScheduler waypointScheduler = Bukkit.getScheduler();
  public static HashMap<Player, BukkitTask> waypointsTasks = new HashMap<Player, BukkitTask>();

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e) {
    Player p = (Player) e.getPlayer();

    if (WaypointsCommand.showWaypointCoords.containsKey(p)) {
      int locX = p.getLocation().getBlockX();
      int locY = p.getLocation().getBlockY();
      int locZ = p.getLocation().getBlockZ();

      LocationName locationName = WaypointsCommand.showWaypointCoords.get(p);

      if (locationName.getLoc().getWorld().getName() != p.getWorld().getName()) {
        WaypointsCommand.showWaypointCoords.remove(p);
        cancelTask(p);
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Your navigation has been canceled due to world change!");

        return;
      }

      if (p.getLocation().distance(locationName.getLoc()) <= 25) {
        WaypointsCommand.showWaypointCoords.remove(p);
        cancelTask(p);

        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
            ChatColor.YELLOW + "" + ChatColor.BOLD + locationName.getName() + ChatColor.GRAY
                + " is in a range of 25 blocks!"));

        return;
      }

      int wpX = locationName.getLoc().getBlockX();
      int wpY = locationName.getLoc().getBlockY();
      int wpZ = locationName.getLoc().getBlockZ();

      String displayCoordsWp = ChatColor.YELLOW + "" + ChatColor.BOLD + locationName.getName().toUpperCase() + ": "
          + ChatColor.RESET
          + ChatColor.YELLOW
          + "X: " + ChatColor.GRAY
          + wpX + ChatColor.YELLOW
          + " Y: " + ChatColor.GRAY + wpY + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + wpZ;

      String displayCoordsCurrent = ChatColor.RED + "" + ChatColor.BOLD + " | " + ChatColor.YELLOW + ChatColor.BOLD
          + "CURRENT: " + ChatColor.RESET + ChatColor.YELLOW + "X: "
          + ChatColor.GRAY
          + locX + ChatColor.YELLOW
          + " Y: " + ChatColor.GRAY + locY + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + locZ;

      String displayText = displayCoordsWp + displayCoordsCurrent;

      displayActionBar(p, displayText);
    } else if (ToggleLocationCommand.showLocation.containsKey(p)) {
      String displayCoordsCurrent = ChatColor.YELLOW + "X: "
          + ChatColor.GRAY
          + p.getLocation().getBlockX() + ChatColor.YELLOW
          + " Y: " + ChatColor.GRAY + p.getLocation().getBlockY() + ChatColor.YELLOW + " Z: " + ChatColor.GRAY
          + p.getLocation().getBlockZ();

      displayActionBar(p, displayCoordsCurrent);
    }
  }

  public static void displayActionBar(Player p, String text) {
    cancelTask(p);

    BukkitTask task = waypointScheduler.runTaskTimerAsynchronously(Main.getInstance(), new Runnable() {
      public void run() {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
      }
    }, 0, 3 * 10);

    waypointsTasks.put(p, task);
  }

  public static void cancelTask(Player p) {
    if (waypointsTasks.containsKey(p)) {
      waypointsTasks.get(p).cancel();
      waypointsTasks.remove(p);
    }
  }
}
