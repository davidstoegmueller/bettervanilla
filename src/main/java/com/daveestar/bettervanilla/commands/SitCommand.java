package com.daveestar.bettervanilla.commands;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.manager.SittingManager;
import com.daveestar.bettervanilla.utils.Theme;

public class SitCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (!(cs instanceof Player)) {
      cs.sendMessage(Main.getNoPlayerMessage());
      return true;
    }

    Player p = (Player) cs;

    if (!p.hasPermission(Permissions.SIT.getName())) {
      p.sendMessage(Main.getNoPermissionMessage(p, Permissions.SIT));
      return true;
    }

    SittingManager sittingManager = Main.getInstance().getSittingManager();

    if (sittingManager.isSitting(p)) {
      sittingManager.unsitPlayer(p);
      p.sendMessage(Main.getPrefix() + Main.tr(p, "command-sit-stood-up"));
      return true;
    }

    if (p.isInsideVehicle()) {
      p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "command-sit-error-in-vehicle"));
      return true;
    }

    Location playerLocation = p.getLocation();
    Block blockAtFeet = playerLocation.clone().subtract(0, 0.1, 0).getBlock();
    Block supportBlock = blockAtFeet.isPassable() ? blockAtFeet.getRelative(BlockFace.DOWN) : blockAtFeet;

    if (supportBlock == null || supportBlock.isPassable()) {
      p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "command-sit-error-solid-ground"));
      return true;
    }

    BoundingBox boundingBox = supportBlock.getBoundingBox();
    double seatY = boundingBox != null ? boundingBox.getMaxY() : supportBlock.getLocation().getY() + 1.0;
    double heightDelta = playerLocation.getY() - seatY;

    if (heightDelta > 0.1D) {
      p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "command-sit-error-solid-ground"));
      return true;
    }

    Location seatLocation = playerLocation.clone();
    seatLocation.setX(Math.floor(seatLocation.getX()) + 0.5);
    seatLocation.setZ(Math.floor(seatLocation.getZ()) + 0.5);
    seatLocation.setY(seatY + 0.02);
    seatLocation.setYaw(playerLocation.getYaw());
    seatLocation.setPitch(0f);

    sittingManager.sitPlayer(p, seatLocation);
    p.sendMessage(Main.getPrefix() + Main.tr(p, "command-sit-sat-down"));

    return true;
  }
}
