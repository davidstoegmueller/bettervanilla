package com.daveestar.bettervanilla.events;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.manager.VanishManager;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent.ListedPlayerInfo;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ServerMOTD implements Listener {

  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final VanishManager _vanishManager;

  public ServerMOTD() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _vanishManager = _plugin.getVanishManager();
  }

  @EventHandler
  public void onServerListPing(PaperServerListPingEvent e) {
    String motd = _settingsManager.getServerMOTD();
    Component motdComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(motd);
    e.motd(motdComponent);

    List<Player> visiblePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
    visiblePlayers.removeIf(_vanishManager::isVanished);

    e.setNumPlayers(visiblePlayers.size());

    List<ListedPlayerInfo> sample = e.getListedPlayers();
    sample.clear();
    visiblePlayers.stream()
        .limit(12)
        .forEach(player -> sample.add(new ListedPlayerInfo(player.getName(), player.getUniqueId())));
  }
}
