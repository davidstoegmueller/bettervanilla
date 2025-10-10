**Note:** This changelog begins at version 1.2.0 as earlier changes were not tracked.

# Changelog

## [1.8.0] - 10.10.2025

- 🔐 **Permission System Rework** - Centralized every command check on the `Permissions` enum, refreshed `/permissions` with better validation, tab completion, and clearer feedback, and removed redundant permission declarations from `plugin.yml`.
- 🔨 **Moderation Updates** - Consolidated moderation commands under `bettervanilla.moderation`, added self-target safeguards, and clarified player-facing notifications.
- 🧰 **Settings Menu Tweaks** - Permission-aware toggles now live entirely inside the GUI, replacing the legacy `/togglelocation` and `/togglecompass` commands.
- 📦 **Paper 1.21.10 Support** - Updated the build and plugin metadata to target the latest Paper release.

## [1.7.0] - 09.10.2025

- 💬 **Private Messages** - New `/msg` and `/r` commands enable direct player chats.
- 🧭 **Waypoint Overhaul** - Rebuilt the waypoint GUI with public/private filters, cancel navigation item, owner-only editing, and an admin review permission.
- 🕒 **Playtime Hub** - Playtime GUI lists all players with AFK breakdowns and quick access to detailed stats.
- 🪑 **Sit Anywhere** - Added `/sit` to toggle sitting on the spot and refined stair seating detection.
- 🫥 **Vanish Mode** - Staff can disappear from players and tab lists using `/vanish` with reliable state handling.
- 🔨 **Moderation Suite** - Added `/kick`, `/ban`, `/unban`, `/mute`, and `/unmute` with duration parsing and sanitized feedback.
- 🎨 **Sign Colors** - Support Minecraft color codes on signs while preserving formatting safety.
- 🧼 **Admin UX Polish** - Sanitized MOTD and maintenance inputs, added static admin GUI footers, and replaced chat prompts with reusable in-game dialogs.
- 📦 **Paper 1.21.8 Support** - Updated to the latest Paper API build.

## [1.6.0] - 16.07.2025

- 🎒 **Backpacks** - Portable storage with configurable pages and rows. Use `/backpack` anywhere.
- ⛏️ **Vein Miner and Chopper** - Sneak to instantly mine entire ore veins or tree trunks with fully customizable settings.
- 🗑️ **Permission Group Delete** - `/permissions group delete` removes unwanted groups.
- 🛠️ **Maintenance Bypass Fix** - Maintenance bypass permission now works correctly.
- ⏱️ **Timer Sync Improvements** - Timer respects AFK time and action bar updates.
- 🪑 **Chair Distance Tweak** - Sittable stairs detection refined.

## [1.5.0] - 05.07.2025

- 🧚 **Navigation Particles** - Follow a guiding beam and optional particle trail while navigating.
- 📦 **Chest Sorting** - Right-click outside a chest inventory to sort items.
- 🌾 **Crop Protection** - Prevent farmland from being trampled by players or mobs.
- 🌱 **Right-Click Harvest** - Harvest and replant crops with a single right-click.
- 📣 **Server MOTD Setting** - Change the server MOTD directly from the admin menu.
- 🎨 **Colored Chat and Mentions** - Use `&` color codes and ping players with `@name`.
- 🧰 **Improved Settings GUI** - New admin submenu and clearer help messaging.

## [1.4.0] - 04.07.2025

- 💤 **AFK Protection** - AFK players become invulnerable, immovable, and collision-free. Toggle via the settings menu.
- 🧰 **Settings GUI** - Manage maintenance, Nether access, and more with an in-game interface.
- 🔥 **Nether Toggle** - Control access to the Nether via `/settings enablenether`.
- 🕒 **Offline Playtime Checks** - `/playtime` now works for offline players.
- 📚 **Smarter Help Command** - Automatically lists commands from `plugin.yml`.
- 🪑 **Sittable Stairs Tweaks** - Smoother seating orientation.
- 🧭 **Waypoint Navigation Cancel** - Cancel coordinate navigation with clearer messages.
- 🔊 **GUI Sound Effects** - Added subtle audio cues and async improvements.
- 📦 **Minecraft 1.21.7 Support** - Updated to the latest Paper build.
- 🧹 **Cleanup** - Removed the unused maintenance config and applied formatting fixes.
- 📄 **Documentation** - README updated with new commands and features.

## [1.3.0] - 06.06.2025

- 📦 **Minecraft 1.21.5 Support** - Fully ported BetterVanilla to the latest game version.
- 📝 **README Glow-Up** - Complete documentation overhaul with richer feature highlights.

## [1.2.0] - 15.02.2025

- 💀 **Complete Deathpoints Overhaul** - Rebuilt deathpoint management entirely:
  - Renamed and replaced the legacy "Last Death" system with a new "Death Points" feature.
  - Introduced a dedicated GUI for managing deathpoints.
  - Integrated `DeathPointsManager` and `DeathPointsCommand` to streamline navigation.
  - Made death chests private so only the owner can edit or claim them.
  - Added holograms above the death chest.
- 🧭 **Waypoints GUI**
  - Fixed the options title for "rename" (was "remove" previously).
  - Reopened the GUI after setting a custom icon.
- ⏱️ **AFK-Time Setting** - Validate input to ensure it is an integer.
- 📄 **Documentation Update** - Updated README to reflect the change from "Last Death" to "Death Points" with clear usage instructions.
- 🧩 **Dependency Injection Enhancements** - Refactored commands to use constructor injection for better modularity.
- 🧹 **General Refactor** - Improved overall code consistency and dependency management.
