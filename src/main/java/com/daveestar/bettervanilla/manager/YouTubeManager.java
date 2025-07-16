package com.daveestar.bettervanilla.manager;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.configuration.file.FileConfiguration;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.Config;

import net.md_5.bungee.api.ChatColor;

public class YouTubeManager {
  
  private final Main _plugin;
  private final Config _config;
  private final FileConfiguration _fileConfig;
  
  // YouTube URL pattern to detect YouTube links
  private static final Pattern YOUTUBE_PATTERN = Pattern.compile(
    "(?:https?://)?(?:www\\.)?(?:youtube\\.com/watch\\?v=|youtu\\.be/)([a-zA-Z0-9_-]{11})"
  );
  
  // Track currently playing songs per player
  private final Map<UUID, PlaybackSession> _activeSessions = new HashMap<>();
  
  public YouTubeManager(Config config) {
    _plugin = Main.getInstance();
    _config = config;
    _fileConfig = config.getFileConfig();
    
    _loadConfig();
  }
  
  private void _loadConfig() {
    // Create sections for YouTube data if not present
    if (!_fileConfig.contains("youtube")) {
      _fileConfig.createSection("youtube");
    }
    
    if (!_fileConfig.contains("youtube.songs")) {
      _fileConfig.createSection("youtube.songs");
    }
    
    if (!_fileConfig.contains("youtube.settings")) {
      _fileConfig.createSection("youtube.settings");
      _fileConfig.set("youtube.settings.defaultVolume", 0.5);
      _fileConfig.set("youtube.settings.maxDuration", 600); // 10 minutes max
      _config.save();
    }
  }
  
  /**
   * Check if a message contains a YouTube link and extract video ID
   */
  public String extractYouTubeVideoId(String message) {
    Matcher matcher = YOUTUBE_PATTERN.matcher(message);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }
  
