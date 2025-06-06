package com.daveestar.bettervanilla.events;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Stairs.Half;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.daveestar.bettervanilla.Main;

public class SittableStairs implements Listener {
  private static final double MAX_DISTANCE_SQUARED = 9.0; // 3 block range
  private final HashMap<Player, ArmorStand> _sittingPlayers = new HashMap<>();

  @EventHandler
  public void onPlayerRightClick(PlayerInteractEvent e) {
    // get the clicked block
    Block clickedBlock = e.getClickedBlock();

    if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
      if (clickedBlock != null && clickedBlock.getType().toString().contains("STAIRS")) {
        Player p = e.getPlayer();

        // only allow to sit on a chair with an empty hand
        if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
          // range check
          if (p.getLocation().distanceSquared(clickedBlock.getLocation()) > MAX_DISTANCE_SQUARED) {
            p.sendMessage(Main.getPrefix() + "You're too far away from that stair.");
            return;
          }

          // validate stair orientation
          if (clickedBlock.getBlockData() instanceof Stairs) {
            Stairs stairs = (Stairs) clickedBlock.getBlockData();
            if (stairs.getHalf() != Half.BOTTOM) {
              p.sendMessage(Main.getPrefix() + "You can only sit on bottom stairs.");
              return;
            }
          }

          // unmount before mounting again to a chair (stair)
          _unmountFromStair(p);

          // create armor stand at the block location for the player to sit on
          Location location = clickedBlock.getLocation().toBlockLocation().add(0.5, 0.1, 0.5);

          // adjust direction based on the lower side of the stairs
          if (clickedBlock.getBlockData() instanceof Stairs) {
            Stairs stairs = (Stairs) clickedBlock.getBlockData();
            switch (stairs.getFacing()) {
              case NORTH:
                location.setYaw(180f); // face south
                break;
              case SOUTH:
                location.setYaw(0f); // face north
                break;
              case EAST:
                location.setYaw(270f); // face west
                break;
              case WEST:
                location.setYaw(90f); // face east
                break;
              default:
                break;
            }
          }

          _mountToStair(p, location);

          p.sendMessage(Main.getPrefix() + "Well have a rest. Stand up using the 'Shift' key.");
        }
      }
    }
  }

  @EventHandler
  public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
    Player p = e.getPlayer();
    _unmountFromStair(p);
  }

  private void _mountToStair(Player p, Location location) {
    ArmorStand armorStand = p.getWorld().spawn(location, ArmorStand.class);
    armorStand.setVisible(false);
    armorStand.setGravity(false);
    armorStand.setMarker(true); // smaller hitbox to make it less noticeable
    armorStand.setInvulnerable(true);

    // mount player on the armor stand and track it
    armorStand.addPassenger(p);
    _sittingPlayers.put(p, armorStand);
  }

  private void _unmountFromStair(Player p) {
    // check if player is seated on an armor stand
    if (_sittingPlayers.containsKey(p)) {
      ArmorStand armorStand = _sittingPlayers.get(p);

      // unmount player and remove armor stand
      armorStand.remove();
      _sittingPlayers.remove(p);
    }
  }
}
