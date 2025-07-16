package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.JukeboxManager;
import com.daveestar.bettervanilla.utils.CustomGUI;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class JukeboxGUI {
  private final Main _plugin;
  private final JukeboxManager _jukeboxManager;

  public JukeboxGUI() {
    _plugin = Main.getInstance();
    _jukeboxManager = _plugin.getJukeboxManager();
  }

  public void displayGUI(Player p) {
    List<String> titles = _jukeboxManager.getQueueTitles();
    List<String> urls = _jukeboxManager.getQueueUrls();
    int currentIndex = _jukeboxManager.getCurrentIndex();

    Map<String, ItemStack> jukeboxEntries = new HashMap<>();
    
    // Add current playing song at the top
    if (!titles.isEmpty()) {
      jukeboxEntries.put("current", _createCurrentSongItem(titles.get(currentIndex), urls.get(currentIndex)));
    }

    // Add control buttons
    jukeboxEntries.put("previous", _createPreviousButton());
    jukeboxEntries.put("next", _createNextButton());
    jukeboxEntries.put("clear", _createClearButton());

    // Add all songs in queue
    for (int i = 0; i < titles.size(); i++) {
      String key = "song_" + i;
      jukeboxEntries.put(key, _createQueueItem(titles.get(i), urls.get(i), i, i == currentIndex));
    }

    String title = ChatColor.YELLOW + "" + ChatColor.BOLD + "» Jukebox";
    
    Map<String, Integer> customSlots = new HashMap<>();
    if (!titles.isEmpty()) {
      customSlots.put("current", 4); // Center of top row
    }
    customSlots.put("previous", 10); // Second row, left
    customSlots.put("next", 12); // Second row, right
    customSlots.put("clear", 16); // Second row, far right

    CustomGUI jukeboxGUI = new CustomGUI(_plugin, p, title, jukeboxEntries, 6, customSlots, null, null);
    jukeboxGUI.open(p);

    Map<String, CustomGUI.ClickAction> clickActions = new HashMap<>();
    
    // Current song click action (open URL in chat)
    if (!titles.isEmpty()) {
      clickActions.put("current", new CustomGUI.ClickAction() {
        public void onLeftClick(Player p) {
          String currentUrl = _jukeboxManager.getCurrentSongUrl();
          p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + "Current Song URL: " + ChatColor.BLUE + ChatColor.UNDERLINE + currentUrl);
          p.closeInventory();
        }
      });
    }

    // Control button actions
    clickActions.put("previous", new CustomGUI.ClickAction() {
      public void onLeftClick(Player p) {
        if (_jukeboxManager.getQueueSize() > 0) {
          _jukeboxManager.skipToPrevious();
          p.playSound(p, Sound.UI_BUTTON_CLICK, 0.7F, 1.0F);
          displayGUI(p); // Refresh GUI
        }
      }
    });

    clickActions.put("next", new CustomGUI.ClickAction() {
      public void onLeftClick(Player p) {
        if (_jukeboxManager.getQueueSize() > 0) {
          _jukeboxManager.skipToNext();
          p.playSound(p, Sound.UI_BUTTON_CLICK, 0.7F, 1.0F);
          displayGUI(p); // Refresh GUI
        }
      }
    });

    clickActions.put("clear", new CustomGUI.ClickAction() {
      public void onLeftClick(Player p) {
        _jukeboxManager.clearQueue();
        p.playSound(p, Sound.UI_BUTTON_CLICK, 0.7F, 1.0F);
        p.sendMessage(Main.getPrefix() + "Cleared the jukebox queue.");
        p.closeInventory();
      }
    });

    // Queue item actions
    for (int i = 0; i < titles.size(); i++) {
      final int index = i;
      String key = "song_" + i;
      clickActions.put(key, new CustomGUI.ClickAction() {
        public void onLeftClick(Player p) {
          _jukeboxManager.skipToIndex(index);
          p.playSound(p, Sound.UI_BUTTON_CLICK, 0.7F, 1.0F);
          displayGUI(p); // Refresh GUI
        }

        public void onRightClick(Player p) {
          _displaySongOptionsGUI(p, index, jukeboxGUI);
        }
      });
    }

    jukeboxGUI.setClickActions(clickActions);
  }

  private void _displaySongOptionsGUI(Player p, int songIndex, CustomGUI parentMenu) {
    List<String> titles = _jukeboxManager.getQueueTitles();
    List<String> urls = _jukeboxManager.getQueueUrls();
    
    if (songIndex >= titles.size()) return;

    String songTitle = titles.get(songIndex);
    String title = ChatColor.YELLOW + "" + ChatColor.BOLD + "» " + songTitle;

    Map<String, ItemStack> optionEntries = new HashMap<>();
    optionEntries.put("play", _createPlayItem());
    optionEntries.put("url", _createUrlItem());
    optionEntries.put("remove", _createRemoveItem());

    Map<String, Integer> customSlots = new HashMap<>();
    customSlots.put("play", 2);
    customSlots.put("url", 4);
    customSlots.put("remove", 6);

    CustomGUI optionsGUI = new CustomGUI(_plugin, p, title, optionEntries, 2, customSlots, parentMenu,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    Map<String, CustomGUI.ClickAction> optionClickActions = new HashMap<>();

    optionClickActions.put("play", new CustomGUI.ClickAction() {
      public void onLeftClick(Player p) {
        _jukeboxManager.skipToIndex(songIndex);
        p.playSound(p, Sound.UI_BUTTON_CLICK, 0.7F, 1.0F);
        p.sendMessage(Main.getPrefix() + "Now playing: " + ChatColor.YELLOW + songTitle);
        p.closeInventory();
      }
    });

    optionClickActions.put("url", new CustomGUI.ClickAction() {
      public void onLeftClick(Player p) {
        String url = urls.get(songIndex);
        p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + songTitle);
        p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + "URL: " + ChatColor.BLUE + ChatColor.UNDERLINE + url);
        p.closeInventory();
      }
    });

    optionClickActions.put("remove", new CustomGUI.ClickAction() {
      public void onLeftClick(Player p) {
        _jukeboxManager.removeSong(songIndex);
        p.playSound(p, Sound.UI_BUTTON_CLICK, 0.7F, 1.0F);
        p.sendMessage(Main.getPrefix() + "Removed " + ChatColor.YELLOW + songTitle + ChatColor.GRAY + " from the queue.");
        p.closeInventory();
      }
    });

    optionsGUI.setClickActions(optionClickActions);
    optionsGUI.open(p);
  }

  // ---------------
  // ITEM CREATORS
  // ---------------

  private ItemStack _createCurrentSongItem(String title, String url) {
    ItemStack item = new ItemStack(Material.JUKEBOX);
    ItemMeta meta = item.getItemMeta();
    meta.displayName(Component.text(ChatColor.GREEN + "♫ Now Playing"));
    meta.lore(Arrays.asList(
        Component.text(ChatColor.YELLOW + title),
        Component.text(""),
        Component.text(ChatColor.GRAY + "Click to copy URL to chat")
    ));
    item.setItemMeta(meta);
    return item;
  }

  private ItemStack _createQueueItem(String title, String url, int index, boolean isCurrentlyPlaying) {
    Material material = isCurrentlyPlaying ? Material.MUSIC_DISC_13 : Material.MUSIC_DISC_CAT;
    ItemStack item = new ItemStack(material);
    ItemMeta meta = item.getItemMeta();
    
    String displayName = isCurrentlyPlaying ? 
        ChatColor.GREEN + "▶ " + (index + 1) + ". " + title :
        ChatColor.YELLOW + "" + (index + 1) + ". " + ChatColor.GRAY + title;
    
    meta.displayName(Component.text(displayName));
    meta.lore(Arrays.asList(
        Component.text(""),
        Component.text(ChatColor.GRAY + "Left-Click: Play this song"),
        Component.text(ChatColor.GRAY + "Right-Click: Song options")
    ));
    item.setItemMeta(meta);
    return item;
  }

  private ItemStack _createPreviousButton() {
    ItemStack item = new ItemStack(Material.ARROW);
    ItemMeta meta = item.getItemMeta();
    meta.displayName(Component.text(ChatColor.YELLOW + "⏮ Previous"));
    meta.lore(Arrays.asList(
        Component.text(ChatColor.GRAY + "Go to the previous song")
    ));
    item.setItemMeta(meta);
    return item;
  }

  private ItemStack _createNextButton() {
    ItemStack item = new ItemStack(Material.ARROW);
    ItemMeta meta = item.getItemMeta();
    meta.displayName(Component.text(ChatColor.YELLOW + "⏭ Next"));
    meta.lore(Arrays.asList(
        Component.text(ChatColor.GRAY + "Skip to the next song")
    ));
    item.setItemMeta(meta);
    return item;
  }

  private ItemStack _createClearButton() {
    ItemStack item = new ItemStack(Material.BARRIER);
    ItemMeta meta = item.getItemMeta();
    meta.displayName(Component.text(ChatColor.RED + "Clear Queue"));
    meta.lore(Arrays.asList(
        Component.text(ChatColor.GRAY + "Remove all songs from the queue")
    ));
    item.setItemMeta(meta);
    return item;
  }

  private ItemStack _createPlayItem() {
    ItemStack item = new ItemStack(Material.GREEN_CONCRETE);
    ItemMeta meta = item.getItemMeta();
    meta.displayName(Component.text(ChatColor.GREEN + "Play"));
    meta.lore(Arrays.asList(
        Component.text(ChatColor.GRAY + "Skip to this song")
    ));
    item.setItemMeta(meta);
    return item;
  }

  private ItemStack _createUrlItem() {
    ItemStack item = new ItemStack(Material.CHAIN);
    ItemMeta meta = item.getItemMeta();
    meta.displayName(Component.text(ChatColor.BLUE + "Show URL"));
    meta.lore(Arrays.asList(
        Component.text(ChatColor.GRAY + "Display the YouTube URL")
    ));
    item.setItemMeta(meta);
    return item;
  }

  private ItemStack _createRemoveItem() {
    ItemStack item = new ItemStack(Material.RED_CONCRETE);
    ItemMeta meta = item.getItemMeta();
    meta.displayName(Component.text(ChatColor.RED + "Remove"));
    meta.lore(Arrays.asList(
        Component.text(ChatColor.GRAY + "Remove this song from the queue")
    ));
    item.setItemMeta(meta);
    return item;
  }
}