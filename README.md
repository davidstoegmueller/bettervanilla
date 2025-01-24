# bettervanilla

A simple Minecraft plugin designed to enhance the vanilla gameplay experience by adding various features and commands to improve the overall player experience.

## Features

- **Waypoints**: Set, remove, and navigate to waypoints.
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

- `/settings`: List all current settings with their values/states.
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

## Installation

1. Download the plugin jar file from the [releases](https://github.com/davidstoegmueller/bettervanilla/releases) page.
2. Place the jar file in your Minecraft server's `plugins` directory.
3. Restart your server.

## Configuration

The plugin generates configuration files in the `plugins/bettervanilla` directory. You can edit these files to customize the plugin's behavior.

## Contributing

Feel free to contribute to the project by opening issues or submitting pull requests on the [GitHub repository](https://github.com/davidstoegmueller/bettervanilla).

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
