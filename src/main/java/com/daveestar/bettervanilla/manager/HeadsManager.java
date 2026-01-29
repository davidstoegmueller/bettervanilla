package com.daveestar.bettervanilla.manager;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.HttpUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class HeadsManager {
  private static final String APP_UUID = "521a4c0c-17e7-4cda-9b60-2e98520abf26";
  private static final String API_BASE_URL = "https://minecraft-heads.com/api/heads/";
  private static final String API_CUSTOM_HEADS_URL = "custom-heads";
  private static final String API_CATEGORIES_URL = "categories";

  private static final String PARAM_APP_UUID = "app_uuid";
  private static final String HEADER_API_KEY = "api-key";
  private static final String PARAM_DEMO = "demo";
  private static final String PARAM_PAGE = "page";

  private static final String KEY_META = "meta";
  private static final String KEY_PAGINATION = "pagination";
  private static final String KEY_PAGINATION_LAST_PAGE = "last_page";
  private static final String KEY_DATA = "data";
  private static final String KEY_RECORDS = "records";
  private static final String KEY_WARNINGS = "warnings";

  private static final boolean DEMO_MODE = false;
  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(60);
  private static final Duration FETCH_COOLDOWN = Duration.ofSeconds(60);

  private JsonObject _customHeadsData;
  private JsonObject _customHeadCategoriesData;
  private long _lastFetchMs;

  private final Main _plugin;
  private SettingsManager _settingsManager;

  public HeadsManager() {
    _plugin = Main.getInstance();
  }

  public void initManagers() {
    _settingsManager = _plugin.getSettingsManager();
  }

  // ----------------
  // FETCH HEADS DATA
  // ----------------

  public CompletableFuture<Boolean> fetchHeadsData() {
    long now = System.currentTimeMillis();
    if (_lastFetchMs > 0 && now - _lastFetchMs < FETCH_COOLDOWN.toMillis()) {
      _plugin.getLogger().info("Heads data refresh skipped due to rate limit. Wait another "
          + getRemainingFetchCooldownSeconds() + " seconds.");
      return CompletableFuture.completedFuture(false);
    }

    _lastFetchMs = now;
    CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();
    Bukkit.getScheduler().runTaskAsynchronously(_plugin, () -> {
      boolean success = false;

      _plugin.getLogger().info("Refreshing heads data from Minecraft-Heads API...");

      try {
        JsonObject headsData = _fetchCustomHeadsData();
        JsonObject categoriesData = _fetchCustomHeadCategoriesData();

        boolean headsOk = _isCustomHeadsFetchSuccessful(headsData);
        boolean categoriesOk = _isCustomHeadCategoriesFetchSuccessful(categoriesData);

        if (headsOk) {
          _customHeadsData = headsData;
        }

        if (categoriesOk) {
          _customHeadCategoriesData = categoriesData;
        }

        success = headsOk && categoriesOk;

        if (success) {
          int headsSize = getTotalCustomHeads();
          int categoriesSize = getTotalCustomHeadCategories();
          JsonArray warnings = getCustomHeadWarnings();

          _plugin.getLogger().info("Total Custom Heads Fetched: " + headsSize);
          _plugin.getLogger().info("Total Custom Head Categories Fetched: " + categoriesSize);

          for (JsonElement warning : warnings) {
            _plugin.getLogger().warning("Custom Heads Warning: " + warning.getAsString());
          }
        }
      } catch (Exception e) {
        _plugin.getLogger().log(Level.SEVERE, "Failed to refresh heads data from Minecraft-Heads API.", e);
      }

      resultFuture.complete(success);
    });

    return resultFuture;
  }

  public long getRemainingFetchCooldownSeconds() {
    if (_lastFetchMs <= 0) {
      return 0;
    }

    long now = System.currentTimeMillis();
    long remainingMs = FETCH_COOLDOWN.toMillis() - (now - _lastFetchMs);
    return Math.max(0, remainingMs / 1000);
  }

  private boolean _isCustomHeadsFetchSuccessful(JsonObject data) {
    return data != null && data.has(KEY_DATA);
  }

  private boolean _isCustomHeadCategoriesFetchSuccessful(JsonObject data) {
    if (data == null || !data.has(KEY_META)) {
      return false;
    }

    JsonObject meta = data.getAsJsonObject(KEY_META);
    return meta.has(KEY_RECORDS);
  }

  // ---------------------------
  // GET HEADS & CATEGORIES DATA
  // ---------------------------

  public JsonArray getCustomHeadsData() {
    return _customHeadsData.getAsJsonArray(KEY_DATA);
  }

  public JsonArray getCustomHeadCategoriesData() {
    return _customHeadCategoriesData.getAsJsonArray(KEY_DATA);
  }

  public int getTotalCustomHeads() {
    if (_customHeadsData != null && _customHeadsData.has(KEY_DATA)) {
      return _customHeadsData.getAsJsonArray(KEY_DATA).size();
    }

    return 0;
  }

  // ----------------------------
  // GET HEAD & CATEGORIES AMOUNT
  // ----------------------------

  public int getTotalCustomHeadCategories() {
    if (_customHeadCategoriesData != null && _customHeadCategoriesData.has(KEY_META)) {
      JsonObject meta = _customHeadCategoriesData.getAsJsonObject(KEY_META);

      if (meta.has(KEY_RECORDS)) {
        return meta.get(KEY_RECORDS).getAsInt();
      }
    }

    return 0;
  }

  // -----------------
  // GET HEAD WARNINGS
  // -----------------

  public JsonArray getCustomHeadWarnings() {
    if (_customHeadsData != null && _customHeadsData.has(KEY_WARNINGS)) {
      return _customHeadsData.getAsJsonArray(KEY_WARNINGS);
    }

    return new JsonArray();
  }

  // -----------------------
  // FETCH INITIAL HEAD DATA
  // -----------------------

  private JsonObject _fetchCustomHeadsData() {
    try {
      Map<String, String> headers = _buildApiHeaders();
      String firstUrl = _buildGetCustomHeadsURL(DEMO_MODE, 1);
      JsonElement responseJSON = HttpUtils.sendGETRequest(firstUrl, REQUEST_TIMEOUT, headers);
      JsonObject responseObject = responseJSON.getAsJsonObject();

      if (responseObject.has(KEY_PAGINATION)) {
        JsonObject pagination = responseObject.getAsJsonObject(KEY_PAGINATION);
        int totalPages = pagination.get(KEY_PAGINATION_LAST_PAGE).getAsInt();

        for (int page = 2; page <= totalPages; page++) {
          String pagedUrl = _buildGetCustomHeadsURL(DEMO_MODE, page);
          JsonElement pagedResponseJSON = HttpUtils.sendGETRequest(pagedUrl, REQUEST_TIMEOUT, headers);
          JsonObject pagedResponseObject = pagedResponseJSON.getAsJsonObject();

          if (pagedResponseObject.has(KEY_DATA)) {
            for (JsonElement data : pagedResponseObject.getAsJsonArray(KEY_DATA)) {
              responseObject.getAsJsonArray(KEY_DATA).add(data);
            }
          }
        }
      }

      return responseObject;
    } catch (Exception e) {
      _plugin.getLogger().log(Level.SEVERE, "Failed to fetch custom heads from Minecraft-Heads API.", e);
    }

    return new JsonObject();
  }

  private JsonObject _fetchCustomHeadCategoriesData() {
    try {
      Map<String, String> headers = _buildApiHeaders();
      String url = _buildGetHeadCategoriesURL();
      JsonElement responseJSON = HttpUtils.sendGETRequest(url, REQUEST_TIMEOUT, headers);

      return responseJSON.getAsJsonObject();
    } catch (Exception e) {
      _plugin.getLogger().log(Level.SEVERE, "Failed to fetch head categories from Minecraft-Heads API.", e);
    }

    return new JsonObject();
  }

  // --------------
  // BUILD API URLS
  // --------------

  private String _buildGetCustomHeadsURL(boolean demo, int page) {
    StringBuilder urlBuilder = new StringBuilder(API_BASE_URL)
        .append(API_CUSTOM_HEADS_URL)
        .append("?")
        .append(PARAM_APP_UUID).append("=").append(APP_UUID)
        .append("&").append(PARAM_PAGE).append("=").append(page);

    if (demo) {
      urlBuilder.append("&").append(PARAM_DEMO).append("=").append(demo);
    }

    return urlBuilder.toString();
  }

  private String _buildGetHeadCategoriesURL() {
    StringBuilder urlBuilder = new StringBuilder(API_BASE_URL)
        .append(API_CATEGORIES_URL)
        .append("?")
        .append(PARAM_APP_UUID).append("=").append(APP_UUID);

    return urlBuilder.toString();
  }

  private String _getApiKey() {
    String apiKey = _settingsManager != null ? _settingsManager.getHeadsExplorerApiKey() : "";
    return apiKey == null ? "" : apiKey.trim();
  }

  private Map<String, String> _buildApiHeaders() {
    String apiKey = _getApiKey();
    if (apiKey.isEmpty()) {
      return Map.of();
    }

    Map<String, String> headers = new HashMap<>();
    headers.put(HEADER_API_KEY, apiKey);
    return headers;
  }
}
