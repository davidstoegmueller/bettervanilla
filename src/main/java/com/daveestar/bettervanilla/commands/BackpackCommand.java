package com.daveestar.bettervanilla.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.BackpackManager;

public class BackpackCommand implements CommandExecutor {
  private final BackpackManager _backpackManager;

  public BackpackCommand() {
    _backpackManager = Main.getInstance().getBackpackManager();
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
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
