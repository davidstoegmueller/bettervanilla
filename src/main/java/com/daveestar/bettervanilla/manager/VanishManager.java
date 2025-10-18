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

  public void vanish(Player player) {
    if (player == null) {
      return;
    }

    UUID uuid = player.getUniqueId();
    if (_vanishedStates.containsKey(uuid)) {
      return;
    }

    VanishState state = VanishState.capture(player);
    _vanishedStates.put(uuid, state);

    _applyVanishState(player);
    _hideFromOthers(player);
  }

  public void unvanish(Player player) {
    if (player == null) {
      return;
    }

    UUID uuid = player.getUniqueId();
    VanishState state = _vanishedStates.remove(uuid);
    if (state == null) {
      return;
    }

    _showToOthers(player);
    _restorePlayerState(player, state);

    TabListManager tabListManager = _getTabListManager();
    if (tabListManager != null) {
      tabListManager.refreshPlayer(player);
    }
  }

  public boolean handlePlayerJoin(Player player) {
    if (player == null) {
      return false;
    }

    UUID uuid = player.getUniqueId();
    boolean stillVanished = _vanishedStates.containsKey(uuid);

    if (stillVanished) {
      // reapply vanish effects for returning player
      _applyVanishState(player);
      _hideFromOthers(player);
    }

    // ensure the joining player cannot see currently vanished players
    _onlineVanishedPlayers()
        .filter(other -> !other.equals(player))
        .forEach(other -> player.hidePlayer(_plugin, other));

    return stillVanished;
  }

  public boolean isVanished(Player player) {
    if (player == null) {
      return false;
    }

    return _vanishedStates.containsKey(player.getUniqueId());
  }

  public boolean isVanished(UUID uuid) {
    return uuid != null && _vanishedStates.containsKey(uuid);
  }

  public int getVanishedCount() {
    return (int) _onlineVanishedPlayers().count();
  }

  private void _applyVanishState(Player player) {
    player.setAllowFlight(true);
    player.setFlying(true);
    player.setInvulnerable(true);
    player.setCollidable(false);
    player.setInvisible(true);
    player.playerListName(Component.empty());
  }

  private void _hideFromOthers(Player player) {
    _plugin.getServer().getOnlinePlayers().forEach(other -> {
      if (!other.equals(player)) {
        other.hidePlayer(_plugin, player);
      }
    });
  }

  private void _showToOthers(Player player) {
    _plugin.getServer().getOnlinePlayers().forEach(other -> {
      if (!other.equals(player)) {
        other.showPlayer(_plugin, player);
      }
    });
  }

  private void _restorePlayerState(Player player, VanishState state) {
    player.setAllowFlight(state.allowFlight);
    if (state.allowFlight) {
      player.setFlying(state.flying);
    } else {
      player.setFlying(false);
    }

    player.setInvulnerable(state.invulnerable);
    player.setCollidable(state.collidable);
    player.setInvisible(state.invisible);
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

    public static VanishState capture(Player player) {
      return new VanishState(
          player.getAllowFlight(),
          player.isFlying(),
          player.isInvulnerable(),
          player.isCollidable(),
          player.isInvisible());
    }
  }
}
