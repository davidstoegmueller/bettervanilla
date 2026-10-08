package com.daveestar.bettervanilla.manager;

import java.time.Duration;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.HttpUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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

  private volatile Catalog _catalog;
  private volatile long _lastFetchMs;
  private CompletableFuture<Boolean> _fetchFuture;
  private BukkitTask _fetchTask;

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

  public synchronized CompletableFuture<Boolean> fetchHeadsData() {
    if (!_plugin.isEnabled()) {
      return CompletableFuture.completedFuture(false);
    }
    if (_fetchFuture != null && !_fetchFuture.isDone()) {
      return _fetchFuture;
    }
    long now = System.currentTimeMillis();
    if (_lastFetchMs > 0 && now - _lastFetchMs < FETCH_COOLDOWN.toMillis()) {
      _plugin.getLogger().info("Heads data refresh skipped due to rate limit. Wait another "
          + getRemainingFetchCooldownSeconds() + " seconds.");
      return CompletableFuture.completedFuture(false);
    }

    _lastFetchMs = now;
    CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();
    _fetchFuture = resultFuture;
    // Read configuration on the caller's server thread, before starting HTTP work.
    Map<String, String> headers = _buildApiHeaders();
    _fetchTask = Bukkit.getScheduler().runTaskAsynchronously(_plugin, () -> {
      Catalog catalog = null;

      _plugin.getLogger().info("Refreshing heads data from Minecraft-Heads API...");

      try {
        JsonObject headsData = _fetchCustomHeadsData(headers);
        JsonObject categoriesData = _fetchCustomHeadCategoriesData(headers);

        boolean headsOk = _isCustomHeadsFetchSuccessful(headsData);
        boolean categoriesOk = _isCustomHeadCategoriesFetchSuccessful(categoriesData);

        if (headsOk && categoriesOk) {
          _validateCatalog(headsData, categoriesData);
          catalog = new Catalog(headsData, categoriesData);
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (Exception e) {
        _plugin.getLogger().log(Level.SEVERE, "Failed to refresh heads data from Minecraft-Heads API.", e);
      }

      Catalog fetchedCatalog = catalog;
      if (!_plugin.isEnabled()) {
        resultFuture.complete(false);
        return;
      }
      try {
        Bukkit.getScheduler().runTask(_plugin, () -> {
          if (resultFuture.isDone()) {
            return;
          }
          if (fetchedCatalog != null) {
            _catalog = fetchedCatalog;
            _plugin.getLogger().info("Total Custom Heads Fetched: " + getTotalCustomHeads());
            _plugin.getLogger().info("Total Custom Head Categories Fetched: " + getTotalCustomHeadCategories());
            for (JsonElement warning : getCustomHeadWarnings()) {
              _plugin.getLogger().warning("Custom Heads Warning: " + warning);
            }
          }
          resultFuture.complete(fetchedCatalog != null);
        });
      } catch (IllegalStateException e) {
        // The plugin can be disabled between checking it and scheduling completion.
        resultFuture.complete(false);
      }
    });

    return resultFuture;
  }

  public synchronized void destroy() {
    if (_fetchTask != null) {
      _fetchTask.cancel();
      _fetchTask = null;
    }
    if (_fetchFuture != null) {
      _fetchFuture.complete(false);
    }
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
    return data != null && data.get(KEY_DATA) instanceof JsonArray;
  }

  private boolean _isCustomHeadCategoriesFetchSuccessful(JsonObject data) {
    if (!_isCustomHeadsFetchSuccessful(data) || !(data.get(KEY_META) instanceof JsonObject meta)) {
      return false;
    }

    return meta.has(KEY_RECORDS);
  }

  // ---------------------------
  // GET HEADS & CATEGORIES DATA
  // ---------------------------

  public JsonArray getCustomHeadsData() {
    Catalog catalog = _catalog;
    return catalog == null ? new JsonArray() : catalog.heads().getAsJsonArray(KEY_DATA);
  }

  public JsonArray getCustomHeadCategoriesData() {
    Catalog catalog = _catalog;
    return catalog == null ? new JsonArray() : catalog.categories().getAsJsonArray(KEY_DATA);
  }

  public int getTotalCustomHeads() {
    return getCustomHeadsData().size();
  }

  // ----------------------------
  // GET HEAD & CATEGORIES AMOUNT
  // ----------------------------

  public int getTotalCustomHeadCategories() {
    return getCustomHeadCategoriesData().size();
  }

  // -----------------
  // GET HEAD WARNINGS
  // -----------------

  public JsonArray getCustomHeadWarnings() {
    Catalog catalog = _catalog;
    if (catalog != null && catalog.heads().get(KEY_WARNINGS) instanceof JsonArray warnings) {
      return warnings;
    }

    return new JsonArray();
  }

  // -----------------------
  // FETCH INITIAL HEAD DATA
  // -----------------------

  private JsonObject _fetchCustomHeadsData(Map<String, String> headers) throws IOException, InterruptedException {
    String firstUrl = _buildGetCustomHeadsURL(DEMO_MODE, 1);
    JsonElement responseJSON = HttpUtils.sendGETRequest(firstUrl, REQUEST_TIMEOUT, headers);
    JsonObject responseObject = responseJSON.getAsJsonObject();
    if (!_isCustomHeadsFetchSuccessful(responseObject)) {
      throw new IOException("Heads response is missing its data array.");
    }

    if (responseObject.has(KEY_PAGINATION)) {
      JsonObject pagination = responseObject.getAsJsonObject(KEY_PAGINATION);
      int totalPages = pagination.get(KEY_PAGINATION_LAST_PAGE).getAsInt();

      for (int page = 2; page <= totalPages; page++) {
        if (Thread.currentThread().isInterrupted() || !_plugin.isEnabled()) {
          throw new InterruptedException("Heads refresh cancelled.");
        }
        String pagedUrl = _buildGetCustomHeadsURL(DEMO_MODE, page);
        JsonElement pagedResponseJSON = HttpUtils.sendGETRequest(pagedUrl, REQUEST_TIMEOUT, headers);
        JsonObject pagedResponseObject = pagedResponseJSON.getAsJsonObject();

        if (_isCustomHeadsFetchSuccessful(pagedResponseObject)) {
          responseObject.getAsJsonArray(KEY_DATA).addAll(pagedResponseObject.getAsJsonArray(KEY_DATA));
        } else {
          throw new IOException("Heads page " + page + " is missing its data array.");
        }
      }
    }

    return responseObject;
  }

  private JsonObject _fetchCustomHeadCategoriesData(Map<String, String> headers) throws IOException, InterruptedException {
    String url = _buildGetHeadCategoriesURL();
    JsonElement responseJSON = HttpUtils.sendGETRequest(url, REQUEST_TIMEOUT, headers);

    return responseJSON.getAsJsonObject();
  }

  private void _validateCatalog(JsonObject heads, JsonObject categories) throws IOException {
    for (JsonElement element : heads.getAsJsonArray(KEY_DATA)) {
      if (!(element instanceof JsonObject head) || !_isString(head.get("n"))
          || !_isString(head.get("u")) || !_isInteger(head.get("c"))) {
        throw new IOException("Heads response contains an invalid head record.");
      }
    }
    for (JsonElement element : categories.getAsJsonArray(KEY_DATA)) {
      if (!(element instanceof JsonObject category) || !_isString(category.get("n"))
          || !_isInteger(category.get("id"))) {
        throw new IOException("Categories response contains an invalid category record.");
      }
    }
  }

  private boolean _isString(JsonElement value) {
    return value instanceof JsonPrimitive primitive && primitive.isString();
  }

  private boolean _isInteger(JsonElement value) {
    if (!(value instanceof JsonPrimitive primitive) || primitive.isBoolean()) {
      return false;
    }
    try {
      primitive.getAsBigDecimal().toBigIntegerExact().intValueExact();
      primitive.getAsInt();
      return true;
    } catch (NumberFormatException | ArithmeticException e) {
      return false;
    }
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

    return Map.of(HEADER_API_KEY, apiKey);
  }

  private record Catalog(JsonObject heads, JsonObject categories) {
  }
}
