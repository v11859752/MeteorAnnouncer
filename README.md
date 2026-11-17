# MeteorAnnouncer

A Paper 1.21+ plugin that adds a meteorite fall event with local voice warning announcements powered by **Plasmo Voice**.

## Features
* **Plasmo Voice Integration:** Streams custom `.wav` audio files directly from placed loudspeaker blocks.
* **Custom Loudspeakers:** Craftable speaker blocks with persistent coordinates, explosion protection, and piston movement support.
* **Meteorite Event:** Animated `BlockDisplay` meteorite with rotation, fire/lava particles, explosion impact, and magma crater generation.
* **Configurable:** Full control over fall speed, size, radii, delays, and explosion parameters via `config.yml`.

## Requirements
* **Server:** Paper / Purpur `1.21+`
* **Java:** `21` or higher
* **Dependency:** [Plasmo Voice](https://modrinth.com) `2.1.x` (Required)

## Crafting Recipe
Craft a Loudspeaker on a Crafting Table:
* **Corners:** Iron Ingots
* **Edges:** Redstone
* **Center:** Note Block

## Commands & Permissions

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/meteor start` | Triggers a meteorite near you and activates nearby loudspeakers | `meteorannouncer.admin` |
| `/meteor give [player]` | Gives a loudspeaker item to yourself or the specified player | `meteorannouncer.admin` |

*Default permission level: `OP`.*

## Installation
1. Drop `MeteorAnnouncer-1.0.0.jar` into your server's `plugins/` folder.
2. Install **Plasmo Voice**.
3. Restart the server to generate `plugins/MeteorAnnouncer/audio/`.
4. Place your `.wav` alert file (default: `meteor_warning.wav`) inside the `audio/` folder.
5. Edit `config.yml` as needed and restart the server.
