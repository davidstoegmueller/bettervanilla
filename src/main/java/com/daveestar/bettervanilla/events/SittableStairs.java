package com.daveestar.bettervanilla.events;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import com.daveestar.bettervanilla.Main;

public class SittableStairs implements Listener {
  private final HashMap<Player, ArmorStand> sittingPlayers = new HashMap<>();

  @EventHandler
  public void onPlayerRightClick(PlayerInteractEvent e) {
    // get the clicked block
    Block clickedBlock = e.getClickedBlock();

    if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
      if (clickedBlock != null && clickedBlock.getType().toString().contains("STAIRS")) {
        Player p = e.getPlayer();

        // only allow to sit on a chair with an empty hand
        if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
          // unmount before mounting again to a chair (stair)
          unmount(p);

          // create armor stand at the block location for the player to sit on
          Location location = clickedBlock.getLocation().add(0.5, 0.5, 0.5);

          // adjust direction based on the lower side of the stairs
          if (clickedBlock.getBlockData() instanceof Stairs) {
            Stairs stairs = (Stairs) clickedBlock.getBlockData();
            switch (stairs.getFacing()) {
              case NORTH:
                location.setDirection(new Vector(0, 0, 1)); // face south
                break;
              case SOUTH:
                location.setDirection(new Vector(0, 0, -1)); // face north
                break;
              case EAST:
                location.setDirection(new Vector(-1, 0, 0)); // face west
                break;
              case WEST:
                location.setDirection(new Vector(1, 0, 0)); // face east
                break;
              default:
                break;
            }
          }

          mount(p, location);

          p.sendMessage(Main.getPrefix() + "We'll have a rest. Stand up using the 'Shift' key.");
        }
      }
    }
  }

  @EventHandler
  public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
    Player p = e.getPlayer();
    unmount(p);
  }

  private void mount(Player p, Location location) {
    ArmorStand armorStand = p.getWorld().spawn(location, ArmorStand.class);
    armorStand.setVisible(false);
    armorStand.setGravity(false);
    armorStand.setMarker(true); // smaller hitbox to make it less noticeable
    armorStand.setInvulnerable(true);

    // mount player on the armor stand and track it
    armorStand.addPassenger(p);
    sittingPlayers.put(p, armorStand);
  }

  private void unmount(Player p) {
    // check if player is seated on an armor stand
    if (sittingPlayers.containsKey(p)) {
      ArmorStand armorStand = sittingPlayers.get(p);

      // unmount player and remove armor stand
      armorStand.remove();
      sittingPlayers.remove(p);
    }
  }
}
