package com.daveestar.bettervanilla.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.manager.VanishManager;
import com.daveestar.bettervanilla.utils.Theme;

public class VanishCommand implements CommandExecutor {
  private final VanishManager _vanishManager;

  public VanishCommand() {
    _vanishManager = Main.getInstance().getVanishManager();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (!(cs instanceof Player)) {
      cs.sendMessage(Main.getNoPlayerMessage());
      return true;
    }

    Player p = (Player) cs;

    if (!p.hasPermission(Permissions.VANISH.getName())) {
      p.sendMessage(Main.getNoPermissionMessage(p, Permissions.VANISH));
      return true;
    }

    if (_vanishManager.isVanished(p)) {
      _vanishManager.unvanish(p);
      p.sendMessage(Main.getPrefix() + Theme.primary() + Main.tr(p, "command-vanish-visible"));
    } else {
      _vanishManager.vanish(p);
      p.sendMessage(Main.getPrefix() + Theme.primary() + Main.tr(p, "command-vanish-hidden"));
    }

    return true;
  }
}
