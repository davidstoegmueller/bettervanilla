# BetterVanilla – SMP All‑in‑One 🍦✨

_A lightweight, drop‑in plugin that upgrades vanilla Minecraft servers with modern quality‑of‑life features—without sacrificing the classic vibe._

[![GitHub release](https://img.shields.io/github/v/release/davidstoegmueller/bettervanilla?style=flat-round)](https://github.com/davidstoegmueller/bettervanilla/releases)
[![MIT license](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-round)](LICENSE)

---

## ✨ Why BetterVanilla?

Your SMP world deserves more than plain vanilla. **BetterVanilla** sprinkles your server with just‑right upgrades—no mods, no bloat, just 🍦 + 🚀.

Whether you’re spinning up a fresh community project or running a seasoned network, BetterVanilla delivers the _wow‑factor_ that keeps players logged in and admins chilled out.

### Key Highlights

- 🧭 **Smart Navigation** – Waypoints with an intuitive GUI, a subtle bossbar compass, and live action‑bar coordinates keep everyone headed in the right direction—no external maps required.
- 🛋️ **Seamless QoL Tweaks** – Sit on any stair, track playtime & ping, auto‑skip rainy days, and bounce back from death with instant chests. Zero learning curve—just play.
- ⚙️ **Admin Superpowers** – One‑command maintenance mode, creeper‑damage toggles, AFK timers, and a live‑reload permission system let you craft the perfect experience in seconds.
- 🔄 **Instant Recovery** – Death points & automatic death chests mean lost gear is just a quick stroll away.
- 🌦️ **Weather? What Weather?** – Skip rain and storms by simply hopping into bed—no one likes soggy boots.
- 🪑 **Immersive Extras** – Sittable stairs, action‑bar coordinates, and subtle HUD touches make your world feel alive.

_(Scroll down for the complete feature & command list.)_

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
- ⏱️ **Playtime** – Track and display personal or global playtime
- 📡 **Ping** – Check your own or any player’s latency
- 🔍 **Inventory Viewer** – Peek inside another player’s inventory
- ⏲️ **Timer** – Global stopwatch with resume, pause, reset & set
- 📍 **Action‑Bar Location** – Live X / Y / Z in your HUD
- 🧭 **Bossbar Compass** – Minimalistic direction overlay
- ⚰️ **Death Points & Death Chests** – Never lose your stuff again
- 🪑 **Sittable Stairs** – Right‑click stairs with an empty hand to sit
- 🌧️ **Sleeping Rain Skip** – Sleep through bad weather
- 📚 **Help** – Get an overview of all available features and commands

### Admin & Server

- 🛠️ **Settings Command** – Toggle maintenance mode, creeper damage, _The End_ access, sleeping‑rain, AFK time, and more
- 🗝️ **Permissions System** – Group & user permissions with live add/remove and hot‑reload
- 📚 **Admin Help** – Quick reference for every admin command

---

## Installation

```text
1. Download the latest release jar 📥
2. Drop it into your server’s `plugins/` folder 📂
3. Restart or reload the server – done ✅
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

<details><summary><strong>Timer ⏲️</strong></summary>

| Command                | Description                      |
| ---------------------- | -------------------------------- |
| ▶️ `/timer resume`     | Resume the timer                 |
| ⏸️ `/timer pause`      | Pause the timer                  |
| 🔄 `/timer reset`      | Reset the timer                  |
| ⏲️ `/timer set <time>` | Set the timer to a specific time |

</details>

<details><summary><strong>Settings & Maintenance 🛠️</strong></summary>

| Command                              | Description                                          |
| ------------------------------------ | ---------------------------------------------------- |
| 🔧 `/settings` or `/set`             | List all settings with their current values          |
| 🚧 `/settings maintenance [message]` | Toggle maintenance mode (plus optional kick message) |
| 💥 `/settings creeperdamage`         | Toggle creeper block/entity damage                   |
| 🏁 `/settings toggleend`             | Enable/disable entry to _The End_                    |
| 🌧️ `/settings sleepingrain`          | Enable/disable sleeping to skip rain                 |
| 💤 `/settings afktime <minutes>`     | Minutes until a player is marked AFK                 |

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
| ➕ `/permissions user addperm <user> <permission>`      | Add permission to a user                |
| ➖ `/permissions user removeperm <user> <permission>`   | Remove permission from a user           |
| 🔄 `/permissions user setgroup <user> <group>`          | Set a user's group                      |
| 📋 `/permissions assignments`                           | List all group & user assignments       |
| 📋 `/permissions list`                                  | List every permission assignment        |
| 🔄 `/permissions reload`                                | Reload the permissions config & reapply |

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
bettervanilla.togglelocation
bettervanilla.togglecompass
bettervanilla.deathpoints
bettervanilla.permissions
```

---

## Contributing 🤝

Pull requests are welcome! If you stumble upon a bug or have a feature idea, open an issue on the [GitHub Issues page](https://github.com/davidstoegmueller/bettervanilla/issues) with reproduction steps, logs, or screenshots.

---

## License

Distributed under the [MIT License](LICENSE).
