package com.daveestar.bettervanilla.events;

import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;

public class VeinMiningChopping implements Listener {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final Set<UUID> _breakingPlayers = new HashSet<>();

  public VeinMiningChopping() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent e) {
    Player player = e.getPlayer();
    if (!player.isSneaking() || _breakingPlayers.contains(player.getUniqueId())) {
      return;
    }

    Block origin = e.getBlock();
    Material blockType = origin.getType();
    ItemStack tool = player.getInventory().getItemInMainHand();
    boolean mining = _settingsManager.getVeinMinerEnabled()
        && _settingsManager.getPlayerVeinMiner(player.getUniqueId())
        && _settingsManager.getVeinMinerAllowedTools().contains(tool.getType().name())
        && _settingsManager.getVeinMinerAllowedBlocks().contains(blockType.name());
    boolean chopping = _settingsManager.getVeinChopperEnabled()
        && _settingsManager.getPlayerVeinChopper(player.getUniqueId())
        && _settingsManager.getVeinChopperAllowedTools().contains(tool.getType().name())
        && _settingsManager.getVeinChopperAllowedBlocks().contains(blockType.name());
    if (!mining && !chopping) {
      return;
    }

    int limit = mining ? _settingsManager.getVeinMinerMaxVeinSize() : _settingsManager.getVeinChopperMaxVeinSize();
    List<Block> blocks = _getVeinBlocks(origin, limit);
    boolean sound = mining ? _settingsManager.getVeinMinerSound() : _settingsManager.getVeinChopperSound();
    Material toolType = tool.getType();
    // Let the original break finish before processing neighbours with normal player events.
    _plugin.getServer().getScheduler().runTask(_plugin, () -> {
      if (!player.isOnline() || player.getWorld() != origin.getWorld() || origin.getType() == blockType) {
        return;
      }
      _breakingPlayers.add(player.getUniqueId());
      int broken = 0;
      try {
        for (Block block : blocks) {
          if (player.getWorld() != origin.getWorld() || player.getInventory().getItemInMainHand().getType() != toolType) {
            break;
          }
          if (!block.equals(origin) && block.getType() == blockType && player.breakBlock(block)) {
            broken++;
          }
        }
      } finally {
        _breakingPlayers.remove(player.getUniqueId());
      }
      if (broken > 0 && sound) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7f, 1);
      }
    });
  }

  private List<Block> _getVeinBlocks(Block origin, int limit) {
    Set<Block> visited = new LinkedHashSet<>();
    Queue<Block> queue = new ArrayDeque<>();
    visited.add(origin);
    queue.add(origin);
    while (!queue.isEmpty() && visited.size() < limit) {
      Block current = queue.remove();
      for (int dx = -1; dx <= 1; dx++) {
        for (int dy = -1; dy <= 1; dy++) {
          for (int dz = -1; dz <= 1 && visited.size() < limit; dz++) {
            if (dx == 0 && dy == 0 && dz == 0) {
              continue;
            }
            int y = current.getY() + dy;
            if (y < current.getWorld().getMinHeight() || y >= current.getWorld().getMaxHeight()) {
              continue;
            }
            Block neighbour = current.getRelative(dx, dy, dz);
            if (neighbour.getType() == origin.getType() && visited.add(neighbour)) {
              queue.add(neighbour);
            }
          }
        }
      }
    }
    return List.copyOf(visited);
  }
}
