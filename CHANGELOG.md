**Note:** This changelog begins at version 1.2.0 as earlier changes were not tracked.

# 📜 Changelog

## [1.3.0] - ??.06.2025

- **🚀 Minecraft 1.21.5 Support** – Fully ported BetterVanilla to the latest game version.

- **📝 README Glow‑Up** – Complete overhaul with richer feature highlights & emoji flair.

## [1.2.0] - 15.02.2025

- **Complete Deathpoints Overhaul:** Rebuilt deathpoint management entirely:
  - Renamed and replaced the legacy “Last Death” system with a new “Death Points” feature.
  - Introduced a dedicated GUI for managing deathpoints.
  - Integrated DeathPointsManager and DeathPointsCommand to streamline deathpoint navigation.
  - Made the deathchest more private to be only edited and claimbed by the owner.
  - Added holograms above the deathchest.
- **Waypoints GUI:**
  - Fixed options title for "reaname" which was "remove" previously
  - Re-Open GUI after setting a custom icon
- **AFK-Time Setting:**
  - validate input to make sure it is an integer
- **Documentation Update:** Updated README to reflect the change from “Last Death” to “Death Points” with clear usage instructions.
- **Dependency Injection Enhancements:** Refactored commands (e.g., PermissionsCommand, PlayTimeCommand, SettingsCommand) to use constructor injection for better modularity.
- **General Refactor:** Improved overall code consistency and dependency management across the repository.
