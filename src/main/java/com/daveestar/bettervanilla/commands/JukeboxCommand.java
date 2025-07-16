package com.daveestar.bettervanilla.commands;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.gui.JukeboxGUI;
import com.daveestar.bettervanilla.manager.JukeboxManager;

import net.md_5.bungee.api.ChatColor;

public class JukeboxCommand implements TabExecutor {

  private final Main _plugin;
  private final JukeboxManager _jukeboxManager;
  private final JukeboxGUI _jukeboxGUI;

  public JukeboxCommand() {
    _plugin = Main.getInstance();
    _jukeboxManager = _plugin.getJukeboxManager();
    _jukeboxGUI = new JukeboxGUI();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (!(cs instanceof Player)) {
      cs.sendMessage(ChatColor.RED + "This command can only be used by players.");
      return true;
    }

    Player p = (Player) cs;

    if (args.length == 0) {
      _jukeboxGUI.displayGUI(p);
      return true;
    }

    String subCommand = args[0].toLowerCase();
    switch (subCommand) {
      case "add":
        handleAdd(p, args);
        break;
      case "remove":
        handleRemove(p, args);
        break;
      case "list":
        handleList(p);
        break;
      case "current":
        handleCurrent(p);
        break;
      case "next":
        handleNext(p);
        break;
      case "previous":
      case "prev":
        handlePrevious(p);
        break;
      case "clear":
        handleClear(p);
        break;
      default:
        sendUsageMessage(p);
        break;
    }
    return true;
  }

  private void handleAdd(Player p, String[] args) {
    if (args.length < 3) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/jukebox add <url> <title>");
      return;
    }

    String url = args[1];
    String title = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

    // Basic URL validation
    if (!isValidYouTubeUrl(url)) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please provide a valid YouTube URL.");
      return;
    }

    _jukeboxManager.addSong(title, url, p.getUniqueId());
    p.sendMessage(Main.getPrefix() + "Added " + ChatColor.YELLOW + title + ChatColor.GRAY + " to the jukebox queue.");
  }

  private void handleRemove(Player p, String[] args) {
    if (args.length != 2) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/jukebox remove <index>");
      return;
    }

    try {
      int index = Integer.parseInt(args[1]) - 1; // Convert to 0-based index
      if (index < 0 || index >= _jukeboxManager.getQueueSize()) {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Invalid index. Use /jukebox list to see the queue.");
        return;
      }

      List<String> titles = _jukeboxManager.getQueueTitles();
      String removedTitle = titles.get(index);
      _jukeboxManager.removeSong(index);
      p.sendMessage(Main.getPrefix() + "Removed " + ChatColor.YELLOW + removedTitle + ChatColor.GRAY + " from the queue.");
    } catch (NumberFormatException e) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please provide a valid number.");
    }
  }

  private void handleList(Player p) {
    List<String> titles = _jukeboxManager.getQueueTitles();
    if (titles.isEmpty()) {
      p.sendMessage(Main.getPrefix() + "The jukebox queue is empty.");
      return;
    }

    p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + "Jukebox Queue:");
    for (int i = 0; i < titles.size(); i++) {
      String prefix = (i == _jukeboxManager.getCurrentIndex()) ? ChatColor.GREEN + "▶ " : ChatColor.GRAY + "  ";
      p.sendMessage(prefix + ChatColor.YELLOW + (i + 1) + ". " + ChatColor.GRAY + titles.get(i));
    }
  }

  private void handleCurrent(Player p) {
    String currentTitle = _jukeboxManager.getCurrentSongTitle();
    String currentUrl = _jukeboxManager.getCurrentSongUrl();
    
    if (currentUrl.isEmpty()) {
      p.sendMessage(Main.getPrefix() + "No songs in the queue.");
      return;
    }

    p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + "Now Playing: " + ChatColor.GRAY + currentTitle);
    p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + "URL: " + ChatColor.BLUE + ChatColor.UNDERLINE + currentUrl);
  }

  private void handleNext(Player p) {
    if (_jukeboxManager.getQueueSize() == 0) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "No songs in the queue.");
      return;
    }

    _jukeboxManager.skipToNext();
    String nextTitle = _jukeboxManager.getCurrentSongTitle();
    p.sendMessage(Main.getPrefix() + "Skipped to: " + ChatColor.YELLOW + nextTitle);
  }

  private void handlePrevious(Player p) {
    if (_jukeboxManager.getQueueSize() == 0) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "No songs in the queue.");
      return;
    }

    _jukeboxManager.skipToPrevious();
    String prevTitle = _jukeboxManager.getCurrentSongTitle();
    p.sendMessage(Main.getPrefix() + "Skipped to: " + ChatColor.YELLOW + prevTitle);
  }

  private void handleClear(Player p) {
    _jukeboxManager.clearQueue();
    p.sendMessage(Main.getPrefix() + "Cleared the jukebox queue.");
  }

  private boolean isValidYouTubeUrl(String url) {
    try {
      URL urlObj = new URL(url);
      String host = urlObj.getHost().toLowerCase();
      return host.equals("www.youtube.com") || host.equals("youtube.com") || host.equals("youtu.be");
    } catch (Exception e) {
      return false;
    }
  }

  private void sendUsageMessage(Player p) {
    p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage:");
    p.sendMessage(ChatColor.YELLOW + "/jukebox" + ChatColor.GRAY + " - Open the jukebox GUI");
    p.sendMessage(ChatColor.YELLOW + "/jukebox add <url> <title>" + ChatColor.GRAY + " - Add a song to the queue");
    p.sendMessage(ChatColor.YELLOW + "/jukebox remove <index>" + ChatColor.GRAY + " - Remove a song from the queue");
    p.sendMessage(ChatColor.YELLOW + "/jukebox list" + ChatColor.GRAY + " - Show the current queue");
    p.sendMessage(ChatColor.YELLOW + "/jukebox current" + ChatColor.GRAY + " - Show the current song");
    p.sendMessage(ChatColor.YELLOW + "/jukebox next" + ChatColor.GRAY + " - Skip to the next song");
    p.sendMessage(ChatColor.YELLOW + "/jukebox previous" + ChatColor.GRAY + " - Go to the previous song");
    p.sendMessage(ChatColor.YELLOW + "/jukebox clear" + ChatColor.GRAY + " - Clear the entire queue");
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    if (args.length == 1) {
      return Arrays.asList("add", "remove", "list", "current", "next", "previous", "clear");
    }
    return new ArrayList<>();
  }
}