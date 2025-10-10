package com.daveestar.bettervanilla.enums;

public enum Permissions {
  ADMINHELP("bettervanilla.adminhelp"),
  PERMISSIONS("bettervanilla.permissions"),
  MAINTENANCE_BYPASS("bettervanilla.maintenance.bypass"),
  SETTINGS("bettervanilla.settings"),
  ADMINSETTINGS("bettervanilla.adminsettings"),
  VANISH("bettervanilla.vanish"),
  INVSEE("bettervanilla.invsee"),
  MODERATION("bettervanilla.moderation"),
  TIMER("bettervanilla.timer"),
  PLAYTIME("bettervanilla.playtime"),
  WAYPOINTS("bettervanilla.waypoints"),
  WAYPOINTS_ADMIN("bettervanilla.waypoints.admin"),
  DEATHPOINTS("bettervanilla.deathpoints"),
  PING("bettervanilla.ping"),
  SIT("bettervanilla.sit"),
  BACKPACK("bettervanilla.backpack"),
  MSG("bettervanilla.msg"),
  TOGGLELOCATION("bettervanilla.togglelocation"),
  TOGGLECOMPASS("bettervanilla.togglecompass"),
  CHESTSORT("bettervanilla.chestsort"),
  VEINMINER("bettervanilla.veinminer"),
  VEINCHOPPER("bettervanilla.veinchopper");

  private final String _permission;

  Permissions(String permission) {
    _permission = permission;
  }

  public String getName() {
    return _permission;
  }
}
