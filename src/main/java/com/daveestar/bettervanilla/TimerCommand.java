package com.daveestar.bettervanilla;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimerCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {

    if (c.getName().equalsIgnoreCase("timer") && cs instanceof Player) {
      Player p = (Player) cs;

      if (args.length == 0) {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please use: " + ChatColor.YELLOW
            + "/timer <resume | pause | reset | set> [time]");

        return true;
      }

      // store the timer model to use later in switch case to handle different timer
      // arguments
      Timer timer = Main.getInstance().getTimer();

      switch (args[0].toLowerCase()) {
        case "resume":
          if (timer.isRunning()) {
            p.sendMessage(Main.getPrefix() + ChatColor.RED + "The timer is currently running.");
            break;
          }

          timer.setRunning(true);

          p.sendMessage(Main.getPrefix() + "Timer has been resumed.");

          break;
        case "pause":
          if (!timer.isRunning()) {
            p.sendMessage(Main.getPrefix() + ChatColor.RED + "Timer could not be paused. No Timer currently active.");
            break;
          }

          timer.setRunning(false);

          p.sendMessage(Main.getPrefix() + "Timer has been paused.");

          break;
        case "set":
          if (args.length != 2) {
            p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please use: " + ChatColor.YELLOW
                + "/timer set <time>");

            break;
          }

          try {
            timer.setRunning(false);
            timer.setTime(Integer.parseInt(args[1]));

            p.sendMessage(Main.getPrefix() + "Timer has been set to " + ChatColor.YELLOW + args[1] + ChatColor.GRAY
                + " seconds.");
          } catch (NumberFormatException ex) {
            p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please provide a number to set the current timer value.");
          }

          break;
        case "reset":
          timer.setRunning(false);
          timer.setTime(0);

          p.sendMessage(Main.getPrefix() + "Timer has been reseted to 0 seconds.");

          break;
        default:
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please use: " + ChatColor.YELLOW
              + "/timer <resume | pause | reset | set> [time]");
          break;
      }

      return true;

    }

    return false;
  }
}