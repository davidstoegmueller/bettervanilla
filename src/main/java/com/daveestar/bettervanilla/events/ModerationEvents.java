package com.daveestar.bettervanilla.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;

import java.util.UUID;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.ModerationManager;
import com.daveestar.bettervanilla.manager.TimerManager;
import com.daveestar.bettervanilla.utils.Theme;

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
      String banMsg = Theme.highlight() + "" + ChatColor.BOLD
          + _tr(e.getUniqueId(), "moderation-ban-screen-title") + "\n\n" + Theme.primary()
          + _tr(e.getUniqueId(), "moderation-ban-screen-description") + "\n\n";

      if (!reason.isEmpty()) {
        banMsg += Theme.highlight() + "" + ChatColor.BOLD
            + _tr(e.getUniqueId(), "moderation-ban-screen-reason",
                "reason", Theme.primary() + reason) + "\n";
      }

      if (expires != -1) {
        long remaining = (expires - System.currentTimeMillis()) / 1000;
        String time = _timerManager.formatTime(e.getUniqueId(), (int) remaining);
        banMsg += Theme.highlight() + "" + ChatColor.BOLD
            + _tr(e.getUniqueId(), "moderation-ban-screen-expires",
                "time", Theme.primary() + time);
      }

      e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Component.text(banMsg));
    }
  }

  @EventHandler
  public void onChat(AsyncChatEvent e) {
    Player p = e.getPlayer();
    if (_modManager.isMuted(p)) {
      String reason = _modManager.getMuteReason(p);
      long expires = _modManager.getMuteExpiry(p);
      String time = null;
      if (expires != -1) {
        long remaining = (expires - System.currentTimeMillis()) / 1000;
        time = _timerManager.formatTime(p, (int) remaining);
      }

      boolean hasReason = reason != null && !reason.isEmpty();
      String key;
      if (time != null && hasReason) {
        key = "moderation-mute-message-temporary-with-reason";
      } else if (time != null) {
        key = "moderation-mute-message-temporary-without-reason";
      } else if (hasReason) {
        key = "moderation-mute-message-permanent-with-reason";
      } else {
        key = "moderation-mute-message-permanent-without-reason";
      }

      String msg = Main.getPrefix() + Theme.error() + Main.tr(p, key,
          "time", Theme.highlight() + String.valueOf(time) + Theme.primary(),
          "reason", Theme.highlight() + String.valueOf(reason));
      p.sendMessage(msg);
      e.setCancelled(true);
    }
  }

  private String _tr(UUID playerId, String key, Object... replacements) {
    var translations = Main.getInstance().getTranslationManager();
    return translations.translate(translations.getLanguage(playerId), key, replacements);
  }
}
