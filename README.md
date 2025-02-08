# BetterVanilla AIO

bettervanilla is a simple yet powerful Minecraft plugin designed to enhance the vanilla gameplay experience on SMP servers while maintaining the classic feel of Minecraft.

## Key Enhancements

- **Intuitive Navigation:**  
  Interactive waypoints and the bossbar compass guide you effortlessly through your world.

- **Enhanced Quality of Life:**

  - A range of intuitive features that streamline navigation and interaction
  - Integrated tools offering natural orientation and ease of use
  - Comprehensive enhancements designed to improve the overall gameplay experience for everyone

- **Admin Features:**  
  Robust command functionalities and configuration settings streamline server management and gameplay control.

## Designed for Vanilla SMP Servers

bettervanilla enriches your server by:

- Preserving the authentic charm of vanilla Minecraft
- Providing customizable, quality-of-life enhancements
- Enabling precise permission management for improved security and control

## Why Choose bettervanilla?

By integrating both player-friendly features and administrative tools, bettervanilla offers:

- A balance between modern enhancements and a classic gaming experience
- An easy-to-use solution that adds depth and interactivity to your server
- Improved enjoyment and efficient server management for both players and admins

Upgrade your SMP server with bettervanilla for a richer, more immersive gameplay experience.

## Features

- **Waypoints**: Add, remove, and navigate to waypoints withh GUI. Custom icons for waypoints can be set in the GUI.
- **Playtime**: Track and display playtime for players.
- **Ping**: Check the ping of yourself or other players.
- **Inventory Viewer**: View the inventory of other players.
- **Timer**: Manage a global timer with resume, pause, and reset functionalities.
- **Admin Help**: Get help for admin commands.
- **Settings**: Toggle various global settings.
- **Action Bar Location**: Display your current location in the action bar.
- **Bossbar Compass**: Display directions as a compass in the bossbar.
- **Last Death**: Navigate to your last death point.
- **Sittable Stairs**: Sit on stairs using an empty hand.
- **Sleeping Rain**: Skip rainy days by sleeping in a bed.
- **Death Chest**: Create a chest with your items at your death location.
- **Permissions**: Manage and assign plugin permissions to users and groups.

## Commands

### Waypoints

- `/waypoints` or `/wp`: Open the waypoints GUI.
- `/waypoints add <name>`: Add a waypoint at your current location.
- `/waypoints remove <name>`: Remove an existing waypoint.
- `/waypoints list`: List all waypoints in the current world.
- `/waypoints nav <name>`: Start navigation to a specified waypoint.
- `/waypoints player <player>`: Navigate to another player's location.
- `/waypoints coords <x> <y> <z>`: Navigate to specific coordinates.
- `/waypoints cancel`: Cancel the current navigation.

### Playtime

- `/playtime` or `/pt`: Display your playtime.
- `/playtime <player>`: Display the playtime of another player.

### Ping

- `/ping`: Display your ping.
- `/ping <player>`: Display the ping of another player.

### Inventory Viewer

- `/invsee <player>`: View the inventory of another player.

### Timer

- `/timer resume`: Resume the timer.
- `/timer pause`: Pause the timer.
- `/timer reset`: Reset the timer.
- `/timer set <time>`: Set the timer to a specific time.

### Admin Help

- `/adminhelp`: Get help for admin commands.

### Settings

- `/settings` or `/set`: List all current settings with their values/states.
- `/settings maintenance [message]`: Toggle maintenance mode and set a message.
- `/settings creeperdamage`: Toggle creeper entity damage.
- `/settings toggleend`: Toggle 'the end' entry.
- `/settings sleepingrain`: Toggle sleep during rain.
- `/settings afktime <minutes>`: Set the time in minutes until a player is marked as AFK.

### Action Bar Location

- `/togglelocation` or `/tl`: Turn on/off action bar location.

### Bossbar Compass

- `/togglecompass` or `/tc`: Turn on/off bossbar compass.

### Last Death

- `/lastdeath` or `/ld`: Navigate to the last death point.
- `/lastdeath cancel`: Cancel the death point navigation.

### Help

- `/help`: Get help for better vanilla.

### Permissions

- `/permissions` or `/perms`: Access a permissions usage message.
- `/permissions group addperm <group> <permission>`: Add a permission to a group.
- `/permissions group removeperm <group> <permission>`: Remove a permission from a group.
- `/permissions user addperm <username> <permission>`: Add a permission to a user.
- `/permissions user removeperm <username> <permission>`: Remove a permission from a user.
- `/permissions user setgroup <username> <group>`: Set the user's group.
- `/permissions assignments`: List all assignments made to groups and users.
- `/permissions list`: List all group and user permission assignments.
- `/permissions reload`: Reload the permissions config and reapply all the defined permissions to all players.

## Installation

1. Download the plugin jar file from the [releases](https://github.com/davidstoegmueller/bettervanilla/releases) page.
2. Place the jar file in your Minecraft server's `plugins` directory.
3. Restart your server.

## Configuration

The plugin generates configuration files in the `plugins/bettervanilla` directory. You can edit these files to customize the plugin's behavior.

## Permissions

- `bettervanilla.waypoints`
- `bettervanilla.waypoints.overwrite`
- `bettervanilla.waypoints.remove`
- `bettervanilla.maintenance.bypass`
- `bettervanilla.playtime`
- `bettervanilla.ping`
- `bettervanilla.invsee`
- `bettervanilla.timer`
- `bettervanilla.adminhelp`
- `bettervanilla.settings`
- `bettervanilla.togglelocation`
- `bettervanilla.togglecompass`
- `bettervanilla.lastdeath`
- `bettervanilla.permissions`

## Contributing

Feel free to contribute to the project by opening issues or submitting pull requests on the [GitHub repository](https://github.com/davidstoegmueller/bettervanilla).

## GitHub Issues

For tracking and managing issues, please visit the repository's [Issues](https://github.com/davidstoegmueller/bettervanilla/issues) page. When creating a new issue, include a clear description of the problem, steps to reproduce, and any related logs or screenshots. Your detailed feedback helps us improve the project!

## License

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).
