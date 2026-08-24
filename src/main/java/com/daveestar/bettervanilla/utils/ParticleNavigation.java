package com.daveestar.bettervanilla.utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.daveestar.bettervanilla.Main;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class ParticleNavigation {

  private final Main _plugin;

  private final Player _player;
  private volatile Location _location;
  private final Color _color;

  private ScheduledTask _beamTask;
  private ScheduledTask _trailTask;

  private volatile boolean _showBeam = false;
  private volatile boolean _showTrail = false;

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

    _beamTask = _player.getScheduler().runAtFixedRate(_plugin, task -> {

      if (!_showBeam)
        return;

      Location target = _location.clone();
      if (_player.getWorld() != target.getWorld())
        return;

      double maxHeight = target.getWorld().getMaxHeight();

      // generate the beam effect upwards from the given location
      for (double y = 0; y <= maxHeight; y += 0.5) { // adjust y to control beam height
        Location particleLocation = target.clone().add(0, y, 0);
        DustOptions options = new DustOptions(_color, 3);

        _player.spawnParticle(Particle.DUST, particleLocation, 1, 0.1, 0.1, 0.1, 0, options, true);
      }
    }, null, 1, 20);
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

    _trailTask = _player.getScheduler().runAtFixedRate(_plugin, t -> {
      if (!_showTrail)
        return;

      Location start = _player.getLocation().clone().add(0, 0.5, 0);
      Location target = _location.clone();

      if (start.getWorld() != target.getWorld())
        return;

      double distance = start.distance(target);
      double maxDistance = Math.min(distance, 10);
      Vector direction = target.toVector().subtract(start.toVector()).normalize();

      for (double d = 0; d <= maxDistance; d += 1) {
        Location point = start.clone().add(direction.clone().multiply(d));
        DustOptions options = new DustOptions(_color, 1);

        _player.spawnParticle(Particle.DUST, point, 1, 0, 0, 0, 0, options, true);
      }
    }, null, 1, 20);
  }

  public void removeTrail() {
    if (_trailTask != null && !_trailTask.isCancelled()) {
      _trailTask.cancel();
      _trailTask = null;

      _showTrail = false;
    }
  }
}
