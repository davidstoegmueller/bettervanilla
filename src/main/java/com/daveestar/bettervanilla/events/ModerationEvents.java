package com.daveestar.bettervanilla.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.ModerationManager;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class ModerationEvents implements Listener {
  private final ModerationManager _modManager;

  public ModerationEvents() {
    _modManager = Main.getInstance().getModerationManager();
  }

  @EventHandler
  public void onPreLogin(AsyncPlayerPreLoginEvent e) {
    OfflinePlayer p = Main.getInstance().getServer().getOfflinePlayer(e.getUniqueId());
    if (_modManager.isBanned(p)) {
      String reason = _modManager.getBanReason(p);
      long expires = _modManager.getBanExpiry(p);
      String msg = ChatColor.GRAY + "[" + ChatColor.YELLOW + "BetterVanilla" + ChatColor.GRAY + "] " + ChatColor.RED + "You are banned!";
      if (!reason.isEmpty()) msg += "\n" + ChatColor.GRAY + "Reason: " + ChatColor.YELLOW + reason;
      if (expires != -1) {
        long remaining = (expires - System.currentTimeMillis()) / 1000;
        msg += "\n" + ChatColor.GRAY + "Expires in: " + ChatColor.YELLOW + remaining + "s";
      }
      e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Component.text(msg));
    }
  }

  @EventHandler
  public void onChat(AsyncChatEvent e) {
    Player p = e.getPlayer();
    if (_modManager.isMuted(p)) {
      String reason = _modManager.getMuteReason(p);
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "You are muted." + (reason.isEmpty() ? "" : " Reason: " + ChatColor.YELLOW + reason));
      e.setCancelled(true);
    }
  }
}
