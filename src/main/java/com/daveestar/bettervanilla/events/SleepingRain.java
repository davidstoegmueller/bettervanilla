package com.daveestar.bettervanilla.events;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;

import com.daveestar.bettervanilla.Main;

public class SleepingRain implements Listener {

  @EventHandler
  public void onPlayerBedEnter(PlayerBedEnterEvent e) {
    if (e.getBedEnterResult() == BedEnterResult.NOT_POSSIBLE_NOW) {
      Player p = e.getPlayer();
      World world = p.getWorld();

      if (world.getEnvironment() == Environment.NORMAL && world.hasStorm()) {
        world.setStorm(false);
        world.setTime(0);
        e.setUseBed(Result.ALLOW);
        p.sendMessage(Main.getPrefix() + "The weather has been cleared and you have slept through the night.");
      }
    }
  }
}
