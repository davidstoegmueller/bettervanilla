package com.daveestar.bettervanilla.events;

import java.util.EnumSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;

public class DoubleDoorSync implements Listener {
  private static final Set<Material> _HAND_OPENABLE_DOORS = EnumSet.noneOf(Material.class);

  static {
    _HAND_OPENABLE_DOORS.addAll(Tag.WOODEN_DOORS.getValues());
    _HAND_OPENABLE_DOORS.add(Material.COPPER_DOOR);
    _HAND_OPENABLE_DOORS.add(Material.EXPOSED_COPPER_DOOR);
    _HAND_OPENABLE_DOORS.add(Material.WEATHERED_COPPER_DOOR);
    _HAND_OPENABLE_DOORS.add(Material.OXIDIZED_COPPER_DOOR);
    _HAND_OPENABLE_DOORS.add(Material.WAXED_COPPER_DOOR);
    _HAND_OPENABLE_DOORS.add(Material.WAXED_EXPOSED_COPPER_DOOR);
    _HAND_OPENABLE_DOORS.add(Material.WAXED_WEATHERED_COPPER_DOOR);
    _HAND_OPENABLE_DOORS.add(Material.WAXED_OXIDIZED_COPPER_DOOR);
  }

  private final Main _plugin;
  private final SettingsManager _settingsManager;

  public DoubleDoorSync() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
  }

  @EventHandler
  public void onDoorInteract(PlayerInteractEvent e) {
    if (e.getHand() != EquipmentSlot.HAND || e.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    Player p = e.getPlayer();
    if (!_settingsManager.getPlayerDoubleDoorSync(p.getUniqueId())) {
      return;
    }

    Block clickedBlock = e.getClickedBlock();
    if (clickedBlock == null) {
      return;
    }

    if (!_isHandOpenableDoor(clickedBlock.getType())) {
      return;
    }

    Block baseDoor = _getBottomDoorBlock(clickedBlock);
    if (baseDoor == null) {
      return;
    }

    Door baseDoorData = (Door) baseDoor.getBlockData();
    Block partnerDoor = _findPairedDoor(baseDoor, baseDoorData);
    if (partnerDoor == null) {
      return;
    }

    // run next tick to let the vanilla interaction finish first
    Bukkit.getScheduler().runTask(_plugin, () -> _syncDoorState(baseDoor, partnerDoor));
  }

  private boolean _isHandOpenableDoor(Material material) {
    return _HAND_OPENABLE_DOORS.contains(material);
  }

  private void _syncDoorState(Block referenceDoor, Block targetDoor) {
    BlockData referenceData = referenceDoor.getBlockData();
    if (!(referenceData instanceof Door)) {
      return;
    }

    boolean desiredState = ((Door) referenceData).isOpen();

    BlockData targetData = targetDoor.getBlockData();
    if (!(targetData instanceof Door)) {
      return;
    }

    Door targetDoorData = (Door) targetData;
    if (targetDoorData.isOpen() == desiredState) {
      return;
    }

    targetDoorData.setOpen(desiredState);
    targetDoor.setBlockData(targetDoorData, true);

    Block topHalf = targetDoor.getRelative(BlockFace.UP);
    BlockData topHalfData = topHalf.getBlockData();
    if (topHalfData instanceof Door) {
      Door topDoorData = (Door) topHalfData;
      if (topDoorData.isOpen() != desiredState) {
        topDoorData.setOpen(desiredState);
        topHalf.setBlockData(topDoorData, true);
      }
    }
  }

  private Block _getBottomDoorBlock(Block block) {
    BlockData data = block.getBlockData();
    if (!(data instanceof Door)) {
      return null;
    }

    Door doorData = (Door) data;
    if (doorData.getHalf() == Half.BOTTOM) {
      return block;
    }

    Block below = block.getRelative(BlockFace.DOWN);
    BlockData belowData = below.getBlockData();
    if (belowData instanceof Door) {
      return below;
    }

    return null;
  }

  private Block _findPairedDoor(Block baseDoor, Door baseDoorData) {
    BlockFace expectedOffset = _getExpectedPartnerOffset(baseDoorData);
    if (expectedOffset == null) {
      return null;
    }

    Block adjacent = baseDoor.getRelative(expectedOffset);
    Block bottomAdjacent = _getBottomDoorBlock(adjacent);
    if (bottomAdjacent == null) {
      return null;
    }

    if (!_isHandOpenableDoor(bottomAdjacent.getType())) {
      return null;
    }

    if (bottomAdjacent.getType() != baseDoor.getType()) {
      return null;
    }

    Door adjacentData = (Door) bottomAdjacent.getBlockData();
    if (adjacentData.getFacing() != baseDoorData.getFacing()) {
      return null;
    }

    if (adjacentData.getHinge() == baseDoorData.getHinge()) {
      return null;
    }

    BlockFace reverseOffset = _getExpectedPartnerOffset(adjacentData);
    if (reverseOffset == null) {
      return null;
    }

    if (!bottomAdjacent.getRelative(reverseOffset).equals(baseDoor)) {
      return null;
    }

    return bottomAdjacent;
  }

  private BlockFace _getExpectedPartnerOffset(Door doorData) {
    BlockFace facing = doorData.getFacing();
    boolean hingeRight = doorData.getHinge() == Door.Hinge.RIGHT;

    switch (facing) {
      case NORTH:
        return hingeRight ? BlockFace.WEST : BlockFace.EAST;
      case SOUTH:
        return hingeRight ? BlockFace.EAST : BlockFace.WEST;
      case EAST:
        return hingeRight ? BlockFace.NORTH : BlockFace.SOUTH;
      case WEST:
        return hingeRight ? BlockFace.SOUTH : BlockFace.NORTH;
      default:
        return null;
    }
  }
}
