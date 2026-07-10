package com.daveestar.bettervanilla.manager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.utils.Theme;

import net.md_5.bungee.api.ChatColor;

public class MessageManager {
  private final Map<Player, Player> _lastMessages = new HashMap<>();

  public void sendPrivateMessage(Player sender, Player receiver, String message) {
    String translated = ChatColor.translateAlternateColorCodes('&', message);
    String prefix = Theme.primary() + "[" + Theme.highlight() + "MSG" + Theme.primary() + "] ";

    sender.sendMessage(
        prefix + "(" + Theme.highlight() + "YOU" + Theme.primary() + " " + Theme.textSymbol() + "» "
            + Theme.highlight() + receiver.getName() + Theme.primary() + ") " + Theme.textPrefix() + translated);
    receiver
        .sendMessage(
            prefix + "(" + Theme.highlight() + sender.getName() + Theme.primary() + " " + Theme.textSymbol()
                + "» " + Theme.highlight() + "YOU" + Theme.primary() + ") " + Theme.textPrefix() + translated);

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
