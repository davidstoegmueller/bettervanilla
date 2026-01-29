package com.daveestar.bettervanilla.gui;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.HeadsManager;
import com.daveestar.bettervanilla.utils.CustomGUI;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class HeadsGUI {
  private static final int CATEGORY_GUI_ROWS = 6;
  private static final int HEADS_GUI_ROWS = 6;

  private static final String GUI_TITLE_PREFIX = ChatColor.YELLOW + "" + ChatColor.BOLD + "» ";
  private static final String GUI_ITEM_PREFIX = ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW;
  private static final String GUI_LORE_PREFIX = ChatColor.YELLOW + "» " + ChatColor.GRAY;

  private static final String KEY_CATEGORY_PREFIX = "category::";
  private static final String KEY_HEAD_PREFIX = "head::";
  private static final String KEY_CATEGORY_ID = "id";
  private static final String KEY_CATEGORY_NAME = "n";
  private static final String KEY_HEAD_NAME = "n";
  private static final String KEY_HEAD_CATEGORY_ID = "c";
  private static final String KEY_HEAD_URL = "u";

  private final Main _plugin;
  private final HeadsManager _headsManager;

  public HeadsGUI() {
    _plugin = Main.getInstance();
    _headsManager = _plugin.getHeadsManager();
  }

  public void displayHeadsGUI(Player p) {
    CustomGUI gui = _createCategoriesGUI(p);
    gui.open(p);
  }

  // -----------
  // CREATE GUIS
  // -----------

  private CustomGUI _createCategoriesGUI(Player p) {
    JsonArray headsData = _headsManager.getCustomHeadsData();
    int totalHeads = _headsManager.getTotalCustomHeads();

    List<HeadCategory> categories = _buildCategories();
    if (categories.isEmpty()) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "No head categories found.");
      return null;
    }

    Map<HeadCategory, List<Head>> headsByCategory = _getHeadsByCategory(headsData, categories);

    Map<String, ItemStack> entries = new LinkedHashMap<>();
    Map<String, HeadCategory> categoryByKey = new HashMap<>();
    Map<String, CategorySortData> sortData = new HashMap<>();

    for (HeadCategory category : categories) {
      String key = KEY_CATEGORY_PREFIX + category.id();
      int count = headsByCategory.getOrDefault(category, List.of()).size();
      ItemStack categoryItem = _createCategoryItem(category, count);

      entries.put(key, categoryItem);
      categoryByKey.put(key, category);
      sortData.put(key, new CategorySortData(category.name(), count));
    }

    CustomGUI categoriesGUI = new CustomGUI(_plugin, p,
        GUI_TITLE_PREFIX + "Categories" + ChatColor.GRAY + " (" + totalHeads + " heads)",
        entries, CATEGORY_GUI_ROWS, null, null,
        EnumSet.of(CustomGUI.Option.ENABLE_SEARCH, CustomGUI.Option.ENABLE_SORT));

    categoriesGUI.setSearchButtonSlot(_footerSearchSlot(CATEGORY_GUI_ROWS));
    categoriesGUI.setSortButtonSlot(_footerSortSlot(CATEGORY_GUI_ROWS));
    categoriesGUI.setSortOptions(_createCategorySortOptions(sortData));

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    for (Map.Entry<String, HeadCategory> entry : categoryByKey.entrySet()) {
      String key = entry.getKey();
      HeadCategory category = entry.getValue();

      actions.put(key, new CustomGUI.ClickAction() {
        @Override
        public void onLeftClick(Player player) {
          List<Head> heads = headsByCategory.getOrDefault(category, List.of());

          if (heads.isEmpty()) {
            player.sendMessage(Main.getPrefix() + ChatColor.RED + "No heads available in this category.");
            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
            return;
          }

          _displayHeadsGUI(player, category, heads, categoriesGUI);
        }
      });
    }

    categoriesGUI.setClickActions(actions);
    return categoriesGUI;
  }

  private void _displayHeadsGUI(Player p, HeadCategory category, List<Head> heads, CustomGUI parentMenu) {
    Map<String, ItemStack> entries = new LinkedHashMap<>();
    Map<String, HeadSortData> sortData = new HashMap<>();
    Map<String, Head> headByKey = new HashMap<>();

    for (Head head : heads) {
      ItemStack headItem = _createHeadItem(head, category, true);
      String entryId = KEY_HEAD_PREFIX + head.uid();

      entries.put(entryId, headItem);
      headByKey.put(entryId, head);
      sortData.put(entryId, new HeadSortData(head.name()));
    }

    String title = GUI_TITLE_PREFIX + "Heads" + ChatColor.GRAY + " (" + category.name() + " " + heads.size()
        + " heads)";
    CustomGUI headsGUI = new CustomGUI(_plugin, p, title, entries, HEADS_GUI_ROWS, null, parentMenu,
        EnumSet.of(CustomGUI.Option.ENABLE_SEARCH, CustomGUI.Option.ENABLE_SORT));

    headsGUI.setSearchButtonSlot(_footerSearchSlot(HEADS_GUI_ROWS));
    headsGUI.setSortButtonSlot(_footerSortSlot(HEADS_GUI_ROWS));
    headsGUI.setSortOptions(_createHeadSortOptions(sortData));

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    for (Map.Entry<String, Head> entry : headByKey.entrySet()) {
      String key = entry.getKey();
      Head head = entry.getValue();

      actions.put(key, new CustomGUI.ClickAction() {
        @Override
        public void onLeftClick(Player player) {
          _giveHead(player, head);
        }
      });
    }

    headsGUI.setClickActions(actions);
    headsGUI.open(p);
  }

  // -------------------
  // BUILD CATEGORY DATA
  // -------------------

  private List<HeadCategory> _buildCategories() {
    List<HeadCategory> categories = new ArrayList<>();
    JsonArray categoryData = _headsManager.getCustomHeadCategoriesData();

    for (JsonElement categoryElement : categoryData) {
      JsonObject categoryObj = categoryElement.getAsJsonObject();
      int id = categoryObj.get(KEY_CATEGORY_ID).getAsInt();
      String name = categoryObj.get(KEY_CATEGORY_NAME).getAsString();

      categories.add(new HeadCategory(id, name));
    }

    return categories;
  }

  private Map<HeadCategory, List<Head>> _getHeadsByCategory(
      JsonArray headsData,
      List<HeadCategory> categories) {
    Map<Integer, HeadCategory> categoryById = new HashMap<>();
    for (HeadCategory category : categories) {
      categoryById.put(category.id(), category);
    }

    Map<HeadCategory, List<Head>> headsByCategory = new LinkedHashMap<>();

    for (JsonElement headElement : headsData) {
      JsonObject headObj = headElement.getAsJsonObject();

      UUID headId = UUID.randomUUID();
      String headName = headObj.get(KEY_HEAD_NAME).getAsString();
      int headCategoryId = headObj.get(KEY_HEAD_CATEGORY_ID).getAsInt();
      String url = headObj.get(KEY_HEAD_URL).getAsString();

      HeadCategory headCategory = categoryById.get(headCategoryId);

      headsByCategory
          .computeIfAbsent(headCategory, k -> new ArrayList<>())
          .add(new Head(headId, headName, headCategoryId, url));
    }

    return headsByCategory;
  }

  // ---------
  // GUI ITEMS
  // ---------

  private ItemStack _createCategoryItem(HeadCategory category, int count) {
    ItemStack item = new ItemStack(Material.BOOK);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(GUI_ITEM_PREFIX + category.name()));

      List<String> lore = new ArrayList<>();
      lore.add("");
      lore.add(GUI_LORE_PREFIX + "Total Heads: " + ChatColor.YELLOW + count);

      lore.add("");
      lore.add(GUI_LORE_PREFIX + "Left-Click: Open");

      meta.lore(lore.stream().map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createHeadItem(Head head, HeadCategory category, boolean includeInstructions) {
    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    ItemMeta meta = item.getItemMeta();

    if (meta instanceof SkullMeta skullMeta) {
      _applyHeadTexture(head, skullMeta);

      meta.displayName(Component.text(GUI_ITEM_PREFIX + head.name()));

      List<String> lore = new ArrayList<>();
      if (includeInstructions && category != null) {
        lore.add("");
        lore.add(GUI_LORE_PREFIX + "Category: " + ChatColor.YELLOW + category.name());
        lore.add("");
        lore.add(GUI_LORE_PREFIX + "Left-Click: Give");
      }

      meta.lore(lore.stream().map(Component::text).collect(Collectors.toList()));
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      item.setItemMeta(meta);
    }

    return item;
  }

  // ------------
  // GIVE A HEAD
  // ------------

  private void _giveHead(Player p, Head head) {
    ItemStack item = _createHeadItem(head, null, false);

    Map<Integer, ItemStack> inventoryOverflow = p.getInventory().addItem(item);
    if (!inventoryOverflow.isEmpty()) {
      inventoryOverflow.values().forEach(left -> p.getWorld().dropItemNaturally(p.getLocation(), left));
    }

    p.playSound(p, Sound.ENTITY_ITEM_PICKUP, 0.6F, 1.2F);
  }

  // -----------------
  // TEXTURE HANDLING
  // -----------------

  private void _applyHeadTexture(Head head, SkullMeta meta) {
    try {
      String json = "{\"textures\":{\"SKIN\":{\"url\":\"https://textures.minecraft.net/texture/"
          + head.url()
          + "\"}}}";

      String base64 = Base64.getEncoder()
          .encodeToString(json.getBytes(StandardCharsets.UTF_8));

      PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
      profile.setProperty(new ProfileProperty("textures", base64));
      meta.setPlayerProfile(profile);
    } catch (Exception ignored) {
      // ignore invalid textures
    }
  }

  // ------------
  // SORTING
  // ------------

  private List<CustomGUI.SortOption> _createCategorySortOptions(Map<String, CategorySortData> sortData) {
    Comparator<Map.Entry<String, ItemStack>> byNameAsc = Comparator.comparing(
        entry -> sortData.get(entry.getKey()).name(), String.CASE_INSENSITIVE_ORDER);

    Comparator<Map.Entry<String, ItemStack>> byNameDesc = byNameAsc.reversed();

    Comparator<Map.Entry<String, ItemStack>> byCountDesc = Comparator.<Map.Entry<String, ItemStack>>comparingInt(
        entry -> sortData.get(entry.getKey()).count())
        .reversed();

    Comparator<Map.Entry<String, ItemStack>> byCountAsc = Comparator.<Map.Entry<String, ItemStack>>comparingInt(
        entry -> sortData.get(entry.getKey()).count());

    return List.of(
        new CustomGUI.SortOption("Name (A - Z)", byNameAsc),
        new CustomGUI.SortOption("Name (Z - A)", byNameDesc),
        new CustomGUI.SortOption("Count (High - Low)", byCountDesc),
        new CustomGUI.SortOption("Count (Low - High)", byCountAsc));
  }

  private List<CustomGUI.SortOption> _createHeadSortOptions(Map<String, HeadSortData> sortData) {
    Comparator<Map.Entry<String, ItemStack>> byNameAsc = Comparator.comparing(
        entry -> sortData.get(entry.getKey()).name(), String.CASE_INSENSITIVE_ORDER);

    Comparator<Map.Entry<String, ItemStack>> byNameDesc = byNameAsc.reversed();

    return List.of(
        new CustomGUI.SortOption("Name (A - Z)", byNameAsc),
        new CustomGUI.SortOption("Name (Z - A)", byNameDesc));
  }

  // ---------
  // HELPERS
  // ---------

  private int _footerSearchSlot(int rows) {
    return (rows * 9) - 9 + 7;
  }

  private int _footerSortSlot(int rows) {
    return (rows * 9) - 9 + 6;
  }

  private record HeadCategory(int id, String name) {
  }

  private record Head(UUID uid, String name, int categoryId, String url) {
  }

  private record HeadSortData(String name) {
  }

  private record CategorySortData(String name, int count) {
  }
}
