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
import com.daveestar.bettervanilla.models.NavigationManager;
import com.daveestar.bettervanilla.models.SettingsManager;
import com.daveestar.bettervanilla.utils.ActionBarManager;
import com.daveestar.bettervanilla.utils.Config;
import com.daveestar.bettervanilla.utils.NavigationData;

public class LastDeathCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (c.getName().equalsIgnoreCase("lastdeath") && cs instanceof Player) {
      Player p = (Player) cs;

      SettingsManager settingsManager = Main.getInstance().getSettingsManager();
      ActionBarManager actionBarManager = Main.getInstance().getActionBarManager();
      NavigationManager navigationManager = Main.getInstance().getNavigationManager();

      if (args.length == 0) {
        Config lastDeaths = new Config("lastDeaths.yml", Main.getInstance().getDataFolder());
        FileConfiguration cfgn = lastDeaths.getFileCfgrn();

        ConfigurationSection playerSection = cfgn.getConfigurationSection(p.getName());

        if (playerSection != null) {
          int locX = playerSection.getInt("x");
          int locY = playerSection.getInt("y");
          int locZ = playerSection.getInt("z");
          String world = playerSection.getString("world");

          Location lastDeathLocation = new Location(Bukkit.getWorld(world), locX, locY,
              locZ);

          settingsManager.setToggleLocation(p, false);

          NavigationData navigationData = new NavigationData("LAST DEATH", lastDeathLocation, Color.RED);
          navigationManager.startNavigation(p, navigationData);

        } else {
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "You have no last deathpoint!");
        }
      } else if (args.length == 1 && args[0].equalsIgnoreCase("cancel")) {
        if (navigationManager.checkActiveNavigation(p)) {
          navigationManager.stopNavigation(p);

          actionBarManager.sendActionBarOnce(p, ChatColor.RED + "You've canceled deathpoint navigation!");
        } else {
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "You are currently not navigating to your last deathpoint");
        }
      } else {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please use: " +
            ChatColor.YELLOW + "/lastdeath [cancel]");
      }

      return true;
    }

    return false;
  }
}