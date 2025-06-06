package com.daveestar.bettervanilla.utils;

import java.util.concurrent.TimeUnit;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class ParticleBeam {

  private final Main _plugin;

  private Player _player;
  private Location _location;
  private Color _color;

  private ScheduledTask _task;

  public ParticleBeam(Player p, Location location, Color color) {
    _plugin = Main.getInstance();

    _player = p;
    _location = location;
    _color = color;
  }

  public void updateLocation(Location newLocation) {
    _location = newLocation;
  }

  /**
   * Starts the particle beam effect indefinitely.
   */
  public void displayBeam() {

    AsyncScheduler scheduler = _plugin.getServer().getAsyncScheduler();
    _task = scheduler.runAtFixedRate(_plugin, task -> {
      World world = _location.getWorld();
      double maxHeight = world.getMaxHeight();

      // generate the beam effect upwards from the given location
      for (double y = 0; y <= maxHeight; y += 0.5) { // adjust y to control beam height
        Location particleLocation = _location.clone().add(0, y, 0);
        DustOptions options = new DustOptions(_color, 3);

        _player.spawnParticle(Particle.DUST, particleLocation, 1, 0.1, 0.1, 0.1, 0, options, true);
      }
    }, 0, 1, TimeUnit.SECONDS);
  }

  /**
   * Cancels the particle beam effect.
   */
  public void removeBeam() {
    if (_task != null && !_task.isCancelled()) {
      _task.cancel();
      _task = null;
    }
  }
}
