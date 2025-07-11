package com.daveestar.bettervanilla.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class Backpack {
  private final UUID _playerId;
  private final Map<Integer, ItemStack[]> _pages;
  private final int _pageSize;
  private final int _totalPages;

  public Backpack(UUID playerId, int totalPages, int pageSize, FileConfiguration cfg) {
    _playerId = playerId;
    _totalPages = totalPages;
    _pageSize = pageSize;
    _pages = new HashMap<>();

    for (int i = 1; i <= totalPages; i++) {
      _pages.put(i, _loadPage(cfg, i));
    }
  }

  private ItemStack[] _loadPage(FileConfiguration cfg, int page) {
    List<?> list = cfg.getList("players." + _playerId + ".page" + page);
    ItemStack[] arr = ItemStackUtils.deserializeArray(list);
    if (arr.length < _pageSize) {
      ItemStack[] tmp = new ItemStack[_pageSize];
      System.arraycopy(arr, 0, tmp, 0, arr.length);
      return tmp;
    }
    return Arrays.copyOf(arr, _pageSize);
  }

  public ItemStack[] getPage(int page) {
    return _pages.get(page);
  }

  public void setPage(int page, ItemStack[] items) {
    if (items.length != _pageSize) {
      items = Arrays.copyOf(items, _pageSize);
    }
    _pages.put(page, items);
  }

  public void savePage(FileConfiguration cfg, int page) {
    ItemStack[] items = _pages.get(page);
    List<Map<String, Object>> data = ItemStackUtils.serializeArray(items);
    cfg.set("players." + _playerId + ".page" + page, data);
  }

  public int getTotalPages() {
    return _totalPages;
  }
}
