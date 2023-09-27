# Conquest Minecraft Plugin for Spigot

This plugin brings a captivating conquest experience to Minecraft, allowing players to capture towns and villages for their kingdom, interact with custom NPCs and monsters, and visualize their conquests via the Dynmap plugin.

## Features
- **Conquest System**: Players can capture towns and villages for their kingdom.
- **Dynmap Integration**: Visualize conquests directly on the website.
- **Player & Kingdom Rewards**: Gain money and resources as rewards for capturing locations.
- **Custom NPCs & Monsters**: Enrich the gameplay with unique non-player characters.
- **Interactive Menus**: Use ChestGUIs to navigate various options.
- **Scoreboards**: View stats on various scoreboards such as CaptureBoard, TrapBoard, KingdomBoard, and NeutralBoard.

## Technical Details
- **Configuration**: Managed through YAML.
- **Database**: Uses SQLite for data storage.
- **Permission System**: Comprehensive system for managing user permissions.
- **Chat Interact System**: Allows for creating and editing string values.
- **Event Management**: Uses event handlers and listeners.
- **Asynchronous Systems**: Improve performance and responsiveness.
- **String Parsing & Multi World Management**: Ensure compatibility and enhanced gameplay.

## Admin Side
Administrators with the right permissions can:
- Create, edit, and delete kingdoms, towns, and villages.
- Set up kits and modify specific values such as teleportation placements.

## Version History

| Version | Name                     | ETA        | Description |
| ------- | ------------------------ | ---------- | ----------- |
| 0.1     | Closed Beta              | 2016-06-11 | Basics such as capture system, permissions, and command structures. Also includes the first iteration of the kingdom system (Join/Leave). |
| 0.2     | Pre-Beta                 | 2016-06-20 | Added teleportation, capture scoreboard and respawn system. |
| 0.3     | Dynmap Update            | 2016-08-03 | Integrated the Dynmap plugin into the project. |
| 0.4     | ChestGui Update          | 2016-11-22 | Introduced YAML implementation, Chest GUI with basic pagination, Alphabet GUI, basic events, and implemented first GUIs. Also, restructured the code. |
| 0.5     | Firework Update          | 2016-11-23 | Added fireworks feature. |
| 0.6     | Economy Update           | 2016-12-01 | Introduced permissions, player rewards, kingdom rewards, and mob spawning. |
| 0.7     | Kit Update               | 2017-06-14 | Introduced chat interact system, kit system, and kit reward system. |
| 0.8     | Kingdom Hierarchy Update | TBD        | Implement player hierarchy within a kingdom, town, and village. |
| 0.9     | Custom Spawner Update    | TBD        | Added custom spawners with custom mobs and related GUIs. |
| 0.10    | Worlds Update            | TBD        | Introduce multiple worlds accessibility feature. |
| 0.11    | Events Update            | TBD        | Introduce custom events for all players. |
| 1.0     | Launch Update            | TBD        | Introduce world regrow feature, enabling mining as worlds regrow over time. |
