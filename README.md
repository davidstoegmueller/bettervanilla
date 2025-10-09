# BetterVanilla – SMP All‑in‑One 🍦✨

_A lightweight, drop‑in plugin made for cozy SMP servers with friends. Upgrade vanilla with modern quality‑of‑life features—no mods, just pure fun._

[![GitHub release](https://img.shields.io/github/v/release/davidstoegmueller/bettervanilla?style=flat-round)](https://github.com/davidstoegmueller/bettervanilla/releases)
[![MIT license](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-round)](LICENSE)

---

## ✨ Why BetterVanilla?

Your SMP world with friends deserves more than plain vanilla. **BetterVanilla** sprinkles your server with just‑right upgrades—no mods, no bloat, just 🍦 + 🚀.

Whether you’re spinning up a fresh community project or running a seasoned network, BetterVanilla delivers the _wow‑factor_ that keeps players logged in and admins chilled out.

---

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Commands](#commands)
- [Permissions](#permissions)
- [Contributing](#contributing)
- [License](#license)

---

## Features

### Player‑Centric

- 🧭 **Waypoints** – Add, share (public/private) and navigate using a GUI with filters and custom icons
- 🎒 **Backpacks** – Portable extra storage with customizable size
- ⛏️ **Vein Miner & Chopper** – Sneak while breaking to clear entire ore veins or tree trunks
- ⚰️ **Death Points & Death Chests** – Never lose your stuff again
- 📦 **Chest Sorting** – Right‑click outside a chest inventory to instantly sort it
- 🛠️ **Settings Menu** – `/settings` player settings can be toggled from there
- 🎨 **Colored Chat & Mentions** – Use `&` color codes and ping players with `@name`
- 💬 **Private Messages** – Send direct messages with `/msg` and reply quickly with `/r`
- 🪧 **Sign Colors** – Write vibrant signs using in-game color codes
- 🔍 **Inventory Viewer** – Peek inside another player’s inventory
- ⏲️ **Timer** – Global stopwatch with resume, pause, reset & set
- ⏱️ **Playtime** – Track and display personal or global playtime, even for offline players
- 📡 **Ping** – Check your own or any player’s latency
- 📍 **Action‑Bar Location** – Live X / Y / Z in your HUD
- 🧭 **Bossbar Compass** – Minimalistic direction overlay
- 🪑 **Sittable Stairs** – Right‑click stairs with an empty hand to sit
- 🧘 **Sit Anywhere** – Use `/sit` on solid ground and stand up with Shift
- 🌧️ **Sleeping Rain Skip** – Sleep through bad weather
- ✨ **Navigation Particles** – Follow a beam and optional trail while navigating
- 🌾 **Crop Protection** – Prevent crops from being trampled by players or mobs
- 🌱 **Right‑Click Harvest** – Harvest and replant crops with a simple right‑click
- 📚 **Help** – Get an overview of all available features and commands

### Admin & Server

- 🛠️ **Admin Settings Menu** – `/settings` admins can access global server settings from there
- 🗝️ **Permissions System** – Group & user permissions with live add/remove and hot‑reload
- 🛡️ **AFK Protection** – AFK players become invulnerable, immovable and collision-free (configurable in the settings menu)
- 📚 **Admin Help** – Quick reference for every admin command
- 🧭 **Waypoint Management** – Staff can review and edit public/private waypoints with dedicated admin tools
- 🫥 **Vanish Mode** – Hide from players and tab lists with `/vanish`
- 🚨 **Moderation Toolkit** – Kick, ban, mute or unmute players with built-in commands
- 💬 **Server MOTD** – Change the server's MOTD from the admin settings menu

---

## Installation

```text
1. Download the latest release jar 📥
2. Drop it into your server’s `plugins/` folder 📂
3. Restart the server – done ✅
```

---

## Commands

<details><summary><strong>Waypoints 🧭</strong></summary>

| Command                            | Description                           |
| ---------------------------------- | ------------------------------------- |
| 🧭 `/waypoints` or `/wp`           | Open the waypoint GUI                 |
| 👥 `/waypoints player <player>`    | Navigate to another player's location |
| 🎯 `/waypoints coords <x> <y> <z>` | Navigate to specific coordinates      |
| ❓ `/waypoints help`               | Show available waypoint actions       |

_Swap between public/private filters, share visibility, and cancel navigation directly from the GUI tools._

</details>

<details><summary><strong>Deathpoints ⚰️</strong></summary>

| Command                    | Description           |
| -------------------------- | --------------------- |
| ⚰️ `/deathpoints` or `/dp` | Open death‑points GUI |

_Manage navigation, teleport, and cancel directly inside the GUI._

</details>

<details><summary><strong>Playtime ⏱️</strong></summary>

| Command                 | Description                       |
| ----------------------- | --------------------------------- |
| ⏱️ `/playtime` or `/pt` | Display your playtime             |
| ⏱️ `/playtime <player>` | Display another player's playtime |

_Browse a head-based GUI with every player listed; left-click to receive detailed total, active, and AFK time summaries._

</details>

<details><summary><strong>Ping 📡</strong></summary>

| Command             | Description                   |
| ------------------- | ----------------------------- |
| 📡 `/ping`          | Display your ping             |
| 📡 `/ping <player>` | Display another player's ping |

</details>

<details><summary><strong>Inventory Management 🔍</strong></summary>

| Command               | Description                          |
| --------------------- | ------------------------------------ |
| 🧳 `/invsee <player>` | View the inventory of another player |

</details>
<details><summary><strong>Private Messaging 💬</strong></summary>

| Command                      | Description                              |
| ---------------------------- | ---------------------------------------- |
| 💬 `/msg <player> <message>` | Send a private message to another player |
| 🔁 `/r <message>`            | Reply to the last private message        |

</details>
<details><summary><strong>Backpack 🎒</strong></summary>

| Command                 | Description        |
| ----------------------- | ------------------ |
| 🎒 `/backpack` or `/bp` | Open your backpack |

</details>
<details><summary><strong>Sit 🧘</strong></summary>

| Command   | Description                                      |
| --------- | ------------------------------------------------ |
| 🧘 `/sit` | Sit down on the spot; press Shift to stand again |

</details>

<details><summary><strong>Timer ⏲️</strong></summary>

| Command                | Description                      |
| ---------------------- | -------------------------------- |
| ▶️ `/timer resume`     | Resume the timer                 |
| ⏸️ `/timer pause`      | Pause the timer                  |
| 🔄 `/timer reset`      | Reset the timer                  |
| ⏲️ `/timer set <time>` | Set the timer to a specific time |

</details>

<details><summary><strong>Settings & Maintenance 🛠️</strong></summary>

| Command                  | Description             |
| ------------------------ | ----------------------- |
| 🔧 `/settings` or `/set` | Open your settings menu |

</details>

<details><summary><strong>Quality‑of‑Life Toggles 🎛️</strong></summary>

| Command                       | Description                        |
| ----------------------------- | ---------------------------------- |
| 🗺️ `/togglelocation` or `/tl` | Enable/disable action‑bar location |
| 🧭 `/togglecompass` or `/tc`  | Enable/disable bossbar compass     |

</details>
<details><summary><strong>Moderation & Staff Tools 🚨</strong></summary>

| Command                                 | Description                                           |
| --------------------------------------- | ----------------------------------------------------- |
| 🚨 `/kick <player> [reason]`            | Kick a player with an optional explanation            |
| ⛔ `/ban <player> [duration] [reason]`  | Temporarily or permanently ban a player               |
| ✅ `/unban <player>`                    | Remove a player's ban                                 |
| 🔇 `/mute <player> [duration] [reason]` | Temporarily or permanently mute chat for a player     |
| 🔊 `/unmute <player>`                   | Lift an active mute                                   |
| 🫥 `/vanish`                             | Toggle vanish mode to hide from players and tab lists |

_Duration arguments support combined `d`, `h`, `m`, and `s` suffixes (e.g. `1d2h30m`)._

</details>

<details><summary><strong>Permissions 🔑</strong></summary>

| Command                                                 | Description                             |
| ------------------------------------------------------- | --------------------------------------- |
| 🔑 `/permissions` or `/perms`                           | Permissions usage message               |
| ➕ `/permissions group addperm <group> <permission>`    | Add permission to a group               |
| ➖ `/permissions group removeperm <group> <permission>` | Remove permission from a group          |
| ❌ `/permissions group delete <group>`                  | Delete a permission group               |
| ➕ `/permissions user addperm <user> <permission>`      | Add permission to a user                |
| ➖ `/permissions user removeperm <user> <permission>`   | Remove permission from a user           |
| 🔄 `/permissions user setgroup <user> <group>`          | Set a user's group                      |
| 📋 `/permissions assignments`                           | List all group & user assignments       |
| 📋 `/permissions list`                                  | List all available permissions          |
| 🔄 `/permissions reload`                                | Reload the permissions config & reapply |

_Use `/permissions reload` to apply permission changes without requiring players to rejoin._

</details>

<details><summary><strong>Help 📚</strong></summary>

| Command         | Description                    |
| --------------- | ------------------------------ |
| 📖 `/adminhelp` | List all admin commands        |
| 📖 `/help`      | In‑game help for BetterVanilla |

</details>

## Settings

### Player Settings

- **Action-Bar Location** – Display your current XYZ in the HUD
- **Bossbar Compass** – Minimal compass overlay
- **Navigation Particles** – Show particles while navigating
- **Chest Sorting** – Right-click outside inventories to sort them
- **Vein Miner** – Mine entire ore veins when sneaking with a pickaxe
- **Vein Chopper** – Chop entire trees when sneaking with an axe

### Admin Settings

- **Maintenance Mode** – Toggle join restrictions with an optional message
- **Creeper Damage** – Prevent creepers from destroying blocks
- **Enable End** – Allow travel to The End dimension
- **Enable Nether** – Allow travel to The Nether dimension
- **Sleeping Rain Skip** – Skip rain by sleeping
- **Server MOTD** – Set the message shown in the server list
- **AFK Protection** – Make AFK players invulnerable
- **AFK Time** – Minutes before a player is marked AFK
- **Crop Protection** – Stop crops from being trampled
- **Right-Click Crop Harvest** – Harvest crops by right-clicking
- **Backpacks** – Enable backpacks and adjust pages and rows
- **Vein Miner Settings** – Global toggle, max size, sound and allowed tools/blocks
- **Vein Chopper Settings** – Global toggle, max size, sound and allowed tools/blocks

---

## Permissions

```
bettervanilla.waypoints
bettervanilla.waypoints.admin
bettervanilla.maintenance.bypass
bettervanilla.playtime
bettervanilla.ping
bettervanilla.invsee
bettervanilla.timer
bettervanilla.adminhelp
bettervanilla.settings
bettervanilla.adminsettings
bettervanilla.togglelocation
bettervanilla.togglecompass
bettervanilla.chestsort
bettervanilla.deathpoints
bettervanilla.backpack
bettervanilla.veinminer
bettervanilla.veinchopper
bettervanilla.permissions
bettervanilla.msg
bettervanilla.vanish
bettervanilla.kick
bettervanilla.ban
bettervanilla.unban
bettervanilla.mute
bettervanilla.unmute
```

---

## Contributing 🤝

Pull requests are welcome! If you stumble upon a bug or have a feature idea, open an issue on the [GitHub Issues page](https://github.com/davidstoegmueller/bettervanilla/issues) with reproduction steps, logs, or screenshots.

---

## License

Distributed under the [MIT License](LICENSE).
