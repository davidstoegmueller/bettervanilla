package com.daveestar.bettervanilla.manager;

public class PlayerTimer {
  private int _playTime;
  private int _afkTime;

  public PlayerTimer(int playTime, int afkTime) {
    _playTime = playTime;
    _afkTime = afkTime;
  }

  public void incrementPlayTime() {
    _playTime++;
  }

  public void incrementAFKTime() {
    _afkTime++;
  }

  public int getPlayTime() {
    return _playTime;
  }

  public int getAFKTime() {
    return _afkTime;
  }
}
