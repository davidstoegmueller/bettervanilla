**Note:** This changelog begins at version 1.2.0 as earlier changes were not tracked.

# Changelog

## [1.14.0] - xx.07.2026

- 📦 **Minecraft Version 26.2** - Upgraded the plugin to minecraft version 26.2

## [1.13.0] - 08.07.2026

- 📦 **Minecraft Version 26.1.2** - Upgraded the plugin to minecraft version 26.1.2

## [1.12.0] - 31.01.2026

- 🧭 **Waypoint Visibility Cycle** - Added an in-GUI visibility cycle for public/private waypoint filtering.
- 🧭 **Navigation QoL** - Player settings for auto-cancel and reach-radius navigation behavior.
- 🧺 **Inventory Sort Modes** - New per-player inventory sort mode setting with multiple sorting styles.
- 🎨 **GUI Styling Polish** - Streamlined option cycle and CustomGUI item styling.
- 🌙 **Tab List Moon Phase** - Tab list now shows the current moon phase.
- 🆘 **Help Command Redesign** - Refreshed `/help` and `/adminhelp` layouts and UX.
- 🎒 **Backpack on Death** - Improved handling when backpacks are used during death events.
- ✨ **Particle Navigation Accuracy** - Particle navigation now uses a centered location for better visuals.
- 📣 **Default MOTD Update** - Updated the default server MOTD for 1.12.0.

## [1.11.0] - 29.01.2026

- 🧠 **Heads Explorer** - New `/heads` GUI with categories, search, and admin controls (API key, enable/disable, refresh).
- 📍 **Here/Coords Command** - `/here` (alias `/coords`) broadcasts your current location to all players.
- 🏷️ **Player Tags** - Custom name tags via the settings menu with color selection and a global admin toggle.
- ⏲️ **Action-Bar Timer Toggle** - Player + admin setting to enable or disable the timer action bar overlay.
- 🛌 **Sleeping Percentage Control** - Admin setting for `playersSleepingPercentage`, plus player sleep announcements.
- 🌧️ **Sleeping Rain Fix** - Weather skip now applies globally across worlds.
- 🧭 **GUI UX Enhancements** - Search dialog support for waypoints/waypoint icons/playtime, sort cycle items, and fixed double-click handling.
- 🧰 **Admin Settings Access** - Admins can open player settings menus for other online players.
- ⚰️ **Death Points Improvements** - Sorted GUI list, multi-world fixes, duplicate deathchest prevention, and 3-line holograms.
- 🗺️ **Waypoint Data Update** - Allow duplicate private waypoint names by storing data per-UUID.
- 💬 **Chat & HUD Updates** - Mention handling refinements, compass async fix, red AFK tags, and clearer AFK messages.
- 🏷️ **Player Tags** - New tag system with configurable name + color via the settings menu.
- ✍️ **Sign Colors** - Reworked sign color handling so signs remain editable.
- ⛏️ **Vein Miner Update** - Added glowstone support.
- 🫥 **Vanish Reliability** - Fixed vanish state resolution issues.
- 📊 **Playtime GUI** - Shows each player’s online state.
- 🧾 **Recipe Sync Overhaul** - Refactored sync pipeline with Fabric/NeoForge support and an admin toggle.
- 📈 **Metrics & Build** - Added bStats and migrated build/CI tooling to Gradle with wrapper support.

## [1.10.2] - 31.10.2025

- ⛏️ **Vein Chopper Missing Blocks** - Added the missing blocks to the vein chopper blocks configuration screen. (Mangrove Roots)

## [1.10.1] - 24.10.2025

- ⛏️ **Vein Chopper Missing Blocks** - Added the missing blocks to the vein chopper blocks configuration screen. (Crimson, Warped and PaleOak)

## [1.10.0] - 24.10.2025

- 🛠️ **Crafting Recipe Controls** - Added an in-game crafting recipe manager to the admin settings, including enable/disable toggles and a 3x3 editor that writes straight to `settings.yml`.
- 🕯️ **Invisible Light Recipe** - Ships with a configurable Invisible Light recipe that defaults to torches around glowstone and saves custom ingredient layouts.
- 🖼️ **Invisible Item Frame Recipe** - Added a new crafting recipe for invisible item frames.
- ✅ **Chest Sort Fix** - Improved the chest sorting algorithm to correctly preserve item metadata, which was previously lost.

## [1.9.0] - 18.10.2025

- 📁 **Renamed Moderation Config File** - For better consistency the `moderation.yml` file was renamed to `moderations.yml`.
- 🚪 **Double Door Sync** - Syncs the state across connected double doors.
- 💣 **Creeper Protection Settings** - Extended protection system to cover both block and entity explosions.
- 🧭 **GUI Back-Navigation** - Custom GUIs now dynamically update when using the back-navigation footer item.
- ⚙️ **TPS & MSPT Stability** - Fixed rare async null-pointer exceptions in Folia task handling.
- 🧩 **Admin GUI Reorganization** - Reordered admin settings into a more logical and intuitive structure.
- 🫥 **Vanish Mode Enhancements** - Hidden from server list, join/leave messages, AFK timers, tab list, and nametags for full invisibility.
- ⚰️ **Deathchest Toggle** - Added an admin settings GUI option to enable or disable deathchests.
- 🔄 **Item Restock Automation** - Global and per-player toggles for automatically refilling empty hotbar slots with matching items.

## [1.8.0] - 10.10.2025

- 🔐 **Permission System Rework** - Centralized every command check on the `Permissions` enum, refreshed `/permissions` with better validation, tab completion, and clearer feedback, and removed redundant permission declarations from `plugin.yml`.
- 🔐 **Permission Group Presets** - Added preset groups for permissions. `player`, `moderator` & `admin` now exist as preset groups. Using `/permissions default <groupname>` a default group can be set for all players.
- 🔨 **Moderation Updates** - Consolidated moderation commands under `bettervanilla.moderation`, added self-target safeguards, and clarified player-facing notifications.
- 🧰 **Settings Menu Tweaks** - Permission-aware toggles now live entirely inside the GUI, replacing the legacy `/togglelocation` and `/togglecompass` commands.
- 🧭 **Locator Bar Toggle** - Added an admin GUI control for `/gamerule locatorBar` that syncs the rule across every world.
- 🧭 **Tab List Refactor** - Rebuilt tab list handling around a dedicated manager that refreshes entries every second and centralizes updates across AFK, vanish, and chat events.
- 🌤️ **Dynamic Header & Footer** - Added world day/time, weather, online player counts, personal playtime, ping, TPS, and MSPT to the tab list header/footer alongside quick help messaging.
- 👻 **AFK & Vanish Awareness** - Players marked AFK now receive a `[AFK]` tag with their death count, while vanished staff stay hidden without nameplate flicker.
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
