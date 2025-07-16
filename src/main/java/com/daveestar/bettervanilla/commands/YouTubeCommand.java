package com.daveestar.bettervanilla.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.YouTubeManager;
import com.daveestar.bettervanilla.enums.Permissions;

import net.md_5.bungee.api.ChatColor;

import java.util.List;

public class YouTubeCommand implements CommandExecutor {

  private final Main _plugin;
  private final YouTubeManager _youtubeManager;

  public YouTubeCommand() {
    _plugin = Main.getInstance();
    _youtubeManager = _plugin.getYouTubeManager();
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(Main.getPrefix() + ChatColor.RED + "This command can only be used by players!");
      return true;
    }

    Player player = (Player) sender;

    if (!player.hasPermission(Permissions.YOUTUBE.getName())) {
      player.sendMessage(Main.getPrefix() + ChatColor.RED + "You don't have permission to use YouTube commands!");
      return true;
    }

    if (args.length == 0) {
      showUsage(player);
      return true;
    }

    String action = args[0].toLowerCase();

    switch (action) {
      case "play":
        if (args.length < 2) {
          player.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: /youtube play <video-id>");
          return true;
        }
        _youtubeManager.playSong(player, args[1]);
        break;

      case "stop":
        _youtubeManager.stopSong(player);
        break;

      case "list":
        listSongs(player);
        break;

      case "volume":
        if (args.length < 2) {
          player.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: /youtube volume <0.1-1.0>");
          return true;
        }
        setVolume(player, args[1]);
        break;

      default:
        showUsage(player);
        break;
    }

    return true;
  }

  private void showUsage(Player player) {
    player.sendMessage(Main.getPrefix() + "YouTube Commands:");
    player.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/youtube play <video-id>" + ChatColor.GRAY + " - Play a YouTube song");
    player.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/youtube stop" + ChatColor.GRAY + " - Stop current playback");
    player.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/youtube list" + ChatColor.GRAY + " - List available songs");
    player.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/youtube volume <0.1-1.0>" + ChatColor.GRAY + " - Set playback volume");
  }

  private void listSongs(Player player) {
    List<String> songs = _youtubeManager.getStoredSongs();
    
    if (songs.isEmpty()) {
      player.sendMessage(Main.getPrefix() + ChatColor.RED + "No songs available! Share YouTube links in chat to add them.");
      return;
    }

    player.sendMessage(Main.getPrefix() + "Available Songs:");
    for (String videoId : songs) {
      String title = _youtubeManager.getSongTitle(videoId);
      player.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + videoId + ChatColor.GRAY + " - " + title);
    }
  }

  private void setVolume(Player player, String volumeStr) {
    try {
      double volume = Double.parseDouble(volumeStr);
      if (volume < 0.1 || volume > 1.0) {
        player.sendMessage(Main.getPrefix() + ChatColor.RED + "Volume must be between 0.1 and 1.0!");
        return;
      }
      
      _youtubeManager.setPlayerVolume(player, volume);
      player.sendMessage(Main.getPrefix() + "Volume set to " + ChatColor.YELLOW + (int)(volume * 100) + "%");
      
    } catch (NumberFormatException e) {
      player.sendMessage(Main.getPrefix() + ChatColor.RED + "Invalid volume! Use a number between 0.1 and 1.0");
    }
  }
}