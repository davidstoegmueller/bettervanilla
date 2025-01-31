package com.daveestar.bettervanilla.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.NavigationType;
import com.daveestar.bettervanilla.manager.NavigationManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.ActionBar;
import com.daveestar.bettervanilla.utils.Config;
import com.daveestar.bettervanilla.utils.NavigationData;

import net.md_5.bungee.api.ChatColor;

public class LastDeathCommand implements TabExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (cs instanceof Player) {
      Player p = (Player) cs;

      SettingsManager settingsManager = Main.getInstance().getSettingsManager();
      ActionBar actionBarManager = Main.getInstance().getActionBar();
      NavigationManager navigationManager = Main.getInstance().getNavigationManager();

      if (args.length == 0) {
        Config lastDeaths = new Config("lastDeaths.yml", Main.getInstance().getDataFolder());
        FileConfiguration cfgn = lastDeaths.getFileCfgrn();

        ConfigurationSection playerSection = cfgn.getConfigurationSection(p.getUniqueId().toString());

        if (playerSection != null) {
          int locX = playerSection.getInt("x");
          int locY = playerSection.getInt("y");
          int locZ = playerSection.getInt("z");
          String world = playerSection.getString("world");

          Location lastDeathLocation = new Location(Bukkit.getWorld(world), locX, locY,
              locZ);

          settingsManager.setToggleLocation(p, false);

          NavigationData navigationData = new NavigationData("LAST DEATH", lastDeathLocation, NavigationType.WAYPOINT,
              Color.RED);
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
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/lastdeath [cancel]");
      }

      return true;
    }

    return false;
  }

  @Override
  public List<String> onTabComplete(CommandSender cs, Command c, String label, String[] args) {
    if (args.length == 1) {
      List<String> availableSettings = Arrays.asList("cancel");
      return availableSettings;
    }

    return new ArrayList<>();
  }
}