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

- 🧭 **Waypoints** – Add, remove and navigate using a GUI (custom icons supported)
- 🎒 **Backpacks** – Portable extra storage with customizable size
- ⛏️ **Vein Miner & Chopper** – Sneak while breaking to clear entire ore veins or tree trunks
- ⚰️ **Death Points & Death Chests** – Never lose your stuff again
- 📦 **Chest Sorting** – Right‑click outside a chest inventory to instantly sort it
- 🛠️ **Settings Menu** – `/settings` player settings can be toggled from there
- 🎨 **Colored Chat & Mentions** – Use `&` color codes and ping players with `@name`
- 🔍 **Inventory Viewer** – Peek inside another player’s inventory
- ⏲️ **Timer** – Global stopwatch with resume, pause, reset & set
- ⏱️ **Playtime** – Track and display personal or global playtime, even for offline players
- 📡 **Ping** – Check your own or any player’s latency
- 📍 **Action‑Bar Location** – Live X / Y / Z in your HUD
- 🧭 **Bossbar Compass** – Minimalistic direction overlay
- 🪑 **Sittable Stairs** – Right‑click stairs with an empty hand to sit
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

| Command                            | Description                             |
| ---------------------------------- | --------------------------------------- |
| 🧭 `/waypoints` or `/wp`           | Open the waypoint GUI                   |
| ➕ `/waypoints add <name>`         | Add a waypoint at your current location |
| ➖ `/waypoints remove <name>`      | Remove an existing waypoint             |
| 📜 `/waypoints list`               | List all waypoints in the current world |
| 🚩 `/waypoints nav <name>`         | Start navigation to a waypoint          |
| 👥 `/waypoints player <player>`    | Navigate to another player's location   |
| 🎯 `/waypoints coords <x> <y> <z>` | Navigate to specific coordinates        |
| ❌ `/waypoints cancel`             | Cancel the current navigation           |

</details>

<details><summary><strong>Deathpoints ⚰️</strong></summary>

| Command                    | Description                   |
| -------------------------- | ----------------------------- |
| ⚰️ `/deathpoints` or `/dp` | Open death‑points GUI         |
| ❌ `/deathpoints cancel`   | Cancel death‑point navigation |

</details>

<details><summary><strong>Playtime ⏱️</strong></summary>

| Command                 | Description                       |
| ----------------------- | --------------------------------- |
| ⏱️ `/playtime` or `/pt` | Display your playtime             |
| ⏱️ `/playtime <player>` | Display another player's playtime |

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
<details><summary><strong>Backpack 🎒</strong></summary>

| Command                 | Description        |
| ----------------------- | ------------------ |
| 🎒 `/backpack` or `/bp` | Open your backpack |

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

---

## Permissions

```text
bettervanilla.waypoints
bettervanilla.waypoints.overwrite
bettervanilla.waypoints.remove
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
```

---

## Contributing 🤝

Pull requests are welcome! If you stumble upon a bug or have a feature idea, open an issue on the [GitHub Issues page](https://github.com/davidstoegmueller/bettervanilla/issues) with reproduction steps, logs, or screenshots.

---

## License

Distributed under the [MIT License](LICENSE).
