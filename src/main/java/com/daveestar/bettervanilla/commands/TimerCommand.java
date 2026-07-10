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
import com.daveestar.bettervanilla.utils.Theme;

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
      p.sendMessage(Main.getPrefix() + Theme.error() + "The timer is already running.");
    } else {
      timer.setRunning(true);
      timer.setRunningOverride(true);
      p.sendMessage(Main.getPrefix() + "Timer has been resumed.");
    }
  }

  private void handlePause(Player p, TimerManager timer) {
    if (!timer.isRunning()) {
      p.sendMessage(Main.getPrefix() + Theme.error() + "No active timer to pause.");
    } else {
      timer.setRunning(false);
      timer.setRunningOverride(false);
      p.sendMessage(Main.getPrefix() + "Timer has been paused.");
    }
  }

  private void handleSet(Player p, TimerManager timer, String[] args) {
    if (args.length != 2) {
      p.sendMessage(Main.getPrefix() + Theme.error() + "Usage: " + Theme.highlight() + "/timer set <time>");
      return;
    }

    try {
      int time = Integer.parseInt(args[1]);
      timer.setRunning(false);
      timer.setRunningOverride(false);
      timer.setGlobalTimer(time);
      p.sendMessage(Main.getPrefix() + "Timer has been set to " + Theme.highlight() + time
          + Theme.primary() + " seconds.");
    } catch (NumberFormatException e) {
      p.sendMessage(Main.getPrefix() + Theme.error() + "Invalid number. Please provide a valid time in seconds.");
    }
  }

  private void handleReset(Player p, TimerManager timer) {
    timer.setRunning(false);
    timer.setRunningOverride(false);
    timer.setGlobalTimer(0);
    timer.resetPlayerTimers();
    p.sendMessage(
        Main.getPrefix() + "Timer has been reset to" + Theme.highlight() + " 0 " + Theme.primary() + "seconds.");
  }

  private void sendUsageMessage(Player p) {
    p.sendMessage(Main.getPrefix() + Theme.error() + "Usage: " + Theme.highlight()
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
