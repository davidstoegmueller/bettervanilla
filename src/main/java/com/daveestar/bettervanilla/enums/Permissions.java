package com.daveestar.bettervanilla.enums;

public enum Permissions {
  ADMINHELP("bettervanilla.adminhelp", Category.MODERATOR),
  PERMISSIONS("bettervanilla.permissions", Category.ADMIN),
  MAINTENANCE_BYPASS("bettervanilla.maintenance.bypass", Category.ADMIN),
  SETTINGS("bettervanilla.settings", Category.PLAYER),
  ADMINSETTINGS("bettervanilla.adminsettings", Category.ADMIN),
  VANISH("bettervanilla.vanish", Category.MODERATOR),
  INVSEE("bettervanilla.invsee", Category.MODERATOR),
  MODERATION("bettervanilla.moderation", Category.MODERATOR),
  TIMER("bettervanilla.timer", Category.MODERATOR),
  PLAYTIME("bettervanilla.playtime", Category.PLAYER),
  WAYPOINTS("bettervanilla.waypoints", Category.PLAYER),
  WAYPOINTS_ADMIN("bettervanilla.waypoints.admin", Category.MODERATOR),
  DEATHPOINTS("bettervanilla.deathpoints", Category.PLAYER),
  PING("bettervanilla.ping", Category.PLAYER),
  SIT("bettervanilla.sit", Category.PLAYER),
  BACKPACK("bettervanilla.backpack", Category.PLAYER),
  MSG("bettervanilla.msg", Category.PLAYER),
  TOGGLELOCATION("bettervanilla.togglelocation", Category.PLAYER),
  TOGGLECOMPASS("bettervanilla.togglecompass", Category.PLAYER),
  CHESTSORT("bettervanilla.chestsort", Category.PLAYER),
  DOUBLE_DOOR("bettervanilla.doubledoor", Category.PLAYER),
  VEINMINER("bettervanilla.veinminer", Category.PLAYER),
  VEINCHOPPER("bettervanilla.veinchopper", Category.PLAYER),
  ITEM_RESTOCK("bettervanilla.itemrestock", Category.PLAYER);

  public enum Category {
    PLAYER,
    MODERATOR,
    ADMIN;
  }

  private final String _permission;
  private final Category _category;

  Permissions(String permission, Category category) {
    _permission = permission;
    _category = category;
  }

  public String getName() {
    return _permission;
  }

  public Category getCategory() {
    return _category;
  }
}
