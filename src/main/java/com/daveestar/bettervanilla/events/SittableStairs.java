package com.daveestar.bettervanilla.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SittingManager;

public class SittableStairs implements Listener {
  private static final double MAX_DISTANCE = 2;

  @EventHandler
  public void onPlayerRightClick(PlayerInteractEvent e) {
    // ignore off-hand interactions to prevent duplicate handling
    if (e.getHand() != EquipmentSlot.HAND) {
      return;
    }

    // get the clicked block
    Block clickedBlock = e.getClickedBlock();

    if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
      if (clickedBlock != null && clickedBlock.getType().toString().contains("STAIRS")) {
        Player p = e.getPlayer();

        // only allow to sit on a chair with an empty hand
        if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
          // range check
          if (p.getLocation().distance(clickedBlock.getLocation()) > MAX_DISTANCE) {
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
          SittingManager sittingManager = Main.getInstance().getSittingManager();
          sittingManager.unsitPlayer(p);

          // create armor stand slightly higher so the player sits on top of the stair
          Location location = clickedBlock.getLocation().toBlockLocation().add(0.5, 0.5, 0.5);

          // adjust direction based on the lower side of the stairs
          if (clickedBlock.getBlockData() instanceof Stairs) {
            Stairs stairs = (Stairs) clickedBlock.getBlockData();
            switch (stairs.getFacing()) {
              case NORTH:
                location.setYaw(0f); // face south
                break;
              case SOUTH:
                location.setYaw(180f); // face north
                break;
              case EAST:
                location.setYaw(90f); // face west
                break;
              case WEST:
                location.setYaw(270f); // face east
                break;
              default:
                break;
            }
          }

          sittingManager.sitPlayer(p, location);

          p.sendMessage(Main.getPrefix() + "Well have a rest. Stand up using the 'Shift' key.");
        }
      }
    }
  }
}
