package com.daveestar.bettervanilla.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.models.WaypointsManager;
import com.daveestar.bettervanilla.utils.Config;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class LastDeathCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (c.getName().equalsIgnoreCase("lastdeath") && cs instanceof Player) {
      Player p = (Player) cs;

      WaypointsManager waypointsManager = Main.getInstance().get_waypointsManager();

      if (args.length == 0) {
        Config lastDeaths = new Config("lastDeaths.yml", Main.getInstance().getDataFolder());
        FileConfiguration cfgn = lastDeaths.getFileCfgrn();

        ConfigurationSection playerSection = cfgn.getConfigurationSection(p.getName());

        if (playerSection != null) {
          int locX = playerSection.getInt("x");
          int locY = playerSection.getInt("y");
          int locZ = playerSection.getInt("z");
          String world = playerSection.getString("world");

          Location lastDeathLoc = new Location(Bukkit.getWorld(world), locX, locY, locZ);

          if (waypointsManager.checkPlayerActiveWaypointNavigation(p)) {
            waypointsManager.removePlayerActiveWaypointNavigation(p);
          }

          if (waypointsManager.checkPlayerActiveToggleLocationNavigation(p)) {
            waypointsManager.removePlayerActiveToggleLocationNavigation(p);
          }

          waypointsManager.addPlayerActiveWaypointNavigation(p, lastDeathLoc, "LAST DEATH", Color.RED);

          int pLocX = p.getLocation().getBlockX();
          int pLocY = p.getLocation().getBlockY();
          int pLocZ = p.getLocation().getBlockZ();

          String displayCoordsWp = ChatColor.YELLOW + "" + ChatColor.BOLD + "LAST DEATH: "
              + ChatColor.RESET
              + ChatColor.YELLOW
              + "X: " + ChatColor.GRAY
              + locX + ChatColor.YELLOW
              + " Y: " + ChatColor.GRAY + locY + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + locZ;

          String displayCoordsCurrent = ChatColor.RED + "" + ChatColor.BOLD + " » " + ChatColor.YELLOW
              + ChatColor.BOLD
              + "CURRENT: " + ChatColor.RESET + ChatColor.YELLOW + "X: "
              + ChatColor.GRAY
              + pLocX + ChatColor.YELLOW
              + " Y: " + ChatColor.GRAY + pLocY + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + pLocZ;

          String displayText = displayCoordsWp + displayCoordsCurrent;

          waypointsManager.displayActionBar(p, displayText);
        } else {
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "You have no last death point!");
        }
      } else if (args.length == 1 && args[0].equalsIgnoreCase("cancel")) {
        if (waypointsManager.checkPlayerActiveWaypointNavigation(p)) {
          waypointsManager.removePlayerActiveWaypointNavigation(p);

          p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
              ChatColor.RED + "You've canceled navigation!"));
        } else {
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "You have no current destination!");
        }
      } else {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please use: " + ChatColor.YELLOW + "/lastdeath [cancel]");
      }

      return true;
    }
    return false;
  }
}
