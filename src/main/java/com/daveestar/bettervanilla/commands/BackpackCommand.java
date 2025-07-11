package com.daveestar.bettervanilla.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.BackpackManager;

import net.md_5.bungee.api.ChatColor;

public class BackpackCommand implements CommandExecutor {
  private final BackpackManager _backpackManager;

  public BackpackCommand() {
    _backpackManager = Main.getInstance().getBackpackManager();
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return true;
    }

    Player p = (Player) sender;

    if (args.length > 0) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/backpack");
      return true;
    }

    _backpackManager.openBackpack(p);

    return true;
  }
}
