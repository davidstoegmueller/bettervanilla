package com.daveestar.bettervanilla.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.daveestar.bettervanilla.Main;

public class PlayerHeadCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (c.getName().equalsIgnoreCase("playerhead") && cs instanceof Player) {
      Player p = (Player) cs;

      if (args.length == 1) {
        @SuppressWarnings("deprecation")
        OfflinePlayer target = (OfflinePlayer) Bukkit.getOfflinePlayer(args[0]);

        p.getInventory().addItem(_getPlayerHead(target));

        p.sendMessage(Main.getPrefix() + "You've recieved the player head of: " + ChatColor.YELLOW + args[0]);

      } else {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please use: " + ChatColor.YELLOW + "/playerhead <name>");
      }
      return true;
    }
    return false;
  }

  private ItemStack _getPlayerHead(OfflinePlayer offlinePlayer) {
    ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
    SkullMeta meta = (SkullMeta) playerHead.getItemMeta();

    meta.setOwningPlayer(offlinePlayer);

    playerHead.setItemMeta(meta);

    return playerHead;
  }
}
