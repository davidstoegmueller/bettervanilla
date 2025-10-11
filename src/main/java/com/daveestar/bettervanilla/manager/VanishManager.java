package com.daveestar.bettervanilla.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;

import net.kyori.adventure.text.Component;

public class VanishManager {
  private final Main _plugin;
  private final Set<UUID> _vanished = new HashSet<>();
  private TabListManager _tabListManager;

  public VanishManager() {
    _plugin = Main.getInstance();
    _tabListManager = _plugin.getTabListManager();
  }

  public void vanish(Player p) {
    if (_vanished.contains(p.getUniqueId())) {
      return;
    }

    _vanished.add(p.getUniqueId());
    _plugin.getServer().getOnlinePlayers().forEach(pl -> {
      if (!pl.equals(p)) {
        pl.hidePlayer(_plugin, p);
      }
    });

    p.setAllowFlight(true);
    p.setFlying(true);
    p.setInvulnerable(true);
    p.setCollidable(false);
    p.setInvisible(true);
    p.playerListName(Component.empty());
  }

  public void unvanish(Player p) {
    if (!_vanished.contains(p.getUniqueId())) {
      return;
    }

    _vanished.remove(p.getUniqueId());
    _plugin.getServer().getOnlinePlayers().forEach(pl -> {
      if (!pl.equals(p)) {
        pl.showPlayer(_plugin, p);
      }
    });

    p.setInvisible(false);
    p.setInvulnerable(false);
    p.setCollidable(true);
    p.setAllowFlight(false);

    _tabListManager.refreshPlayer(p);
  }

  public boolean isVanished(Player p) {
    return _vanished.contains(p.getUniqueId());
  }

  public int getVanishedCount() {
    return _vanished.size();
  }

  public void onPlayerLeft(Player p) {
    if (isVanished(p)) {
      unvanish(p);
    }
  }
}
