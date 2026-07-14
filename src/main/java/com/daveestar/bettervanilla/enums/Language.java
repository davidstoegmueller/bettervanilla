package com.daveestar.bettervanilla.enums;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import com.daveestar.bettervanilla.Main;

public enum Language {
  EN("language-option-english"),
  DE("language-option-german");

  private final String _translationKey;
  Language(String translationKey) { _translationKey = translationKey; }
  public String getCode() { return name().toLowerCase(); }
  public String getDisplayName() { return getDisplayName(null); }
  public String getDisplayName(CommandSender viewer) { return Main.tr(viewer, _translationKey); }
  public static Language fromCode(String code) {
    if (code == null) return EN;
    return Arrays.stream(values()).filter(language -> language.getCode().equalsIgnoreCase(code))
        .findFirst().orElse(EN);
  }
}
