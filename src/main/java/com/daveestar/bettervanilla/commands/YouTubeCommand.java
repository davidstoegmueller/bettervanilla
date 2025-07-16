package com.daveestar.bettervanilla.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.YouTubeManager;

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
}