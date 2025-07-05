package com.daveestar.bettervanilla.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CropProtection implements Listener {

  @EventHandler(ignoreCancelled = true)
  public void onPlayerTrample(PlayerInteractEvent e) {
    if (e.getAction() == Action.PHYSICAL
        && e.getClickedBlock() != null
        && e.getClickedBlock().getType() == Material.FARMLAND
        && e.getClickedBlock().getRelative(0, 1, 0).getType() != Material.AIR) {
      e.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onEntityChangeBlock(EntityChangeBlockEvent e) {
    if (e.getBlock().getType() == Material.FARMLAND
        && e.getBlock().getRelative(0, 1, 0).getType() != Material.AIR) {
      e.setCancelled(true);
    }
  }
}
