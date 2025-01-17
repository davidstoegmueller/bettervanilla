package com.daveestar.bettervanilla.utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.daveestar.bettervanilla.Main;

public class ParticleBeam {
  private Player _player;
  private Location _location;
  private Color _color;

  private BukkitTask _task;

  public ParticleBeam(Player player, Location location, Color color) {
    this._player = player;
    this._location = location;
    this._color = color;
  }

  /**
   * Starts the particle beam effect indefinitely.
   */
  public void displayBeam() {
    _task = new BukkitRunnable() {
      @Override
      public void run() {
        World world = _location.getWorld();
        double maxHeight = world.getMaxHeight();

        // Generate the beam effect upwards from the given location
        for (double y = 0; y <= maxHeight; y += 0.5) { // Adjust y to control beam height
          Location particleLocation = _location.clone().add(0, y, 0);
          DustOptions options = new DustOptions(_color, 3);

          _player.spawnParticle(Particle.DUST, particleLocation, 1, 0.1, 0.1, 0.1, 0, options, true);
        }
      }
    }.runTaskTimer(Main.getInstance(), 0, 20); // Run every 20 ticks (1 seconds)
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
