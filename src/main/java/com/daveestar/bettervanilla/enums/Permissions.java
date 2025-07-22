package com.daveestar.bettervanilla.enums;

public enum Permissions {
  WAYPOINTS("bettervanilla.waypoints"),
  WAYPOINTS_OVERWRITE("bettervanilla.waypoints.overwrite"),
  WAYPOINTS_REMOVE("bettervanilla.waypoints.remove"),
  MAINTENANCE_BYPASS("bettervanilla.maintenance.bypass"),
  PLAYTIME("bettervanilla.playtime"),
  PING("bettervanilla.ping"),
  INVSEE("bettervanilla.invsee"),
  TIMER("bettervanilla.timer"),
  ADMINHELP("bettervanilla.adminhelp"),
  SETTINGS("bettervanilla.settings"),
  ADMINSETTINGS("bettervanilla.adminsettings"),
  TOGGLELOCATION("bettervanilla.togglelocation"),
  TOGGLECOMPASS("bettervanilla.togglecompass"),
  CHESTSORT("bettervanilla.chestsort"),
  LASTDEATH("bettervanilla.deathpoints"),
  PERMISSION("bettervanilla.permissions"),
  VEINMINER("bettervanilla.veinminer"),
  VEINCHOPPER("bettervanilla.veinchopper"),
  MSG("bettervanilla.msg"),
  VANISH("bettervanilla.vanish");

  private final String _permission;

  Permissions(String permission) {
    _permission = permission;
  }

  public String getName() {
    return _permission;
  }
}
