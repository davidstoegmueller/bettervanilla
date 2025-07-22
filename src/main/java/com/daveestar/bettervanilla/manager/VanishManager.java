package com.daveestar.bettervanilla.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class VanishManager {
  private final Main _plugin;
  private final Set<UUID> _vanished = new HashSet<>();

  public VanishManager() {
    _plugin = Main.getInstance();
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
    p.playerListName(Component.text(ChatColor.RED + " » " + ChatColor.YELLOW + p.getName()));
  }

  public boolean isVanished(Player p) {
    return _vanished.contains(p.getUniqueId());
  }

  public void onPlayerLeft(Player p) {
    if (isVanished(p)) {
      unvanish(p);
    }
  }
}
