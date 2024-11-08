package com.daveestar.bettervanilla.events;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

public class SittableStairs implements Listener {
  private final HashMap<Player, ArmorStand> sittingPlayers = new HashMap<>();

  @EventHandler
  public void onPlayerRightClick(PlayerInteractEvent e) {
    // get the clicked block
    Block clickedBlock = e.getClickedBlock();

    if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
      if (clickedBlock != null && clickedBlock.getType().toString().contains("STAIRS")) {
        Player p = e.getPlayer();

        // prevent sitting if already seated
        if (sittingPlayers.containsKey(p)) {
          return;
        }

        // create Armor Stand at the block location for the player to sit on
        Location location = clickedBlock.getLocation().add(0.5, 0.5, 0.5); // position slightly below the player
        location.setDirection(new Vector(0, 0, 0));

        ArmorStand armorStand = p.getWorld().spawn(location, ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setMarker(true); // smaller hitbox to make it less noticeable
        armorStand.setInvulnerable(true);

        // mount player on the Armor Stand and track it
        armorStand.addPassenger(p);
        sittingPlayers.put(p, armorStand);
      }
    }
  }

  @EventHandler
  public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
    Player p = e.getPlayer();

    // check if player is seated on an armor stand
    if (sittingPlayers.containsKey(p) && p.isInsideVehicle()) {
      ArmorStand armorStand = sittingPlayers.get(p);

      // unmount player and remove armor stand
      armorStand.remove();
      sittingPlayers.remove(p);
    }
  }
}
