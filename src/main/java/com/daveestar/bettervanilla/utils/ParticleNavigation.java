package com.daveestar.bettervanilla.utils;

import java.util.concurrent.TimeUnit;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.daveestar.bettervanilla.Main;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class ParticleNavigation {

  private final Main _plugin;

  private Player _player;
  private Location _location;
  private Color _color;

  private ScheduledTask _beamTask;
  private ScheduledTask _trailTask;

  private boolean _showBeam = false;
  private boolean _showTrail = false;

  public ParticleNavigation(Player p, Location location, Color color) {
    _plugin = Main.getInstance();

    _player = p;
    _location = location.toCenterLocation();
    _color = color;
  }

  public void update(Location newLocation, boolean showBeam, boolean showTrail) {
    _location = newLocation.toCenterLocation();
    _showBeam = showBeam;
    _showTrail = showTrail;
  }

  public void displayBeam() {
    _showBeam = true;

    AsyncScheduler scheduler = _plugin.getServer().getAsyncScheduler();
    World world = _location.getWorld();
    double maxHeight = world.getMaxHeight();

    _beamTask = scheduler.runAtFixedRate(_plugin, task -> {

      if (!_showBeam)
        return;

      // generate the beam effect upwards from the given location
      for (double y = 0; y <= maxHeight; y += 0.5) { // adjust y to control beam height
        Location particleLocation = _location.clone().add(0, y, 0);
        DustOptions options = new DustOptions(_color, 3);

        _player.spawnParticle(Particle.DUST, particleLocation, 1, 0.1, 0.1, 0.1, 0, options, true);
      }
    }, 0, 1, TimeUnit.SECONDS);
  }

  public void removeBeam() {
    if (_beamTask != null && !_beamTask.isCancelled()) {
      _beamTask.cancel();
      _beamTask = null;

      _showBeam = false;
    }
  }

  public void displayTrail() {
    _showTrail = true;

    AsyncScheduler scheduler = _plugin.getServer().getAsyncScheduler();

    _trailTask = scheduler.runAtFixedRate(_plugin, t -> {
      if (!_showTrail)
        return;

      Location start = _player.getLocation().clone().add(0, 0.5, 0);
      double distance = start.distance(_location);
      double maxDistance = Math.min(distance, 10);
      Vector direction = _location.toVector().subtract(start.toVector()).normalize();

      for (double d = 0; d <= maxDistance; d += 1) {
        Location point = start.clone().add(direction.clone().multiply(d));
        DustOptions options = new DustOptions(_color, 1);

        _player.spawnParticle(Particle.DUST, point, 1, 0, 0, 0, 0, options, true);
      }
    }, 0, 1, TimeUnit.SECONDS);
  }

  public void removeTrail() {
    if (_trailTask != null && !_trailTask.isCancelled()) {
      _trailTask.cancel();
      _trailTask = null;

      _showTrail = false;
    }
  }
}
