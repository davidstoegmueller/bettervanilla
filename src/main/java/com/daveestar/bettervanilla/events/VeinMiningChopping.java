package com.daveestar.bettervanilla.events;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;

public class VeinMiningChopping implements Listener {

  private final Main _plugin;
  private final SettingsManager _settingsManager;

  public VeinMiningChopping() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent e) {
    Player p = e.getPlayer();

    boolean isVeinMinerActive = _settingsManager.getPlayerVeinMiner(p.getUniqueId());
    boolean isVeinChopperActive = _settingsManager.getPlayerVeinChopper(p.getUniqueId());

    Block block = e.getBlock();
    ItemStack tool = p.getInventory().getItem(EquipmentSlot.HAND);

    if (isVeinMinerActive && p.isSneaking()) {
      _handleVeinMiner(p, block, tool);
    }

    if (isVeinChopperActive && p.isSneaking()) {
      _handleVeinChopper(p, block, tool);
    }
  }

  private void _handleVeinMiner(Player p, Block originBlock, ItemStack tool) {
    Material toolType = tool.getType();
    Material originBlockType = originBlock.getType();

    boolean isValidPickaxe = _isValidPickaxe(toolType);
    boolean isVeinMinerBlock = _isVeinMinerBlock(originBlockType);

    if (isValidPickaxe && isVeinMinerBlock) {
      List<Block> veinBlocks = _getVeinBlocks(originBlock);

      for (Block veinBlock : veinBlocks) {
        veinBlock.breakNaturally(tool, true, true);

        if (!_applyDurabilityDamage(tool, p))
          break;
      }
    }
  }

  private void _handleVeinChopper(Player p, Block originBlock, ItemStack tool) {
    Material toolType = tool.getType();
    Material originBlockType = originBlock.getType();

    boolean isValidAxe = _isValidAxe(toolType);
    boolean isVeinChopperBlock = _isVeinChopperBlock(originBlockType);

    if (isValidAxe && isVeinChopperBlock) {
      List<Block> veinBlocks = _getVeinBlocks(originBlock);

      for (Block veinBlock : veinBlocks) {
        veinBlock.breakNaturally(tool, true, true);

        if (!_applyDurabilityDamage(tool, p))
          break;

      }

    }
  }

  private boolean _applyDurabilityDamage(ItemStack tool, Player p) {
    ItemMeta meta = tool.getItemMeta();
    if (!(meta instanceof Damageable))
      return true;

    Damageable dmg = (Damageable) meta;
    int unbreakingLvl = tool.getEnchantmentLevel(Enchantment.UNBREAKING);

    // 1 / (unbreakingLvl + 1) chance to actually consume durability
    if (unbreakingLvl > 0 && ThreadLocalRandom.current().nextDouble() >= 1D / (unbreakingLvl + 1)) {
      return true; // lucky swing – no damage
    }

    dmg.setDamage(dmg.getDamage() + 1);

    if (dmg.getDamage() >= tool.getType().getMaxDurability()) {
      p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
      p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
      return false;
    }

    tool.setItemMeta(dmg);
    p.getInventory().setItemInMainHand(tool);

    return true;
  }

  private List<Block> _getVeinBlocks(Block originBlock) {
    int limit = 100; // safety valve; make this configurable ???
    Set<Block> visited = new HashSet<>();
    Queue<Block> queue = new ArrayDeque<>();

    visited.add(originBlock);
    queue.add(originBlock);

    while (!queue.isEmpty()) {
      Block current = queue.poll();

      for (Block neighbourBlock : _getNeighbourBlocks(current)) {
        if (visited.contains(neighbourBlock))
          continue;

        if (!neighbourBlock.getType().equals(current.getType()))
          continue;

        visited.add(neighbourBlock);
        queue.add(neighbourBlock);

        if (visited.size() >= limit)
          break;
      }
    }

    return new ArrayList<>(visited);
  }

  private List<Block> _getNeighbourBlocks(Block originBlock) {
    List<Block> neighbourBlocks = new ArrayList<>(26);

    for (int dx = -1; dx <= 1; dx++) {
      for (int dy = -1; dy <= 1; dy++) {
        for (int dz = -1; dz <= 1; dz++) {
          if (dx == 0 && dy == 0 && dz == 0)
            continue; // skip origin block
          neighbourBlocks.add(originBlock.getRelative(dx, dy, dz));
        }
      }
    }

    return neighbourBlocks;
  }

  private boolean _isVeinMinerBlock(Material blockType) {
    return blockType == Material.COAL_ORE || blockType == Material.IRON_ORE || blockType == Material.GOLD_ORE ||
        blockType == Material.REDSTONE_ORE || blockType == Material.LAPIS_ORE || blockType == Material.DIAMOND_ORE ||
        blockType == Material.EMERALD_ORE || blockType == Material.COPPER_ORE || blockType == Material.NETHER_QUARTZ_ORE
        || blockType == Material.NETHER_GOLD_ORE || blockType == Material.DEEPSLATE_COAL_ORE ||
        blockType == Material.DEEPSLATE_IRON_ORE || blockType == Material.DEEPSLATE_GOLD_ORE ||
        blockType == Material.DEEPSLATE_REDSTONE_ORE || blockType == Material.DEEPSLATE_LAPIS_ORE ||
        blockType == Material.DEEPSLATE_DIAMOND_ORE || blockType == Material.DEEPSLATE_COPPER_ORE;
  }

  private boolean _isVeinChopperBlock(Material blockType) {
    return blockType == Material.OAK_LOG || blockType == Material.SPRUCE_LOG || blockType == Material.BIRCH_LOG ||
        blockType == Material.JUNGLE_LOG || blockType == Material.ACACIA_LOG || blockType == Material.DARK_OAK_LOG ||
        blockType == Material.MANGROVE_LOG || blockType == Material.CHERRY_LOG;
  }

  private boolean _isValidAxe(Material tool) {
    return tool == Material.WOODEN_AXE || tool == Material.STONE_AXE || tool == Material.IRON_AXE ||
        tool == Material.GOLDEN_AXE || tool == Material.DIAMOND_AXE || tool == Material.NETHERITE_AXE;
  }

  private boolean _isValidPickaxe(Material tool) {
    return tool == Material.WOODEN_PICKAXE || tool == Material.STONE_PICKAXE || tool == Material.IRON_PICKAXE ||
        tool == Material.GOLDEN_PICKAXE || tool == Material.DIAMOND_PICKAXE || tool == Material.NETHERITE_PICKAXE;
  }
}