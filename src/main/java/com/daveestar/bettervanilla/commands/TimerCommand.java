package com.daveestar.bettervanilla.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.models.TimerManager;

public class TimerCommand implements TabExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!command.getName().equalsIgnoreCase("timer") || !(sender instanceof Player)) {
      return false;
    }

    Player player = (Player) sender;
    TimerManager timer = Main.getInstance().getTimerManager();

    if (args.length == 0) {
      sendUsageMessage(player);
      return true;
    }

    switch (args[0].toLowerCase()) {
      case "resume":
        handleResume(player, timer);
        break;
      case "pause":
        handlePause(player, timer);
        break;
      case "set":
        handleSet(player, timer, args);
        break;
      case "reset":
        handleReset(player, timer);
        break;
      default:
        sendUsageMessage(player);
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
