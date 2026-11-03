# Aetherlux

**Stunning visuals. Near-vanilla performance.**

High-performance client-side visual mod for **Minecraft 1.20.1 (Fabric)**.  
Built first for **stable 60 FPS on Redmi Note 12s** (Helio G88 / Mali-G52).

## Features

| System        | Status | Description                                      |
|---------------|--------|--------------------------------------------------|
| Night Sky     | ✅     | Dense stars, Milky Way, Andromeda, comets        |
| Sun & Moon    | ✅     | Soft sun glow + detailed moon                    |
| Shadows       | ✅     | Low-res soft shadows                             |
| Water         | ✅     | Better underwater fog + surface tint             |
| Lightning     | ✅     | Soft screen flash                                |
| God Rays      | ✅     | Cheap light shafts                               |
| Config Screen | ✅     | Mod Menu + Cloth Config + quality presets        |

## Performance Defaults (Redmi Note 12s)

- **Performance** preset enabled by default
- 1300 stars (static buffer)
- Max 2 comets
- Low-res shadows
- All effects have early-outs when disabled
- Sky effects fully skipped during daytime

## Config

1. Install **Mod Menu** and **Cloth Config**
2. Mods → **Aetherlux** → Config

Presets: `Performance` · `Balanced` · `High`

## Building

```bash
gradle build
```

GitHub Actions builds on every push → artifact `aetherlux-1.20.1`

## License

MIT

---

*Aetherlux – light from the upper sky.*
