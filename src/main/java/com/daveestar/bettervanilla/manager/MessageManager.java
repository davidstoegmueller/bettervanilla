package com.daveestar.bettervanilla.manager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;

import net.md_5.bungee.api.ChatColor;

public class MessageManager {
  private final Main _plugin;
  private final Map<Player, Player> _lastMessages = new HashMap<>();

  public MessageManager() {
    _plugin = Main.getInstance();
  }

  public void sendPrivateMessage(Player sender, Player receiver, String message) {
    String translated = ChatColor.translateAlternateColorCodes('&', message);
    String prefix = Main.getPrefix() + ChatColor.DARK_AQUA + "[PM] " + ChatColor.GRAY;
    sender.sendMessage(prefix + ChatColor.YELLOW + "You -> " + receiver.getName() + ChatColor.GRAY + ": " + translated);
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
