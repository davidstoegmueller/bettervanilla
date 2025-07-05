package com.daveestar.bettervanilla.enums;

public enum ScoreboardStat {
  PLAYTIME("Playtime"),
  AFKTIME("AFK Time"),
  INGAMETIME("Day Time"),
  PLAYERKILLS("Player Kills"),
  MOBKILLS("Mob Kills"),
  DEATHS("Deaths"),
  ONLINE("Online Players"),
  TOTALDISTANCE("Travelled"),
  JUMPS("Jumps"),
  ITEMSENCHANTED("Items Enchanted"),
  FISHCAUGHT("Fish Caught"),
  DAMAGETAKEN("Damage Taken"),
  XPLEVEL("XP Level");

  private final String _displayName;

  ScoreboardStat(String displayName) {
    _displayName = displayName;
  }

  public String getDisplayName() {
    return _displayName;
  }
}
