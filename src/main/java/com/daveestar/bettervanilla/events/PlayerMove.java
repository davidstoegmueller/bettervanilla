package com.daveestar.bettervanilla.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.models.WaypointsManager;
import com.daveestar.bettervanilla.utils.LocationStorage;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerMove implements Listener {
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e) {
    Player p = (Player) e.getPlayer();

    WaypointsManager waypointsManager = Main.getInstance().getWaypointsManager();

    if (waypointsManager.checkPlayerActiveWaypointNavigation(p)) {
      int locX = p.getLocation().getBlockX();
      int locY = p.getLocation().getBlockY();
      int locZ = p.getLocation().getBlockZ();

      LocationStorage locationName = waypointsManager.getPlayerActiveWaypointNavigation(p);

      if (locationName.getCoordinates().getWorld().getName() != p.getWorld().getName()) {
        waypointsManager.removePlayerActiveWaypointNavigation(p);

        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Your navigation has been canceled due to world change!");

        return;
      }

      if (p.getLocation().distance(locationName.getCoordinates()) <= 25) {
        waypointsManager.removePlayerActiveWaypointNavigation(p);

        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
            ChatColor.YELLOW + "" + ChatColor.BOLD + locationName.getName() + ChatColor.GRAY
                + " is in a range of 25 blocks!"));

        return;
      }

      int wpX = locationName.getCoordinates().getBlockX();
      int wpY = locationName.getCoordinates().getBlockY();
      int wpZ = locationName.getCoordinates().getBlockZ();

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

      waypointsManager.displayActionBar(p, displayText);
    } else if (waypointsManager.checkPlayerActiveToggleLocationNavigation(p)) {
      String displayCoordsCurrent = ChatColor.YELLOW + "X: "
          + ChatColor.GRAY
          + p.getLocation().getBlockX() + ChatColor.YELLOW
          + " Y: " + ChatColor.GRAY + p.getLocation().getBlockY() + ChatColor.YELLOW + " Z: " + ChatColor.GRAY
          + p.getLocation().getBlockZ();

      waypointsManager.displayActionBar(p, displayCoordsCurrent);
    }
  }
}
