package com.daveestar.bettervanilla.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.daveestar.bettervanilla.Main;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

import java.util.UUID;

public class NameTagManager {
  private final Main _plugin;
  private AFKManager _afkManager;
  private VanishManager _vanishManager;
  private TagManager _tagManager;
  private ScoreboardManager _scoreboardManager;

  public NameTagManager() {
    _plugin = Main.getInstance();
  }

  public void initManagers() {
    _afkManager = _plugin.getAFKManager();
    _vanishManager = _plugin.getVanishManager();
    _tagManager = _plugin.getTagManager();
    _scoreboardManager = Bukkit.getScoreboardManager();
  }

  public void updateNameTag(Player p) {
    if (p == null) {
      return;
    }

    if (_vanishManager.isVanished(p)) {
      _removeTeamEntry(p);
      return;
    }

    String prefix = _buildPrefix(p);
    String suffix = _buildSuffix(p);

    Scoreboard board = _scoreboardManager.getMainScoreboard();
    String teamName = _getTeamName(p.getUniqueId());
    Team team = board.getTeam(teamName);

    if (team == null) {
      team = board.registerNewTeam(teamName);
    }

    team.addEntry(p.getName());
    team.prefix(LegacyComponentSerializer.legacySection().deserialize(prefix));
    team.suffix(LegacyComponentSerializer.legacySection().deserialize(suffix));
  }

  public void removeNameTag(Player p) {
    if (p == null) {
      return;
    }

    _removeTeamEntry(p);
  }

  private String _buildPrefix(Player p) {
    boolean isAfk = _afkManager.isPlayerMarkedAFK(p);

    String afkPrefix = isAfk
        ? ChatColor.GRAY + "[" + ChatColor.RED + "AFK" + ChatColor.GRAY + "] "
        : "";

    return afkPrefix + ChatColor.YELLOW;
  }

  private String _buildSuffix(Player p) {
    return _tagManager.getFormattedTag(p);
  }

  private void _removeTeamEntry(Player p) {
    Scoreboard board = _scoreboardManager.getMainScoreboard();
    String teamName = _getTeamName(p.getUniqueId());
    Team team = board.getTeam(teamName);
    if (team == null) {
      return;
    }

    team.removeEntry(p.getName());
    if (team.getEntries().isEmpty()) {
      team.unregister();
    }
  }

  private String _getTeamName(UUID uuid) {
    String compact = uuid.toString().replace("-", "");
    if (compact.length() > 12) {
      compact = compact.substring(0, 12);
    }

    return "bvs_" + compact;
  }
}
