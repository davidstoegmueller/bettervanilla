package com.daveestar.bettervanilla.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class HereCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (!(cs instanceof org.bukkit.entity.Player)) {
      cs.sendMessage(Main.getNoPlayerMessage());
      return true;
    }

    Player p = (Player) cs;

    if (!p.hasPermission(Permissions.HERE.getName())) {
      p.sendMessage(Main.getNoPermissionMessage(Permissions.HERE));
      return true;
    }

    Location loc = p.getLocation();
    String worldName = loc.getWorld() != null ? loc.getWorld().getName() : "unknown";

    String messageText = Main.getPrefix()
        + ChatColor.YELLOW + p.getName() + ChatColor.GRAY + "'s location: "
        + ChatColor.GRAY + "World: " + ChatColor.YELLOW + worldName + ChatColor.GRAY + " | "
        + ChatColor.GRAY + "X: " + ChatColor.YELLOW + loc.getBlockX() + ChatColor.GRAY + " | "
        + ChatColor.GRAY + "Y: " + ChatColor.YELLOW + loc.getBlockY() + ChatColor.GRAY + " | "
        + ChatColor.GRAY + "Z: " + ChatColor.YELLOW + loc.getBlockZ();

    Component message = Component.text(messageText);
    Main.getInstance().getServer().sendMessage(message);

    return true;
  }
}
