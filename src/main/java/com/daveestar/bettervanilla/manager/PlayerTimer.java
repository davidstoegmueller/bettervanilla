package com.daveestar.bettervanilla.manager;

public class PlayerTimer {
  private int _playTime;
  private int _afkTime;

  public PlayerTimer(int playTime, int afkTime) {
    _playTime = playTime;
    _afkTime = afkTime;
  }

  public void incrementPlayTime() {
    incrementPlayTime(1);
  }

  void incrementPlayTime(int seconds) {
    _playTime += seconds;
  }

  public void incrementAFKTime() {
    incrementAFKTime(1);
  }

  void incrementAFKTime(int seconds) {
    _afkTime += seconds;
  }

  public int getPlayTime() {
    return _playTime;
  }

  public int getAFKTime() {
    return _afkTime;
  }
}
