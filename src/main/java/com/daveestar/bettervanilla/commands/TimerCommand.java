package com.daveestar.bettervanilla.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.manager.TimerManager;

import net.md_5.bungee.api.ChatColor;

public class TimerCommand implements TabExecutor {

  private final Main _plugin;
  private final TimerManager _timerManager;

  public TimerCommand() {
    _plugin = Main.getInstance();
    _timerManager = _plugin.getTimerManager();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (!(cs instanceof Player)) {
      cs.sendMessage(Main.getNoPlayerMessage());
      return true;
    }

    Player p = (Player) cs;

    if (!p.hasPermission(Permissions.TIMER.getName())) {
      p.sendMessage(Main.getNoPermissionMessage(Permissions.TIMER));
      return true;
    }

    if (args.length == 0) {
      sendUsageMessage(p);
      return true;
    }

    switch (args[0].toLowerCase()) {
      case "resume":
        handleResume(p, _timerManager);
        break;
      case "pause":
        handlePause(p, _timerManager);
        break;
      case "set":
        handleSet(p, _timerManager, args);
        break;
      case "reset":
        handleReset(p, _timerManager);
        break;
      default:
        sendUsageMessage(p);
        break;
    }
    return true;
  }

  private void handleResume(Player p, TimerManager timer) {
    if (timer.isRunning()) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "The timer is already running.");
    } else {
      timer.setRunning(true);
      timer.setRunningOverride(true);
      p.sendMessage(Main.getPrefix() + "Timer has been resumed.");
    }
  }

  private void handlePause(Player p, TimerManager timer) {
    if (!timer.isRunning()) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "No active timer to pause.");
    } else {
      timer.setRunning(false);
      timer.setRunningOverride(false);
      p.sendMessage(Main.getPrefix() + "Timer has been paused.");
    }
  }

  private void handleSet(Player p, TimerManager timer, String[] args) {
    if (args.length != 2) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/timer set <time>");
      return;
    }

    try {
      int time = Integer.parseInt(args[1]);
      timer.setRunning(false);
      timer.setRunningOverride(false);
      timer.setGlobalTimer(time);
      p.sendMessage(Main.getPrefix() + "Timer has been set to " + ChatColor.YELLOW + time
          + ChatColor.GRAY + " seconds.");
    } catch (NumberFormatException e) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Invalid number. Please provide a valid time in seconds.");
    }
  }

  private void handleReset(Player p, TimerManager timer) {
    timer.setRunning(false);
    timer.setRunningOverride(false);
    timer.setGlobalTimer(0);
    timer.resetPlayerTimers();
    p.sendMessage(
        Main.getPrefix() + "Timer has been reset to" + ChatColor.YELLOW + " 0 " + ChatColor.GRAY + "seconds.");
  }

  private void sendUsageMessage(Player p) {
    p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW
        + "/timer <resume | pause | reset | set> [time]");
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    if (args.length == 1) {
      return Arrays.asList("resume", "pause", "reset", "set");
    }
    return new ArrayList<>();
  }
}
