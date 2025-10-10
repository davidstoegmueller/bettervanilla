**Note:** This changelog begins at version 1.2.0 as earlier changes were not tracked.

# 📜 Changelog

## [1.8.0] - xx.xx.xxxx

- **new entry** - description

## [1.7.0] - 09.10.2025

- **💬 Private Messages** – New `/msg` and `/r` commands for direct player chats.
- **🧭 Waypoint Overhaul** – Rebuilt waypoint GUI with public/private visibility, filters, cancel navigation item, owner-only editing, and an admin management permission.
- **🕒 Playtime Hub** – Playtime GUI now lists all players with AFK breakdowns and quick message summaries.
- **🧘 Sit Anywhere** – Added `/sit` to toggle sitting on the spot and refined stair seating detection.
- **🫥 Vanish Mode** – Staff can disappear from players and tab lists using `/vanish` with reliable state handling.
- **🚨 Moderation Suite** – Added `/kick`, `/ban`, `/unban`, `/mute` and `/unmute` commands with duration parsing and sanitized feedback.
- **🪧 Sign Colors** – Support Minecraft color codes on signs while preserving formatting safety.
- **🛡️ Admin UX Polish** – Sanitized MOTD/maintenance inputs, added static admin GUI footers, and replaced chat prompts with reusable in-game dialogs.
- **🧱 Paper 1.21.8 Support** – Updated to the latest Paper API build.

## [1.6.0] - 16.07.2025

- **🎒 Backpacks** – Portable storage with configurable pages and rows. Use `/backpack` anywhere.
- **⛏️ Vein Miner & Chopper** – Sneak to instantly mine entire ore veins or tree trunks. Fully customizable in the settings.
- **🗑️ Permission Group Delete** – `/permissions group delete` removes unwanted groups.
- **🚫 Maintenance Bypass Fix** – Bypass permission now works correctly.
- **🔄 Timer Sync Improvements** – Timer respects AFK time and action bar updates.
- **🪑 Chair Distance Tweak** – Sittable stairs detection refined.

## [1.5.0] - 05.07.2025

- **✨ Navigation Particles** – Follow a beam and optional trail while navigating.
- **📦 Chest Sorting** – Right-click outside a chest inventory to sort items.
- **🌾 Crop Protection** – Prevent farmland from being trampled by players or mobs.
- **🌱 Right-Click Harvest** – Harvest and replant crops with a single right-click.
- **💬 Server MOTD Setting** – Change the server MOTD directly from the admin menu.
- **🎨 Colored Chat & Mentions** – Use `&` color codes and ping players with `@name`.
- **🛠 Improved Settings GUI** – New admin submenu and clearer help messages.

## [1.4.0] - 04.07.2025

- **🆕 AFK Protection** – AFK players are now invulnerable, immovable and collision-free. Toggle with `/settings afkprotection` (disabled by default).
- **🛠 Settings GUI** – Manage toggles like maintenance or Nether access with an in-game interface.
- **🌋 Nether Toggle** – Control access to _The Nether_ via `/settings enablenether`.
- **⏱ Offline Playtime Checks** – `/playtime` now works for offline players.
- **❓ Smarter Help Command** – Automatically lists commands from `plugin.yml`.
- **💺 Sittable Stairs Tweaks** – Smoother seating orientation.
- **🧭 Waypoint Navigation Cancel** – Cancel coordinate navigation with clearer messages.
- **🔊 GUI Sound Effects** – Added subtle audio cues and async improvements.
- **🚀 Minecraft 1.21.7 Support** – Updated to the latest Paper build.
- **🗑️ Cleanup** – Removed the unused maintenance config and minor formatting fixes.
- **📚 Documentation** – README updated with new commands and features.

## [1.3.0] - 06.06.2025

- **🚀 Minecraft 1.21.5 Support** – Fully ported BetterVanilla to the latest game version.
- **📝 README Glow‑Up** – Complete overhaul with richer feature highlights & emoji flair.

## [1.2.0] - 15.02.2025

- **Complete Deathpoints Overhaul:** Rebuilt deathpoint management entirely:
  - Renamed and replaced the legacy “Last Death” system with a new “Death Points” feature.
  - Introduced a dedicated GUI for managing deathpoints.
  - Integrated DeathPointsManager and DeathPointsCommand to streamline deathpoint navigation.
  - Made the deathchest more private to be only edited and claimed by the owner.
  - Added holograms above the deathchest.
- **Waypoints GUI:**
  - Fixed options title for "rename" which was "remove" previously
  - Re-Open GUI after setting a custom icon
- **AFK-Time Setting:**
  - validate input to make sure it is an integer
- **Documentation Update:** Updated README to reflect the change from “Last Death” to “Death Points” with clear usage instructions.
- **Dependency Injection Enhancements:** Refactored commands (e.g., PermissionsCommand, PlayTimeCommand, SettingsCommand) to use constructor injection for better modularity.
- **General Refactor:** Improved overall code consistency and dependency management across the repository.
