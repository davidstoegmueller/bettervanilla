package com.daveestar.bettervanilla.events;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.entity.CraftVillager;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.Theme;

public class VillagerTradeCycling implements Listener {
  private final Main _plugin;
  private final SettingsManager _settingsManager;

  public VillagerTradeCycling() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    if (!_settingsManager.getVillagerTradeCyclingEnabled()
        || !e.isRightClick()
        || e.getRawSlot() != -999
        || !(e.getWhoClicked() instanceof Player player)
        || !(e.getView().getTopInventory() instanceof MerchantInventory inventory)
        || !(inventory.getMerchant() instanceof Villager villager)) {
      return;
    }

    e.setCancelled(true);
    _cycle(player, villager);
  }

  private void _cycle(Player player, Villager villager) {
    if (!_canCycle(villager)) {
      player.sendMessage(Main.getPrefix() + Theme.error()
          + Main.tr(player, "villager-trade-cycling-locked-message"));
      player.playSound(player, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
      return;
    }

    villager.resetOffers();
    player.playSound(player, Sound.ENTITY_VILLAGER_WORK_LIBRARIAN, 0.5F, 1);

    var serverPlayer = ((CraftPlayer) player).getHandle();
    var serverVillager = ((CraftVillager) villager).getHandle();
    serverPlayer.sendMerchantOffers(
        serverPlayer.containerMenu.containerId,
        serverVillager.getOffers(),
        serverVillager.getVillagerData().level(),
        serverVillager.getVillagerXp(),
        serverVillager.showProgressBar(),
        serverVillager.canRestock());
  }

  private boolean _canCycle(Villager villager) {
    return villager.getVillagerLevel() == 1
        && villager.getVillagerExperience() == 0
        && villager.getRecipes().stream().noneMatch(recipe -> recipe.getUses() > 0);
  }
}
