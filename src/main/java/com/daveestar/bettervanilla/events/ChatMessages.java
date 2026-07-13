package com.daveestar.bettervanilla.events;

import java.util.regex.Pattern;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerKickEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.AFKManager;
import com.daveestar.bettervanilla.manager.CompassManager;
import com.daveestar.bettervanilla.manager.MaintenanceManager;
import com.daveestar.bettervanilla.manager.PermissionsManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.manager.TimerManager;
import com.daveestar.bettervanilla.manager.BackpackManager;
import com.daveestar.bettervanilla.manager.MessageManager;
import com.daveestar.bettervanilla.manager.VanishManager;
import com.daveestar.bettervanilla.manager.TabListManager;
import com.daveestar.bettervanilla.manager.TagManager;
import com.daveestar.bettervanilla.manager.NameTagManager;
import com.daveestar.bettervanilla.utils.Theme;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatColor;

public class ChatMessages implements Listener {

  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final PermissionsManager _permissionsManager;
  private final AFKManager _afkManager;
  private final TimerManager _timerManager;
  private final CompassManager _compassManager;
  private final MaintenanceManager _maintenanceManager;
  private final BackpackManager _backpackManager;
  private final MessageManager _messageManager;
  private final VanishManager _vanishManager;
  private final TabListManager _tabListManager;
  private final TagManager _tagManager;
  private final NameTagManager _nameTagManager;

  public ChatMessages() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _permissionsManager = _plugin.getPermissionsManager();
    _afkManager = _plugin.getAFKManager();
    _timerManager = _plugin.getTimerManager();
    _compassManager = _plugin.getCompassManager();
    _maintenanceManager = _plugin.getMaintenanceManager();
    _backpackManager = _plugin.getBackpackManager();
    _messageManager = _plugin.getMessageManager();
    _vanishManager = _plugin.getVanishManager();
    _tabListManager = _plugin.getTabListManager();
    _tagManager = _plugin.getTagManager();
    _nameTagManager = _plugin.getNameTagManager();
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    Player p = (Player) e.getPlayer();
    e.joinMessage(null);

    _permissionsManager.onPlayerJoined(p);

    if (_settingsManager.getMaintenanceState()) {
      boolean bypass = _maintenanceManager.sendMaintenance(p);

      if (!bypass)
        return;
    }

    boolean wasVanished = _vanishManager.isVanished(p);
    if (wasVanished) {
      // Vanished players do not produce a join announcement.
    } else {
      _broadcastJoinMessage(p);
    }

    _afkManager.onPlayerJoined(p);
    _timerManager.onPlayerJoined(p);
    _compassManager.onPlayerJoined(p);
    _tabListManager.refreshPlayer(p);
    _nameTagManager.updateNameTag(p);

    _vanishManager.handlePlayerJoin(p);
  }

  @EventHandler
  public void onPlayerLeave(PlayerQuitEvent e) {
    Player p = (Player) e.getPlayer();

    e.quitMessage(null);
    if (_vanishManager.isVanished(p)) {
      // Vanished players do not produce a quit announcement.
    } else {
      _broadcastQuitMessage(p);
    }

    _permissionsManager.onPlayerLeft(p);
    _afkManager.onPlayerLeft(p);
    _timerManager.onPlayerLeft(p);
    _compassManager.onPlayerLeft(p);
    _backpackManager.onPlayerLeft(p);
    _messageManager.onPlayerLeft(p);
    _nameTagManager.removeNameTag(p);
  }

  @EventHandler
  public void onPlayerKick(PlayerKickEvent e) {
    Player p = (Player) e.getPlayer();

    _permissionsManager.onPlayerLeft(p);
    _afkManager.onPlayerLeft(p);
    _timerManager.onPlayerLeft(p);
    _compassManager.onPlayerLeft(p);
    _backpackManager.onPlayerLeft(p);
    _messageManager.onPlayerLeft(p);
    _nameTagManager.removeNameTag(p);
  }

  @EventHandler
  public void onPlayerChat(AsyncChatEvent e) {
    // convert & color codes to actual ChatColor codes
    String raw = ((TextComponent) e.message()).content();
    String translated = ChatColor.translateAlternateColorCodes('&', raw);

    // set the formatted chat message with ping support
    e.renderer((source, sourceDisplayName, messageComponent, viewer) -> {
      String formatted = translated;

      if (viewer instanceof Player) {
        Player chatViewer = (Player) viewer;
        String name = chatViewer.getName();

        String lowerName = name.toLowerCase();

        if (formatted.toLowerCase().contains(lowerName) || formatted.toLowerCase().contains("@" + lowerName)) {
          formatted = formatted.replaceAll("(?i)@?" + Pattern.quote(name),
              Theme.highlight() + "" + ChatColor.BOLD + name + Theme.primary());

          chatViewer.playSound(chatViewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1);
        }
      }

      String tagSuffix = "";
      if (source instanceof Player sourcePlayer) {
        tagSuffix = _tagManager.getFormattedTag(sourcePlayer);
      }

      CommandSender commandViewer = viewer instanceof CommandSender sender ? sender : null;
      return Component.text(Main.tr(commandViewer, "chat-message-format",
          "sender", Theme.primary() + "[" + Theme.highlight() + source.getName() + Theme.primary() + "]",
          "tag", tagSuffix,
          "message", Theme.textPrefix() + formatted));
    });
  }

  private void _broadcastJoinMessage(Player joinedPlayer) {
    String tagSuffix = _tagManager.getFormattedTag(joinedPlayer);
    _plugin.getServer().getOnlinePlayers().forEach(viewer -> viewer.sendMessage(Component.text(
        Theme.primary() + "[" + Theme.highlight() + "+" + Theme.primary() + "] "
            + Main.tr(viewer, "event-player-joined",
                "player", Theme.highlight() + joinedPlayer.getName() + tagSuffix + Theme.primary()))));
  }

  private void _broadcastQuitMessage(Player leavingPlayer) {
    String tagSuffix = _tagManager.getFormattedTag(leavingPlayer);
    _plugin.getServer().getOnlinePlayers().stream()
        .filter(viewer -> !viewer.equals(leavingPlayer))
        .forEach(viewer -> viewer.sendMessage(Component.text(
            Theme.primary() + "[" + Theme.error() + "-" + Theme.primary() + "] "
                + Main.tr(viewer, "event-player-left",
                    "player", Theme.error() + leavingPlayer.getName() + tagSuffix + Theme.primary()))));
  }
}
