package com.daveestar.bettervanilla.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatColor;

public class ChatMessages implements Listener {
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    Player p = (Player) e.getPlayer();

    p.playerListName(Component.text(ChatColor.RED + " » " + ChatColor.YELLOW + p.getName()));

    e.joinMessage(
        Component.text(
            ChatColor.GRAY + "[" + ChatColor.YELLOW + "+" + ChatColor.GRAY + "] " + ChatColor.YELLOW + p.getName()));

    Main.getInstance().getMaintenanceManager().sendMaintenance(p);

    Main.getInstance().getAFKManager().onPlayerJoined(p);
    Main.getInstance().getTimerManager().onPlayerJoined(p);

    SettingsManager settingsManager = Main.getInstance().getSettingsManager();
    if (settingsManager.getToggleCompass(p)) {
      Main.getInstance().getCompassManager().addPlayerToCompass(p);
    }
  }

  @EventHandler
  public void onPlayerLeave(PlayerQuitEvent e) {
    Player p = (Player) e.getPlayer();

    e.quitMessage(Component
        .text(ChatColor.GRAY + "[" + ChatColor.RED + "-" + ChatColor.GRAY + "] " + ChatColor.RED + p.getName()));

    Main.getInstance().getAFKManager().onPlayerLeft(p);
    Main.getInstance().getTimerManager().onPlayerLeft(p);
  }

  @EventHandler
  public void onPlayerChat(AsyncChatEvent e) {
    // convert & color codes to actual ChatColor codes
    String message = ((TextComponent) e.message()).content();
    message = ChatColor.translateAlternateColorCodes('&', message);

    // set the formatted chat message
    e.renderer((source, sourceDisplayName, messageComponent, viewer) -> Component
        .text(ChatColor.GRAY + "[" + ChatColor.YELLOW + source.getName() + ChatColor.GRAY + "]"
            + ChatColor.YELLOW + " » " + ChatColor.GRAY + ((TextComponent) messageComponent).content()));

  }
}
