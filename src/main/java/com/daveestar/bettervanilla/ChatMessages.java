package com.daveestar.bettervanilla;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatMessages implements Listener {
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    Player p = (Player) e.getPlayer();

    e.setJoinMessage(
        ChatColor.GRAY + "[" + ChatColor.YELLOW + "+" + ChatColor.GRAY + "] " + ChatColor.YELLOW + p.getName());

    Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
    Main.getInstance().getTimer().checkAndSetTimerRunning(onlinePlayers.size());

    Main.getInstance().getMaintenance().sendMaintenance(p);
  }

  @EventHandler
  public void onPlayerLeave(PlayerQuitEvent e) {
    Player p = (Player) e.getPlayer();

    e.setQuitMessage(ChatColor.GRAY + "[" + ChatColor.RED + "-" + ChatColor.GRAY + "] " + ChatColor.RED + p.getName());

    Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
    Main.getInstance().getTimer().checkAndSetTimerRunning(onlinePlayers.size() - 1);
  }

  @EventHandler
  public void onPlayerChat(AsyncPlayerChatEvent e) {
    e.setFormat(
        ChatColor.GRAY + "[" + ChatColor.YELLOW + e.getPlayer().getName() + ChatColor.GRAY + "] -> " + e.getMessage());
  }
}
