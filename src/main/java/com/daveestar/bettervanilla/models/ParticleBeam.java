package com.daveestar.bettervanilla.models;

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
  private Player player;
  private Location location;
  private Color color;

  private BukkitTask task;

  public ParticleBeam(Player player, Location location, Color color) {
    this.player = player;
    this.location = location;
    this.color = color;
  }

  /**
   * Starts the particle beam effect indefinitely.
   */
  public void displayBeam() {
    task = new BukkitRunnable() {
      @Override
      public void run() {
        World world = location.getWorld();
        double maxHeight = world.getMaxHeight();

        // Generate the beam effect upwards from the given location
        for (double y = 0; y <= maxHeight; y += 0.5) { // Adjust y to control beam height
          Location particleLocation = location.clone().add(0, y, 0);
          DustOptions options = new DustOptions(color, 3);

          player.spawnParticle(Particle.DUST, particleLocation, 1, 0.1, 0.1, 0.1, 0, options, true);
        }
      }
    }.runTaskTimer(Main.getInstance(), 0, 20); // Run every 20 ticks (1 seconds)
  }

  /**
   * Cancels the particle beam effect.
   */
  public void removeBeam() {
    if (task != null && !task.isCancelled()) {
      task.cancel();
      task = null;
    }
  }
}
