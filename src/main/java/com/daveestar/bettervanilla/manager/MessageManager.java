package com.daveestar.bettervanilla.manager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.Theme;

import net.md_5.bungee.api.ChatColor;

public class MessageManager {
  private final Map<Player, Player> _lastMessages = new HashMap<>();

  public void sendPrivateMessage(Player sender, Player receiver, String message) {
    String translated = ChatColor.translateAlternateColorCodes('&', message);

    String senderPrefix = Theme.primary() + "[" + Theme.highlight()
        + Main.tr(sender, "private-message-label") + Theme.primary() + "] ";
    String senderYou = Theme.highlight() + Main.tr(sender, "private-message-you") + Theme.primary();
    sender.sendMessage(senderPrefix + Main.tr(sender, "private-message-outgoing-format",
        "you", senderYou,
        "recipient", Theme.highlight() + receiver.getName() + Theme.primary(),
        "message", Theme.textPrefix() + translated));

    String receiverPrefix = Theme.primary() + "[" + Theme.highlight()
        + Main.tr(receiver, "private-message-label") + Theme.primary() + "] ";
    String receiverYou = Theme.highlight() + Main.tr(receiver, "private-message-you") + Theme.primary();
    receiver.sendMessage(receiverPrefix + Main.tr(receiver, "private-message-incoming-format",
        "sender", Theme.highlight() + sender.getName() + Theme.primary(),
        "you", receiverYou,
        "message", Theme.textPrefix() + translated));

    _lastMessages.put(sender, receiver);
    _lastMessages.put(receiver, sender);
  }

  public Player getReplyTarget(Player p) {
    Player target = _lastMessages.get(p);
    if (target != null && target.isOnline()) {
      return target;
    }

    return null;
  }

  public void onPlayerLeft(Player p) {
    _lastMessages.remove(p);
    _lastMessages.entrySet().removeIf(entry -> entry.getValue().equals(p));
  }
}
