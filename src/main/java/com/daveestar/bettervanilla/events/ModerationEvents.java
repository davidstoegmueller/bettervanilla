package com.daveestar.bettervanilla.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.ModerationManager;
import com.daveestar.bettervanilla.manager.TimerManager;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class ModerationEvents implements Listener {
  private final ModerationManager _modManager;
  private final TimerManager _timerManager;

  public ModerationEvents() {
    _modManager = Main.getInstance().getModerationManager();
    _timerManager = Main.getInstance().getTimerManager();
  }

  @EventHandler
  public void onPreLogin(AsyncPlayerPreLoginEvent e) {
    OfflinePlayer p = Main.getInstance().getServer().getOfflinePlayer(e.getUniqueId());
    if (_modManager.isBanned(p)) {
      String reason = _modManager.getBanReason(p);
      long expires = _modManager.getBanExpiry(p);
      String banMsg = ChatColor.YELLOW + "" + ChatColor.BOLD + "BANNED\n\n" + ChatColor.GRAY
          + "You were banned from the server.\n\n";
      if (!reason.isEmpty()) {
        banMsg += ChatColor.YELLOW + "" + ChatColor.BOLD + "Reason: " + ChatColor.GRAY + reason + "\n";
      }
      if (expires != -1) {
        long remaining = (expires - System.currentTimeMillis()) / 1000;
        String time = _timerManager.formatTime((int) remaining);
        banMsg += ChatColor.YELLOW + "" + ChatColor.BOLD + "Expires in: " + ChatColor.GRAY + time;
      }
      e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Component.text(banMsg));
    }
  }

  @EventHandler
  public void onChat(AsyncChatEvent e) {
    Player p = e.getPlayer();
    if (_modManager.isMuted(p)) {
      String reason = _modManager.getMuteReason(p);
      String msg = Main.getPrefix() + ChatColor.RED + "You are muted.";
      if (reason != null && !reason.isEmpty()) {
        msg += " Reason: " + ChatColor.YELLOW + reason;
      } else {
        msg += " No reason given.";
      }
      p.sendMessage(msg);
      e.setCancelled(true);
    }
  }
}
