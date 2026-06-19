# Nova Client Mod

A production-ready client-side Minecraft mod inspired by Lunar Client and Feather Client.

## Features

### Multi-Version Support
- Minecraft 1.7.10 (Forge)
- Minecraft 1.12.2 (Forge)
- Minecraft 1.16.5 (Forge)
- Minecraft 1.20.x (Fabric)
- Minecraft 1.21.x (Fabric)
- Minecraft 26.1 (Forge)
- Minecraft 26.1.2 (Forge)

### Custom Main Menu
- Blurred animated background
- Modern dark theme with rounded buttons
- Smooth transitions and animations
- Custom logo centered
- Bottom icon bar for quick access

### ClickGUI (Lunar Client inspired)
- Categories: All, New, HUD, Server, Visual, Utility, PvP, Performance
- Mod cards with icon, name, description, enable/disable toggle, settings button
- Search support across all mods
- Drag and drop HUD positioning
- Smooth animations (fade, scale, blur, scrolling)

### 49 Mods Included

#### PvP (13 mods)
- 1.7 Visuals
- Armor Status
- Potion Effects
- Toggle Sneak
- Toggle Sprint
- Keystrokes
- CPS Display
- Reach Display
- Combo Counter
- Hitbox
- Hit Color
- TNT Countdown
- PvP Info

#### HUD (10 mods)
- Coordinates
- Direction HUD
- FPS Display
- Ping Display
- Clock Display
- Day Counter
- Memory Usage
- Server Address
- Speedometer
- Playtime

#### Visual (14 mods)
- Freelook
- Zoom
- Motion Blur
- Crosshair Editor
- Item Physics
- Glint Colorizer
- Block Outline
- Weather Changer
- Time Changer
- Particle Changer
- Fog Customizer
- 3D Skins
- 2D Items
- Shiny Pots

#### Utility (11 mods)
- Waypoints
- Saturation
- Scrollable Tooltips
- Pack Organizer
- Screenshot Uploader
- Auto Text Hotkeys
- Better Sounds
- Chunk Borders
- Item Counter
- Nick Hider
- Replay System

#### Server (5 mods)
- Hypixel Mods
- Bedwars Utilities
- Quickplay
- Skyblock Addons
- Wynncraft Support

### Account System
- Microsoft OAuth login
- Offline login support
- Multiple account management
- Account switching

### Cosmetics Engine
- Capes (5 variants)
- Wings (5 variants)
- Hats (4 variants)
- Cloaks (2 variants)
- Bandanas (2 variants)
- Animated cosmetics support
- Real-time rendering
- Player sync
- Cosmetic cache
- API support

### HUD System
- Draggable HUD elements
- HUD editor with grid
- Element visibility toggles
- Snap-to-grid positioning

### Nametag Renderer
- Custom nametag rendering
- Health display
- Armor display
- Potion effects display
- Distance display

### Configuration
- JSON-based config save/load
- Per-module settings persistence
- Account persistence
- Cosmetic cache persistence

## Architecture

```
NovaClientMod/
├── core/                          # Version-agnostic shared code
│   └── src/main/java/com/novaclient/core/
│       ├── NovaCore.java          # Main entry point
│       ├── event/                 # Event system (11 events)
│       ├── module/                # Module system + 49 mods
│       │   ├── Module.java
│       │   ├── ModuleManager.java
│       │   ├── ModuleCategory.java
│       │   ├── setting/           # Settings (5 types)
│       │   └── impl/              # Mod implementations
│       │       ├── pvp/           # 13 PvP mods
│       │       ├── hud/           # 10 HUD mods
│       │       ├── visual/        # 14 Visual mods
│       │       ├── utility/       # 11 Utility mods
│       │       └── server/        # 5 Server mods
│       ├── config/                # Config save/load
│       ├── ui/                    # GUI system
│       │   ├── ClickGuiScreen.java
│       │   ├── NovaMenuScreen.java
│       │   ├── HudEditor.java
│       │   ├── hud/               # HUD elements
│       │   └── render/            # Render utilities
│       ├── account/               # Account system
│       ├── cosmetics/             # Cosmetics engine
│       └── nametag/               # Nametag renderer
├── adapter-1_7_10/                # Forge 1.7.10 adapter
├── adapter-1_12_2/                # Forge 1.12.2 adapter
├── adapter-1_16_5/                # Forge 1.16.5 adapter
├── adapter-1_20_x/                # Fabric 1.20.x adapter
├── adapter-1_21_x/                # Fabric 1.21.x adapter
├── adapter-26_1/                  # Forge 26.1 adapter
├── adapter-26_1_2/                # Forge 26.1.2 adapter
└── assets/novaclient/             # Textures and assets
```

## Building

```bash
# Build all modules
./gradlew clean build

# Run core test
./gradlew :core:runCoreTest

# Run specific adapter (example: 1.20.x Fabric)
./gradlew :adapter-1_20_x:runClient
```

## Settings System

Each mod supports multiple setting types:
- **BooleanSetting** - On/off toggle
- **NumberSetting** - Numeric value with min/max/step
- **ModeSetting** - Dropdown selection
- **ColorSetting** - RGBA color picker with rainbow mode

## Event System

The mod uses a custom event bus with priority support:
- `GameTickEvent` - Called every game tick
- `Render2DEvent` - Called for 2D rendering
- `Render3DEvent` - Called for 3D rendering
- `KeyEvent` - Called on key press
- `PacketEvent` - Called on packet send/receive
- `ChatEvent` - Called on chat message
- `AttackEvent` - Called on entity attack
- `MotionEvent` - Called on player movement
- `WorldLoadEvent` - Called on world load
- `UpdateEvent` - Called on player update

## License

MIT License
