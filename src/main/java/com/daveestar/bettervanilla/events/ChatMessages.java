package com.daveestar.bettervanilla.events;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.daveestar.bettervanilla.Main;

public class ChatMessages implements Listener {
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    Player p = (Player) e.getPlayer();

    p.setPlayerListName(ChatColor.RED + " » " + ChatColor.YELLOW + p.getName());

    e.setJoinMessage(
        ChatColor.GRAY + "[" + ChatColor.YELLOW + "+" + ChatColor.GRAY + "] " + ChatColor.YELLOW + p.getName());

    Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
    Main.getInstance().get_timerManager().checkAndSetTimerRunning(onlinePlayers.size());

    Main.getInstance().get_maintenanceManager().sendMaintenance(p);

    Main.getInstance().getAFKManager().playerJoined(p);
  }

  @EventHandler
  public void onPlayerLeave(PlayerQuitEvent e) {
    Player p = (Player) e.getPlayer();

    e.setQuitMessage(ChatColor.GRAY + "[" + ChatColor.RED + "-" + ChatColor.GRAY + "] " + ChatColor.RED + p.getName());

    Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
    Main.getInstance().get_timerManager().checkAndSetTimerRunning(onlinePlayers.size() - 1);

    Main.getInstance().getAFKManager().playerLeft(p);
  }

  @EventHandler
  public void onPlayerChat(AsyncPlayerChatEvent e) {
    // convert & color codes to actual ChatColor codes
    String message = e.getMessage();
    message = ChatColor.translateAlternateColorCodes('&', message);

    // set the formatted chat message
    e.setFormat(
        ChatColor.GRAY + "[" + ChatColor.YELLOW + e.getPlayer().getName() + ChatColor.GRAY + "]" + ChatColor.YELLOW
            + " » " + ChatColor.GRAY + message);
  }
}
