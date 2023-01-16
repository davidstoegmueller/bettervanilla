package com.daveestar.bettervanilla;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * bettervanilla java plugin
 */
public class main extends JavaPlugin {
  private static final Logger LOGGER = Logger.getLogger("bettervanilla");

  public void onEnable() {
    LOGGER.info("bettervanilla enabled");
  }

  public void onDisable() {
    LOGGER.info("bettervanilla disabled");
  }
}
