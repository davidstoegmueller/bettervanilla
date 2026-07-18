package com.daveestar.bettervanilla.manager;

import java.io.File;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.daveestar.bettervanilla.enums.Language;
import com.daveestar.bettervanilla.utils.Config;
import com.daveestar.bettervanilla.utils.Theme;

import net.md_5.bungee.api.ChatColor;

public class TranslationManager {
  private final JavaPlugin _plugin;
  private final SettingsManager _settingsManager;
  private final Map<Language, Map<String, String>> _translations = new EnumMap<>(Language.class);

  public TranslationManager(JavaPlugin plugin, SettingsManager settingsManager) {
    _plugin = plugin;
    _settingsManager = settingsManager;
    for (Language language : Language.values()) _translations.put(language, _load(language));
    _validateKeyParity();
  }

  public Language getLanguage(CommandSender sender) {
    return sender instanceof Player player ? getLanguage(player.getUniqueId()) : getServerLanguage();
  }

  public Language getLanguage(UUID playerId) {
    return Language.fromCode(_settingsManager.getPlayerLanguage(playerId));
  }

  public Language getServerLanguage() {
    return Language.fromCode(_settingsManager.getServerLanguage());
  }

  public String translate(CommandSender sender, String key, Object... replacements) {
    return translate(getLanguage(sender), key, replacements);
  }

  public String translate(Language language, String key, Object... replacements) {
    String value = _translations.getOrDefault(language, Collections.emptyMap()).get(key);
    if (value == null) value = _translations.getOrDefault(Language.EN, Collections.emptyMap()).get(key);
    if (value == null) {
      _plugin.getLogger().warning("Missing translation key '" + key + "' for " + language.getCode());
      return key;
    }
    if (replacements.length % 2 != 0) {
      throw new IllegalArgumentException("Translation replacements must be name/value pairs for key " + key);
    }
    value = _applyStyles(value);
    for (int i = 0; i + 1 < replacements.length; i += 2) {
      value = value.replace("{" + replacements[i] + "}", String.valueOf(replacements[i + 1]));
    }
    return value;
  }

  private String _applyStyles(String value) {
    return value
        .replace("{primary}", Theme.primary().toString())
        .replace("{highlight}", Theme.highlight().toString())
        .replace("{error}", Theme.error().toString())
        .replace("{text-symbol}", Theme.textSymbol().toString())
        .replace("{bold}", ChatColor.BOLD.toString())
        .replace("{reset}", ChatColor.RESET.toString());
  }

  public Map<String, String> getLanguageOptions() {
    return getLanguageOptions(null);
  }

  public Map<String, String> getLanguageOptions(CommandSender viewer) {
    Map<String, String> options = new LinkedHashMap<>();
    for (Language language : Language.values()) {
      options.put(language.getCode(), language.getDisplayName(viewer));
    }
    return options;
  }

  private Map<String, String> _load(Language language) {
    String resourceName = "translations_" + language.getCode() + ".yml";
    File translationsDirectory = new File(_plugin.getDataFolder(), "translations");
    Config translationConfig = new Config(resourceName, translationsDirectory, _plugin);
    FileConfiguration yaml = translationConfig.getFileConfig();
    Map<String, String> values = new LinkedHashMap<>();
    for (String key : yaml.getKeys(false)) {
      if (yaml.isString(key)) values.put(key, yaml.getString(key, key));
    }
    return Collections.unmodifiableMap(values);
  }

  private void _validateKeyParity() {
    Set<String> englishKeys = _translations.getOrDefault(Language.EN, Collections.emptyMap()).keySet();
    for (Language language : Language.values()) {
      Set<String> missing = new HashSet<>(englishKeys);
      missing.removeAll(_translations.getOrDefault(language, Collections.emptyMap()).keySet());
      if (!missing.isEmpty()) {
        _plugin.getLogger().warning("Language " + language.getCode() + " is missing keys: " + missing);
      }
      Set<String> extra = new HashSet<>(_translations.getOrDefault(language, Collections.emptyMap()).keySet());
      extra.removeAll(englishKeys);
      if (!extra.isEmpty()) {
        _plugin.getLogger().warning("Language " + language.getCode() + " has extra keys: " + extra);
      }
    }
  }
}
