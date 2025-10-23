package com.daveestar.bettervanilla.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;

import net.kyori.adventure.text.Component;

public class VanishManager {
  private final Main _plugin;
  private final Map<UUID, VanishState> _vanishedStates = new HashMap<>();
  private TabListManager _tabListManager;

  public VanishManager() {
    _plugin = Main.getInstance();
  }

  public void vanish(Player p) {
    if (p == null) {
      return;
    }

    UUID uuid = p.getUniqueId();
    if (_vanishedStates.containsKey(uuid)) {
      return;
    }

    VanishState state = VanishState.capture(p);
    _vanishedStates.put(uuid, state);

    _applyVanishState(p);
    _hideFromOthers(p);
  }

  public void unvanish(Player p) {
    if (p == null) {
      return;
    }

    UUID uuid = p.getUniqueId();
    VanishState state = _vanishedStates.remove(uuid);
    if (state == null) {
      return;
    }

    _showToOthers(p);
    _restorePlayerState(p, state);

    TabListManager tabListManager = _getTabListManager();
    if (tabListManager != null) {
      tabListManager.refreshPlayer(p);
    }
  }

  public boolean handlePlayerJoin(Player p) {
    if (p == null) {
      return false;
    }

    UUID uuid = p.getUniqueId();
    boolean stillVanished = _vanishedStates.containsKey(uuid);

    if (stillVanished) {
      // reapply vanish effects for returning player
      _applyVanishState(p);
      _hideFromOthers(p);
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

    return _vanishedStates.containsKey(p.getUniqueId());
  }

  public boolean isVanished(UUID uuid) {
    return uuid != null && _vanishedStates.containsKey(uuid);
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

  private void _restorePlayerState(Player p, VanishState state) {
    p.setAllowFlight(state.allowFlight);
    if (state.allowFlight) {
      p.setFlying(state.flying);
    } else {
      p.setFlying(false);
    }

    p.setInvulnerable(state.invulnerable);
    p.setCollidable(state.collidable);
    p.setInvisible(state.invisible);
  }

  private Stream<Player> _onlineVanishedPlayers() {
    return _vanishedStates.keySet()
        .stream()
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

  private static final class VanishState {
    private final boolean allowFlight;
    private final boolean flying;
    private final boolean invulnerable;
    private final boolean collidable;
    private final boolean invisible;

    private VanishState(boolean allowFlight, boolean flying, boolean invulnerable, boolean collidable,
        boolean invisible) {
      this.allowFlight = allowFlight;
      this.flying = flying;
      this.invulnerable = invulnerable;
      this.collidable = collidable;
      this.invisible = invisible;
    }

    public static VanishState capture(Player p) {
      return new VanishState(
          p.getAllowFlight(),
          p.isFlying(),
          p.isInvulnerable(),
          p.isCollidable(),
          p.isInvisible());
    }
  }
}
