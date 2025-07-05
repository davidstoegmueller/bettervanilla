package com.daveestar.bettervanilla.events;

import org.bukkit.Sound;
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
import com.daveestar.bettervanilla.manager.TimerManager;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatColor;

public class ChatMessages implements Listener {

  private final Main _plugin;
  private final PermissionsManager _permissionsManager;
  private final AFKManager _afkManager;
  private final TimerManager _timerManager;
  private final CompassManager _compassManager;
  private final MaintenanceManager _maintenanceManager;

  public ChatMessages() {
    _plugin = Main.getInstance();
    _permissionsManager = _plugin.getPermissionsManager();
    _afkManager = _plugin.getAFKManager();
    _timerManager = _plugin.getTimerManager();
    _compassManager = _plugin.getCompassManager();
    _maintenanceManager = _plugin.getMaintenanceManager();
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    Player p = (Player) e.getPlayer();

    if (_maintenanceManager.getState() && !p.hasPermission("bettervanilla.maintenance.bypass")) {
      _maintenanceManager.sendMaintenance(p);
      return;
    }

    p.playerListName(Component.text(ChatColor.RED + " » " + ChatColor.YELLOW + p.getName()));

    e.joinMessage(
        Component.text(
            ChatColor.GRAY + "[" + ChatColor.YELLOW + "+" + ChatColor.GRAY + "] " + ChatColor.YELLOW + p.getName()));

    _permissionsManager.onPlayerJoined(p);
    _afkManager.onPlayerJoined(p);
    _timerManager.onPlayerJoined(p);
    _compassManager.onPlayerJoined(p);
  }

  @EventHandler
  public void onPlayerLeave(PlayerQuitEvent e) {
    Player p = (Player) e.getPlayer();

    e.quitMessage(Component
        .text(ChatColor.GRAY + "[" + ChatColor.RED + "-" + ChatColor.GRAY + "] " + ChatColor.RED + p.getName()));

    _permissionsManager.onPlayerLeft(p);
    _afkManager.onPlayerLeft(p);
    _timerManager.onPlayerLeft(p);
    _compassManager.onPlayerLeft(p);
  }

  @EventHandler
  public void onPlayerKick(PlayerKickEvent e) {
    Player p = (Player) e.getPlayer();

    _permissionsManager.onPlayerLeft(p);
    _afkManager.onPlayerLeft(p);
    _timerManager.onPlayerLeft(p);
    _compassManager.onPlayerLeft(p);
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

        if (formatted.toLowerCase().contains("@" + name.toLowerCase())) {
          formatted = formatted.replaceAll("(?i)@" + java.util.regex.Pattern.quote(name),
              ChatColor.YELLOW + "" + ChatColor.BOLD + name + ChatColor.GRAY);

          chatViewer.playSound(chatViewer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1);
        }
      }

      return Component.text(ChatColor.GRAY + "[" + ChatColor.YELLOW + source.getName() + ChatColor.GRAY + "]"
          + ChatColor.YELLOW + " » " + ChatColor.GRAY + formatted);
    });
  }
}
