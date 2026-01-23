package com.daveestar.bettervanilla.manager;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.daveestar.bettervanilla.Main;

import net.kyori.adventure.text.Component;

public class VanishManager {
  private final Main _plugin;
  private final Set<UUID> _vanishedPlayers = new HashSet<>();
  private TabListManager _tabListManager;

  public VanishManager() {
    _plugin = Main.getInstance();
  }

  public void vanish(Player p) {
    if (p == null) {
      return;
    }

    UUID uuid = p.getUniqueId();
    if (_vanishedPlayers.contains(uuid)) {
      return;
    }

    _vanishedPlayers.add(uuid);

    _applyVanishState(p);
    _hideFromOthers(p);
    _refreshTabList(p);
  }

  public void unvanish(Player p) {
    if (p == null) {
      return;
    }

    UUID uuid = p.getUniqueId();
    boolean wasVanished = _vanishedPlayers.remove(uuid);
    if (!wasVanished) {
      return;
    }

    _showToOthers(p);
    _applyVisibleState(p);
    _refreshTabList(p);
  }

  public boolean handlePlayerJoin(Player p) {
    if (p == null) {
      return false;
    }

    UUID uuid = p.getUniqueId();
    boolean stillVanished = _vanishedPlayers.contains(uuid);

    if (stillVanished) {
      // reapply vanish effects for returning player
      _applyVanishState(p);
      _hideFromOthers(p);
      _refreshTabList(p);
    }

    // ensure the joining player cannot see currently vanished players
    _onlineVanishedPlayers()
        .filter(other -> !other.equals(p))
        .forEach(other -> p.hidePlayer(_plugin, other));

    return stillVanished;
  }

  public boolean isVanished(Player p) {
    if (p == null) {
      return false;
    }

    return _vanishedPlayers.contains(p.getUniqueId());
  }

  public boolean isVanished(UUID uuid) {
    return uuid != null && _vanishedPlayers.contains(uuid);
  }

  public int getVanishedCount() {
    return (int) _onlineVanishedPlayers().count();
  }

  private void _applyVanishState(Player p) {
    p.setAllowFlight(true);
    p.setFlying(true);
    p.setInvulnerable(true);
    p.setCollidable(false);
    p.setInvisible(true);
    p.playerListName(Component.empty());
  }

  private void _applyVisibleState(Player p) {
    boolean canFly = p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR;

    p.setAllowFlight(canFly);
    if (!canFly) {
      p.setFlying(false);
    }

    p.setInvulnerable(false);
    p.setCollidable(true);
    if (!p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
      p.setInvisible(false);
    }
  }

  private void _hideFromOthers(Player p) {
    _plugin.getServer().getOnlinePlayers().forEach(other -> {
      if (!other.equals(p)) {
        other.hidePlayer(_plugin, p);
      }
    });
  }

  private void _showToOthers(Player p) {
    _plugin.getServer().getOnlinePlayers().forEach(other -> {
      if (!other.equals(p)) {
        other.showPlayer(_plugin, p);
      }
    });
  }

  private Stream<Player> _onlineVanishedPlayers() {
    return _vanishedPlayers.stream()
        .map(Bukkit::getPlayer)
        .filter(Objects::nonNull)
        .filter(Player::isOnline);
  }

  private TabListManager _getTabListManager() {
    if (_tabListManager == null) {
      _tabListManager = _plugin.getTabListManager();
    }

    return _tabListManager;
  }

  private void _refreshTabList(Player p) {
    TabListManager tabListManager = _getTabListManager();
    if (tabListManager != null) {
      tabListManager.refreshPlayer(p);
    }
  }

}
