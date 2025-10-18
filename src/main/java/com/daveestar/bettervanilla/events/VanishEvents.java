package com.daveestar.bettervanilla.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.server.TabCompleteEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.VanishManager;

public class VanishEvents implements Listener {
  private final VanishManager _vanishManager;

  public VanishEvents() {
    _vanishManager = Main.getInstance().getVanishManager();
  }

  @EventHandler(ignoreCancelled = true)
  public void onDamage(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player p) {
      if (_vanishManager.isVanished(p)) {
        e.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onTabComplete(TabCompleteEvent e) {
    if (!(e.getSender() instanceof Player sender)) {
      return;
    }

    if (e.getCompletions().isEmpty()) {
      return;
    }

    e.getCompletions().removeIf(completion -> _shouldHideCompletion(sender, completion));
  }

  private boolean _shouldHideCompletion(Player viewer, String completion) {
    if (completion == null || completion.isEmpty()) {
      return false;
    }

    Player target = Bukkit.getPlayerExact(completion);
    if (target == null) {
      // no online player matches this completion
      return false;
    }

    if (target.getUniqueId().equals(viewer.getUniqueId())) {
      return false;
    }

    return _vanishManager.isVanished(target);
  }
}
