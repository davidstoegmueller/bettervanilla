package com.daveestar.bettervanilla.enums;

public enum ScoreboardStat {
  PLAYTIME("Playtime"),
  AFKTIME("AFK Time"),
  INGAMETIME("In-Game Time"),
  PLAYERKILLS("Player Kills"),
  MOBKILLS("Mob Kills"),
  DEATHS("Deaths"),
  ONLINE("Online Players"),
  SWIMDISTANCE("Swim Distance"),
  WALKDISTANCE("Walk Distance"),
  TOTALDISTANCE("Total Distance");

  private final String _displayName;

  ScoreboardStat(String displayName) {
    _displayName = displayName;
  }

  public String getDisplayName() {
    return _displayName;
  }
}
