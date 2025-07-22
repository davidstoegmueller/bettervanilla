package com.daveestar.bettervanilla.manager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class MessageManager {
  private final Map<Player, Player> _lastMessages = new HashMap<>();

  public void sendPrivateMessage(Player sender, Player receiver, String message) {
    String translated = ChatColor.translateAlternateColorCodes('&', message);
    String prefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "MSG" + ChatColor.GRAY + "] ";

    sender.sendMessage(prefix + ChatColor.YELLOW + "You " + ChatColor.GRAY + "»" + ChatColor.YELLOW + receiver.getName()
        + ChatColor.GRAY + ": " + translated);
    receiver.sendMessage(prefix + ChatColor.YELLOW + sender.getName() + ChatColor.GRAY + " -> You: " + translated);

    _lastMessages.put(sender, receiver);
    _lastMessages.put(receiver, sender);
  }

  public Player getReplyTarget(Player player) {
    Player target = _lastMessages.get(player);
    if (target != null && target.isOnline()) {
      return target;
    }

    return null;
  }

  public void onPlayerLeft(Player player) {
    _lastMessages.remove(player);
    _lastMessages.entrySet().removeIf(entry -> entry.getValue().equals(player));
  }
}
