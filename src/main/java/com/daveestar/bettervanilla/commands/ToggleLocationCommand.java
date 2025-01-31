package com.daveestar.bettervanilla.commands;

import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.NavigationManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.ActionBar;

import net.md_5.bungee.api.ChatColor;

public class ToggleLocationCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (cs instanceof Player) {
      Player p = (Player) cs;

      SettingsManager settingsManager = Main.getInstance().getSettingsManager();
      ActionBar actionBarManager = Main.getInstance().getActionBar();
      NavigationManager navigationManager = Main.getInstance().getNavigationManager();

      if (args.length == 0) {
        if (settingsManager.getToggleLocation(p)) {
          settingsManager.setToggleLocation(p, false);
          actionBarManager.removeActionBar(p);
        } else {
          navigationManager.stopNavigation(p);
          settingsManager.setToggleLocation(p, true);

          Biome playerBiome = p.getWorld().getBiome(p.getLocation());

          String locationText = ChatColor.YELLOW + "X: "
              + ChatColor.GRAY
              + p.getLocation().getBlockX() + ChatColor.YELLOW
              + " Y: " + ChatColor.GRAY + p.getLocation().getBlockY() + ChatColor.YELLOW +
              " Z: " + ChatColor.GRAY
              + p.getLocation().getBlockZ() + ChatColor.RED + ChatColor.BOLD + " » "
              + ChatColor.GRAY + playerBiome.getKey();

          actionBarManager.sendActionBar(p, locationText);
        }
      } else {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " +
            ChatColor.YELLOW + "/togglelocation");
      }

      return true;
    }

    return false;
  }
}
