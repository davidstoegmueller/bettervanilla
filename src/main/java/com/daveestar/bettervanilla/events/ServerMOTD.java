package com.daveestar.bettervanilla.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ServerMOTD implements Listener {

  private final Main _plugin;
  private final SettingsManager _settingsManager;

  public ServerMOTD() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
  }

  @EventHandler
  public void onServerListPing(ServerListPingEvent e) {
    String motd = _settingsManager.getServerMOTD();
    Component motdComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(motd);

    e.motd(motdComponent);
  }
}
