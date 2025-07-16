package com.daveestar.bettervanilla.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.daveestar.bettervanilla.utils.Config;

public class JukeboxManager {
  private Config _config;
  private FileConfiguration _fileConfig;
  private int _currentIndex = 0;

  public JukeboxManager(Config config) {
    _config = config;
    _fileConfig = config.getFileConfig();
    
    // Load current index from config
    _currentIndex = _fileConfig.getInt("currentIndex", 0);
  }

  public List<String> getQueueTitles() {
    ConfigurationSection queueSection = _fileConfig.getConfigurationSection("queue");
    List<String> titles = new ArrayList<>();

    if (queueSection != null) {
      for (String key : queueSection.getKeys(false)) {
        String title = queueSection.getString(key + ".title", "Unknown Song");
        titles.add(title);
      }
    }

    return titles;
  }

  public List<String> getQueueUrls() {
    ConfigurationSection queueSection = _fileConfig.getConfigurationSection("queue");
    List<String> urls = new ArrayList<>();

    if (queueSection != null) {
      for (String key : queueSection.getKeys(false)) {
        String url = queueSection.getString(key + ".url", "");
        urls.add(url);
      }
    }

    return urls;
  }

  public void addSong(String title, String url, UUID addedBy) {
    ConfigurationSection queueSection = _fileConfig.getConfigurationSection("queue");
    int nextIndex = 0;

    if (queueSection != null) {
      nextIndex = queueSection.getKeys(false).size();
    }

    _fileConfig.set("queue." + nextIndex + ".title", title);
    _fileConfig.set("queue." + nextIndex + ".url", url);
    _fileConfig.set("queue." + nextIndex + ".addedBy", addedBy.toString());
    
    _config.save();
  }

  public void removeSong(int index) {
    ConfigurationSection queueSection = _fileConfig.getConfigurationSection("queue");
    
    if (queueSection != null) {
      List<String> keys = new ArrayList<>(queueSection.getKeys(false));
      if (index >= 0 && index < keys.size()) {
        // Remove the song at the specified index
        _fileConfig.set("queue." + index, null);
        
        // Reorder remaining songs
        for (int i = index + 1; i < keys.size(); i++) {
          ConfigurationSection songSection = queueSection.getConfigurationSection(String.valueOf(i));
          if (songSection != null) {
            String title = songSection.getString("title");
            String url = songSection.getString("url");
            String addedBy = songSection.getString("addedBy");
            
            _fileConfig.set("queue." + (i - 1) + ".title", title);
            _fileConfig.set("queue." + (i - 1) + ".url", url);
            _fileConfig.set("queue." + (i - 1) + ".addedBy", addedBy);
            _fileConfig.set("queue." + i, null);
          }
        }
        
        // Adjust current index if necessary
        if (_currentIndex > index) {
          _currentIndex--;
        } else if (_currentIndex == index) {
          // If we removed the current song, stay at the same index (which now points to the next song)
          if (_currentIndex >= getQueueSize()) {
            _currentIndex = 0; // Loop back to start if we're at the end
          }
        }
        
        _fileConfig.set("currentIndex", _currentIndex);
        _config.save();
      }
    }
  }

  public String getCurrentSongTitle() {
    ConfigurationSection queueSection = _fileConfig.getConfigurationSection("queue");
    
    if (queueSection != null && queueSection.contains(String.valueOf(_currentIndex))) {
      return queueSection.getString(_currentIndex + ".title", "Unknown Song");
    }
    
    return "No songs in queue";
  }

  public String getCurrentSongUrl() {
    ConfigurationSection queueSection = _fileConfig.getConfigurationSection("queue");
    
    if (queueSection != null && queueSection.contains(String.valueOf(_currentIndex))) {
      return queueSection.getString(_currentIndex + ".url", "");
    }
    
    return "";
  }

  public void skipToNext() {
    int queueSize = getQueueSize();
    if (queueSize > 0) {
      _currentIndex = (_currentIndex + 1) % queueSize;
      _fileConfig.set("currentIndex", _currentIndex);
      _config.save();
    }
  }

  public void skipToPrevious() {
    int queueSize = getQueueSize();
    if (queueSize > 0) {
      _currentIndex = (_currentIndex - 1 + queueSize) % queueSize;
      _fileConfig.set("currentIndex", _currentIndex);
      _config.save();
    }
  }

  public void skipToIndex(int index) {
    int queueSize = getQueueSize();
    if (index >= 0 && index < queueSize) {
      _currentIndex = index;
      _fileConfig.set("currentIndex", _currentIndex);
      _config.save();
    }
  }

  public int getCurrentIndex() {
    return _currentIndex;
  }

  public int getQueueSize() {
    ConfigurationSection queueSection = _fileConfig.getConfigurationSection("queue");
    return queueSection != null ? queueSection.getKeys(false).size() : 0;
  }

  public void clearQueue() {
    _fileConfig.set("queue", null);
    _currentIndex = 0;
    _fileConfig.set("currentIndex", _currentIndex);
    _config.save();
  }
}