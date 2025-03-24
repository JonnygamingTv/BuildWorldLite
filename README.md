# BuildWorld Plugin

**BuildWorld** is a lightweight Minecraft plugin for Paper 1.20+ that generates a flat, fully customizable creative world ideal for building. It provides teleport commands, configurable world layers, and settings to disable weather and time progression.

---

## 🌍 Features

- Custom flat world generator with configurable block layers
- Surface set at Y=70 by default (grass block on top, stone down to -60, bedrock at -61)
- No mobs, structures, caves, or ores
- Clear weather, locked daytime, and flight enabled (optional)
- Automatically generates the build world on server start
- Teleport commands to easily switch between worlds

---

## 🔧 Configuration (`config.yml`)

```yaml
world-name: build_world

clear-weather: true
lock-daylight: true
enable-flight: true
gamemode: creative

spawn-coordinates:
  x: 0
  y: 71
  z: 0

layers:
  - block: bedrock
    fromY: -61
    toY: -61
  - block: stone
    fromY: -60
    toY: 69
  - block: grass_block
    fromY: 70
    toY: 70
