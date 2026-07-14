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
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class HereCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (!(cs instanceof org.bukkit.entity.Player)) {
      cs.sendMessage(Main.getNoPlayerMessage());
      return true;
    }

    Player p = (Player) cs;

    if (!p.hasPermission(Permissions.HERE.getName())) {
      p.sendMessage(Main.getNoPermissionMessage(p, Permissions.HERE));
      return true;
    }

    Location loc = p.getLocation();
    String navigationCommand = "/waypoints coords " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();

    for (Player recipient : Main.getInstance().getServer().getOnlinePlayers()) {
      String worldName = loc.getWorld() != null
          ? loc.getWorld().getName()
          : Main.tr(recipient, "common-world-unknown");
      String messageText = Main.getPrefix() + Main.tr(recipient, "command-here-broadcast",
          "player", Theme.highlight() + p.getName() + Theme.primary(),
          "world", Theme.highlight() + worldName + Theme.primary(),
          "x", Theme.highlight().toString() + loc.getBlockX() + Theme.primary(),
          "y", Theme.highlight().toString() + loc.getBlockY() + Theme.primary(),
          "z", Theme.highlight().toString() + loc.getBlockZ() + Theme.primary());
      Component message = LegacyComponentSerializer.legacySection().deserialize(messageText)
          .clickEvent(ClickEvent.runCommand(navigationCommand))
          .hoverEvent(HoverEvent.showText(Component.text(Main.tr(recipient, "command-here-hover"))));
      recipient.sendMessage(message);
    }

    return true;
  }
}