  /**
   * Handle YouTube link found in chat
   */
  public void handleYouTubeLink(Player player, String message, String videoId) {
    // Extract metadata and store in config
    String url = "https://youtube.com/watch?v=" + videoId;
    String title = extractTitleFromMessage(message, videoId);
    
    // Store in YAML
    storeYouTubeData(videoId, title, url, player.getName());
    
    // Notify player
    player.sendMessage(Main.getPrefix() + "YouTube link detected: " + ChatColor.YELLOW + title);
    player.sendMessage(Main.getShortPrefix() + "Use " + ChatColor.YELLOW + "/youtube play " + videoId + ChatColor.GRAY + " to play this song!");
    
    // Play notification sound
    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.7F, 1.2F);
  }
  
  /**
   * Extract a reasonable title from the chat message or generate one
   */
  private String extractTitleFromMessage(String message, String videoId) {
    // Try to find text before the URL that might be a title
    String[] words = message.split("\\s+");
    StringBuilder titleBuilder = new StringBuilder();
    
    for (String word : words) {
      if (word.contains("youtube.com") || word.contains("youtu.be")) {
        break;
      }
      if (!word.isEmpty() && titleBuilder.length() < 50) {
        if (titleBuilder.length() > 0) titleBuilder.append(" ");
        titleBuilder.append(word);
      }
    }
    
    String title = titleBuilder.toString().trim();
    if (title.isEmpty() || title.length() < 3) {
      // Generate a friendly name based on video ID
      title = "YouTube Song " + videoId.substring(0, 8);
    }
    
    return title;
  }
  
  /**
   * Store YouTube video data in YAML configuration
   */
  private void storeYouTubeData(String videoId, String title, String url, String sharedBy) {
    String path = "youtube.songs." + videoId;
    _fileConfig.set(path + ".title", title);
    _fileConfig.set(path + ".url", url);
    _fileConfig.set(path + ".sharedBy", sharedBy);
    _fileConfig.set(path + ".timestamp", System.currentTimeMillis());
    _config.save();
  }
  
  /**
   * Play a YouTube song for a player
   */
  public void playSong(Player player, String videoId) {
    // Stop any current playback
    stopSong(player);
    
    // Get song data
    String path = "youtube.songs." + videoId;
    if (!_fileConfig.contains(path)) {
      player.sendMessage(Main.getPrefix() + ChatColor.RED + "Song not found!");
      return;
    }
    
    String title = _fileConfig.getString(path + ".title", "Unknown");
    double volume = getPlayerVolume(player);
    
    // Create playback session
    PlaybackSession session = new PlaybackSession(videoId, title);
    _activeSessions.put(player.getUniqueId(), session);
    
    // Start playback simulation (using note block sounds as placeholder)
    startPlaybackSimulation(player, session, volume);
    
    // Notify all nearby players about the music
    notifyNearbyPlayers(player, title);
    
    // Notify player
    player.sendMessage(Main.getPrefix() + "Now playing: " + ChatColor.YELLOW + title);
    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8F, 1.0F);
  }
  
  /**
   * Stop playback for a player
   */
  public void stopSong(Player player) {
    PlaybackSession session = _activeSessions.remove(player.getUniqueId());
    if (session != null) {
      session.cancel();
      player.sendMessage(Main.getPrefix() + "Stopped playback: " + ChatColor.YELLOW + session.getTitle());
      player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.8F);
    }
  }
  
  /**
   * Get all stored YouTube songs
   */
  public List<String> getStoredSongs() {
    List<String> songs = new ArrayList<>();
    if (_fileConfig.contains("youtube.songs")) {
      songs.addAll(_fileConfig.getConfigurationSection("youtube.songs").getKeys(false));
    }
    return songs;
  }
  
  /**
   * Get song title by video ID
   */
  public String getSongTitle(String videoId) {
    return _fileConfig.getString("youtube.songs." + videoId + ".title", "Unknown");
  }
  
  /**
   * Set volume for a specific player
   */
  public void setPlayerVolume(Player player, double volume) {
    _fileConfig.set("youtube.players." + player.getUniqueId() + ".volume", volume);
    _config.save();
    
    // Update current session if playing
    PlaybackSession session = _activeSessions.get(player.getUniqueId());
    if (session != null) {
      // Restart playback with new volume
      session.cancel();
      startPlaybackSimulation(player, session, volume);
    }
  }
  
  /**
   * Get player's volume setting
   */
  private double getPlayerVolume(Player player) {
    return _fileConfig.getDouble("youtube.players." + player.getUniqueId() + ".volume", 
                                _fileConfig.getDouble("youtube.settings.defaultVolume", 0.5));
  }
  
  /**
   * Simulate audio playback using Minecraft sounds
   * In a full implementation, this would handle actual audio streaming
   */
  private void startPlaybackSimulation(Player player, PlaybackSession session, double volume) {
    // Create a repeating task that plays musical notes to simulate audio
    BukkitTask task = _plugin.getServer().getScheduler().runTaskTimer(_plugin, () -> {
      if (!player.isOnline() || !_activeSessions.containsKey(player.getUniqueId())) {
        return;
      }
      
      // Play a sequence of musical notes to simulate a melody
      Sound[] musicalSounds = {
        Sound.BLOCK_NOTE_BLOCK_HARP,
        Sound.BLOCK_NOTE_BLOCK_BASS,
        Sound.BLOCK_NOTE_BLOCK_BELL,
        Sound.BLOCK_NOTE_BLOCK_GUITAR,
        Sound.BLOCK_NOTE_BLOCK_CHIME,
        Sound.BLOCK_NOTE_BLOCK_FLUTE,
        Sound.BLOCK_NOTE_BLOCK_XYLOPHONE
      };
      
      // Create a more musical sequence
      int tick = session.getCurrentTick();
      Sound sound = musicalSounds[tick % musicalSounds.length];
      
      // Generate a somewhat musical pitch progression
      float basePitch = 0.5F;
      float[] melody = {1.0F, 1.1F, 1.3F, 1.5F, 1.3F, 1.1F, 0.9F, 1.0F};
      float pitch = basePitch * melody[tick % melody.length];
      
      player.playSound(player.getLocation(), sound, (float) volume * 0.3F, pitch);
      session.incrementTick();
      
    }, 0L, 10L); // Every half second for better musical flow
    
    session.setTask(task);
    
    // Auto-stop after a reasonable duration (5 minutes)
    _plugin.getServer().getScheduler().runTaskLater(_plugin, () -> {
      stopSong(player);
    }, 6000L); // 5 minutes in ticks
  }
  
  /**
   * Notify nearby players that music is being played
   */
  private void notifyNearbyPlayers(Player player, String title) {
    for (Player nearbyPlayer : _plugin.getServer().getOnlinePlayers()) {
      if (nearbyPlayer != player && 
          nearbyPlayer.getWorld() == player.getWorld() &&
          nearbyPlayer.getLocation().distance(player.getLocation()) <= 50) {
        nearbyPlayer.sendMessage(Main.getShortPrefix() + ChatColor.GREEN + player.getName() + 
                                ChatColor.GRAY + " is playing: " + ChatColor.YELLOW + title);
      }
    }
  }
  
  /**
   * Clean up sessions for disconnected players
   */
  public void onPlayerLeft(Player player) {
    stopSong(player);
  }
  
  /**
   * Inner class to track playback sessions
   */
  private static class PlaybackSession {
    private final String videoId;
    private final String title;
    private BukkitTask task;
    private int currentTick = 0;
    
    public PlaybackSession(String videoId, String title) {
      this.videoId = videoId;
      this.title = title;
    }
    
    public String getVideoId() {
      return videoId;
    }
    
    public String getTitle() {
      return title;
    }
    
    public void setTask(BukkitTask task) {
      this.task = task;
    }
    
    public int getCurrentTick() {
      return currentTick;
    }
    
    public void incrementTick() {
      currentTick++;
    }
    
    public void cancel() {
      if (task != null) {
        task.cancel();
      }
    }
  }
}