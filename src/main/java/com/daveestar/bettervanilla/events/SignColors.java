package com.daveestar.bettervanilla.events;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.papermc.paper.event.player.PlayerOpenSignEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class SignColors implements Listener {
  private static final int SIGN_LINES = 4;
  private static final LegacyComponentSerializer AMPERSAND_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
  private static final PlainTextComponentSerializer PLAIN_SERIALIZER = PlainTextComponentSerializer.plainText();

  private final Map<UUID, Location> _trackedSigns = new ConcurrentHashMap<>();

  @EventHandler
  public void onSignChange(SignChangeEvent e) {
    _applyLegacyColors(e);

    Player p = e.getPlayer();
    Location signLocation = e.getBlock().getLocation().toBlockLocation();

    _clearTrackedSignAt(p, signLocation);
  }

  @EventHandler
  public void onSignOpen(PlayerOpenSignEvent e) {
    if (e.isCancelled()) {
      return;
    }

    Player p = e.getPlayer();
    _clearTrackedSignForPlayer(p);
    _sendLegacySign(p, e.getSign(), e.getSide());
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent e) {
    _trackedSigns.remove(e.getPlayer().getUniqueId());
  }

  private void _applyLegacyColors(SignChangeEvent e) {
    int lines = Math.min(SIGN_LINES, e.lines().size());

    for (int i = 0; i < lines; i++) {
      Component current = e.line(i);
      e.line(i, AMPERSAND_SERIALIZER.deserialize(_plainText(current)));
    }
  }

  private void _sendLegacySign(Player p, Sign sign, Side side) {
    Location signLocation = sign.getLocation().toBlockLocation();
    Sign snapshot = (Sign) sign.getBlock().getState();
    SignSide previewSide = snapshot.getSide(side);
    SignSide editingSide = sign.getSide(side);

    for (int i = 0; i < SIGN_LINES; i++) {
      Component lineComponent = editingSide.line(i);
      String legacyLine = _legacyText(lineComponent);
      previewSide.line(i, Component.text(legacyLine));
    }

    p.sendBlockUpdate(signLocation, snapshot);
    _trackedSigns.put(p.getUniqueId(), signLocation);
  }

  private void _clearTrackedSignForPlayer(Player p) {
    UUID uuid = p.getUniqueId();
    Location signLocation = _trackedSigns.remove(uuid);

    if (signLocation == null) {
      return;
    }

    _sendActualSign(p, signLocation);
  }

  private void _clearTrackedSignAt(Player p, Location location) {
    UUID uuid = p.getUniqueId();
    Location signLocation = _trackedSigns.get(uuid);

    if (signLocation == null || !_isSameLocation(signLocation, location)) {
      return;
    }

    _trackedSigns.remove(uuid);
    _sendActualSign(p, location);
  }

  private void _sendActualSign(Player p, Location location) {
    if (location == null || location.getWorld() == null) {
      return;
    }

    if (!(location.getBlock().getState() instanceof Sign signState)) {
      return;
    }

    p.sendBlockUpdate(location, signState);
  }

  private boolean _isSameLocation(Location left, Location right) {
    if (left == null || right == null) {
      return false;
    }

    if (left.getWorld() == null || right.getWorld() == null) {
      return false;
    }

    return left.getWorld().getUID().equals(right.getWorld().getUID())
        && left.getBlockX() == right.getBlockX()
        && left.getBlockY() == right.getBlockY()
        && left.getBlockZ() == right.getBlockZ();
  }

  private String _plainText(Component component) {
    return component == null ? "" : PLAIN_SERIALIZER.serialize(component);
  }

  private String _legacyText(Component component) {
    return component == null ? "" : AMPERSAND_SERIALIZER.serialize(component);
  }

}
