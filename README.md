# BetterVanilla ✨ SMP All-in-One

A lightweight, drop-in plugin built for cozy SMP servers with friends. BetterVanilla layers modern quality-of-life upgrades onto a vanilla Paper server-no client mods or loaders required. 👑 Core Feature: Configure every player and server toggle through the in-game `/settings` menu and [jump straight to the full settings guide](#settings) when you need the details.

[![GitHub release](https://img.shields.io/github/v/release/davidstoegmueller/bettervanilla?style=flat-round)](https://github.com/davidstoegmueller/bettervanilla/releases)
[![MIT license](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-round)](LICENSE)

Tested on Paper 1.21.10 and newer.

## Table of Contents

- [Why BetterVanilla?](#why-bettervanilla)
- [Highlights](#highlights)
- [Installation](#installation)
- [Commands](#commands)
- [Settings](#settings)
- [Permissions](#permissions)
- [Contributing](#contributing)
- [License](#license)

## Why BetterVanilla?

Your SMP deserves more than plain vanilla. BetterVanilla keeps the familiar Minecraft feel while adding carefully scoped tools that reduce admin busywork and make day-to-day play smoother. Everything ships in one polished plugin you can drop in and go.

## Highlights

### Player-Focused Features

- 🧭 **Waypoints** – Add, share, and filter public or private waypoints with GUI navigation and particle trails.
- 🎒 **Backpacks** – Carry expandable storage with configurable rows and pages.
- ⛏️ **Vein Miner & Tree Chopper** – Sneak-break to harvest entire ore veins or tree trunks instantly.
- 💀 **Death Points & Chests** – Track every death, teleport back, and safely reclaim your belongings.
- ✨ **Chest Sorting** – Right-click outside inventories to auto-organize loot.
- 🧰 **Settings Menu** – Toggle personal QoL options in an in-game UI.
- 🎨 **Colored Chat & Mentions** – Use `&` color codes and ping friends with `@name`.
- 📬 **Private Messages** – Chat directly with `/msg` and reply quickly using `/r`.
- 🪧 **Sign Colors** – Decorate signs with vibrant color codes.
- 👀 **Inventory Viewer** – Peek inside another player’s inventory when permitted.
- ⏱️ **Timer** – Global stopwatch with resume, pause, reset, and set commands.
- 🕒 **Playtime** – View detailed play history with AFK tracking for yourself or other players.
- 📶 **Ping** – Check personal or remote player latency.
- 📍 **Action-Bar Location** – Display live XYZ coordinates in your HUD.
- 🧭 **Bossbar Compass** – Minimal directional overlay in the boss bar.
- 🪑 **Sittable Stairs** – Sit on stairs with an empty hand.
- 🪑 **Sit Anywhere** – Use `/sit` to plop down on any solid block and stand with Shift.
- 🌧️ **Sleep to Clear Weather** – Skip rain and fast-forward the day by sleeping.
- 🧚 **Navigation Particles** – Follow a beam and optional trail during waypoint navigation.
- 🌾 **Crop Protection** – Prevent farmland trampling.
- 🌱 **Right-Click Harvest** – Harvest and replant crops with a single interaction.
- 📖 **Help** – Built-in `/help` overview of every command.

### Admin & Server Tools

- 🛠️ **Admin Settings Menu** – Configure every feature live from the GUI.
- 🗝️ **Permissions System** – Manage groups and users with hot-reload via `/permissions`.
- 💤 **AFK Protection** – Shield idle players from damage, movement, and collisions.
- 🧭 **Waypoint Management** – Review and edit public/private waypoints with dedicated staff tools.
- 🫥 **Vanish Mode** – Hide from players and tab lists with `/vanish`.
- 🔨 **Moderation Toolkit** – Kick, ban, mute, unmute, and unban directly in-game.
- 📣 **Server MOTD** – Edit the server list message from the admin menu.
- 🚧 **Maintenance Mode** – Toggle server access with custom messaging and bypass support.

## Installation

```text
1. Download the latest BetterVanilla release jar.
2. Drop it into your server's plugins/ folder.
3. Restart the server and you are ready to go!
```

## Commands

### Core Command

- `/settings` - Open your personal settings dashboard. Admins can enter the full server control panel from here, making it the fastest way to tweak any BetterVanilla feature. [See everything it unlocks](#settings).

### Player Utilities

- `/waypoints` - Open the waypoint GUI. Use `player <name>` or `coords <x> <y> <z>` for direct navigation.
- `/backpack` – Open your personal backpack storage.
- `/deathpoints` – Manage recent death locations and teleport back via GUI.
- `/playtime [player]` – Show detailed playtime (AFK included) for you or another player.
- `/ping [player]` – Check network latency for yourself or another player.
- `/sit` – Sit on any solid block; sneak to stand up.
- `/msg <player> <message>` – Send a private message. Alias: `/message`.
- `/r <message>` – Reply to the last private message.
- `/help` – View all available commands pulled directly from `plugin.yml`.

### Admin & Moderation

- `/adminhelp` - List key staff commands (`bettervanilla.adminhelp`).
- `/settings` - Open the settings GUI for players and admins.
- `/invsee <player>` – Inspect another player’s inventory.
- `/timer <resume|pause|reset|set>` – Control the global timer.
- `/vanish` – Toggle vanish mode with movement safeguards.
- `/kick`, `/ban`, `/unban`, `/mute`, `/unmute` – Moderation toolset with duration parsing (e.g. `1d2h30m`).
- `/permissions` – Manage groups and assignments:
  - `group addperm <group> <permission>`
  - `group removeperm <group> <permission>`
  - `group delete <group>`
  - `user addperm <player> <permission>`
  - `user removeperm <player> <permission>`
  - `user setgroup <player> <group>`
  - `assignments` – Show current group and user mapping
  - `list` – Display built-in permission nodes
  - `reload` – Reload configuration and refresh online players

## Settings

### Player Settings

- 📍 Action-bar location HUD
- 🧭 Bossbar compass overlay
- ✨ Navigation particles
- 🧹 Chest sorting toggle
- ⛏️ Vein miner toggle with per-player limits
- 🪓 Vein chopper toggle with per-player limits

### Admin Settings

- 🚧 Maintenance mode and custom message
- 💣 Creeper block damage toggle
- 🔭 End travel toggle
- 🔥 Nether travel toggle
- 🌧️ Sleeping rain skip
- 📣 Server MOTD editor
- 💤 AFK protection toggle and AFK timeout
- 🌾 Crop protection
- 🌱 Right-click harvest
- 🎒 Backpack availability, page count, and row count
- ⛏️ Vein miner global options (limits, tools, blocks, sound)
- 🪓 Vein chopper global options

## Permissions

### Built-in Groups

- `player` - default fallback that grants all player-facing QoL features such as `/settings`, waypoints, backpacks, and automation toggles.
- `moderator` - inherits the player group and adds moderation, vanish, inventory viewing, and timer controls.
- `admin` - includes every BetterVanilla permission, covering server maintenance, admin settings, and permission management.
- Use `/permissions default <group>` (e.g. `/permissions default player`) to choose which group new players fall back to. Switching the default moves existing players that still belonged to the previous default.

```
bettervanilla.adminhelp
bettervanilla.permissions
bettervanilla.maintenance.bypass
bettervanilla.settings
bettervanilla.adminsettings
bettervanilla.vanish
bettervanilla.invsee
bettervanilla.moderation
bettervanilla.timer
bettervanilla.playtime
bettervanilla.waypoints
bettervanilla.waypoints.admin
bettervanilla.deathpoints
bettervanilla.ping
bettervanilla.sit
bettervanilla.backpack
bettervanilla.msg
bettervanilla.togglelocation
bettervanilla.togglecompass
bettervanilla.chestsort
bettervanilla.veinminer
bettervanilla.veinchopper
```

## Contributing

Pull requests are welcome! If you find a bug or have an idea, open an issue with reproduction steps, logs, or screenshots so we can help quickly.

## License

Distributed under the [MIT License](LICENSE).
