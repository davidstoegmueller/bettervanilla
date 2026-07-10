package com.daveestar.bettervanilla.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.utils.Theme;

import net.kyori.adventure.text.Component;

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
        + Theme.highlight() + p.getName() + Theme.primary() + "'s location: "
        + Theme.primary() + "World: " + Theme.highlight() + worldName + Theme.primary() + " | "
        + Theme.primary() + "X: " + Theme.highlight() + loc.getBlockX() + Theme.primary() + " | "
        + Theme.primary() + "Y: " + Theme.highlight() + loc.getBlockY() + Theme.primary() + " | "
        + Theme.primary() + "Z: " + Theme.highlight() + loc.getBlockZ();

    Component message = Component.text(messageText);
    Main.getInstance().getServer().sendMessage(message);

    return true;
  }
}
