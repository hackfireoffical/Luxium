# Aetherlux

**Stunning visuals. Near-vanilla performance.**

High-performance client-side visual mod for Minecraft 1.20.1 (Fabric).  
Target: **stable 60 FPS on Redmi Note 12s**.

## Focus Areas

| System              | Status          | Goal                                      |
|---------------------|-----------------|-------------------------------------------|
| Night Sky           | ✅ Working      | Dense stars, big Milky Way, Andromeda, comets |
| Sun & Moon          | ✅ Working      | Soft sun glow + detailed moon             |
| Shadows             | ✅ Working      | Low-res soft shadows                      |
| Water               | ✅ Working      | Better underwater fog + surface tint      |
| Lightning           | ✅ Working      | Soft screen flash + storm ambient         |
| God Rays            | Scaffolded      | Cheap light shafts                        |

## Performance Philosophy

Everything is designed with weak mobile GPUs first:

- Low resolution techniques + smart filtering
- Features can be fully disabled
- Quality presets: **Performance** (default) / Balanced / High
- Zero cost during daytime for sky effects

## Current Features

- Custom night sky (1400 stars, Milky Way, Andromeda, comets)
- Improved Sun with soft glow
- Improved Moon with phases + glow
- Low-res soft shadow system
- Better underwater fog & water tint
- Lightning screen flash + ambient storm flashes
- God rays framework ready

## Building

```bash
gradle build
```

GitHub Actions builds automatically on every push.  
Artifact: `aetherlux-1.20.1`

## License

MIT

---

*Aetherlux – light from the upper sky.*
