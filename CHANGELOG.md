**Note:** This changelog begins at version 1.2.0 as earlier changes were not tracked.

# 📜 Changelog

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
