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

  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final ActionBar _actionBar;
  private final NavigationManager _navigationManager;

  public ToggleLocationCommand() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _actionBar = _plugin.getActionBar();
    _navigationManager = _plugin.getNavigationManager();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (cs instanceof Player) {
      Player p = (Player) cs;

      if (args.length == 0) {
        if (_settingsManager.getToggleLocation(p)) {
          _settingsManager.setToggleLocation(p, false);
          _actionBar.removeActionBar(p);
        } else {
          _navigationManager.stopNavigation(p);
          _settingsManager.setToggleLocation(p, true);

          Biome playerBiome = p.getWorld().getBiome(p.getLocation().toBlockLocation());

          String locationText = ChatColor.YELLOW + "X: "
              + ChatColor.GRAY
              + p.getLocation().toBlockLocation().getBlockX() + ChatColor.YELLOW
              + " Y: " + ChatColor.GRAY + p.getLocation().toBlockLocation().getBlockY() + ChatColor.YELLOW +
              " Z: " + ChatColor.GRAY
              + p.getLocation().toBlockLocation().getBlockZ() + ChatColor.RED + ChatColor.BOLD + " » "
              + ChatColor.GRAY + playerBiome.getKey();

          _actionBar.sendActionBar(p, locationText);
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
