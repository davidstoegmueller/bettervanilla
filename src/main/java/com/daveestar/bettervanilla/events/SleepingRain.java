package com.daveestar.bettervanilla.events;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;

import io.papermc.paper.block.bed.BedEnterAction;
import io.papermc.paper.block.bed.BedRuleResult;

public class SleepingRain implements Listener {

  private final Main _plugin;
  private final SettingsManager _settingsManager;

  public SleepingRain() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
  }

  @EventHandler
  public void onPlayerBedEnter(PlayerBedEnterEvent e) {
    if (!_settingsManager.getSleepingRain()) {
      return;
    }

    BedEnterAction enterAction = e.enterAction();
    BedRuleResult sleepResult = enterAction.canSleep();

    if (enterAction.problem() != null || sleepResult.success()) {
      return;
    }

    Player p = e.getPlayer();
    World world = p.getWorld();

    if (world.getEnvironment() != Environment.NORMAL || !world.hasStorm()) {
      return;
    }

    world.setStorm(false);
    world.setTime(0);
    e.setUseBed(Result.ALLOW);

    p.sendMessage(Main.getPrefix() + "The weather has been cleared and you have slept through the night.");
  }
}
