# Aetherlux

**Stunning visuals. Near-vanilla performance.**

High-performance client-side visual mod for Minecraft 1.20.1 (Fabric).  
Target: **stable 60 FPS on Redmi Note 12s** (Helio G88 / Mali-G52).

## Focus Areas

| System              | Status     | Goal                                      |
|---------------------|------------|-------------------------------------------|
| Night Sky           | Implemented | Dense stars, big Milky Way, Andromeda, comets |
| Sun & Moon          | Scaffolded | Beautiful sun glow + detailed moon        |
| Shadows             | Scaffolded | Low-res soft shadows (512–1024)           |
| Water               | Scaffolded | Better color, simple reflections, underwater |
| Lightning           | Scaffolded | Dramatic bolts + soft screen flash        |
| God Rays            | Scaffolded | Cheap light shafts                        |

## Performance Philosophy

Everything is designed with weak mobile GPUs first:

- Low resolution techniques + smart filtering
- Features can be fully disabled
- Quality presets: **Performance** (default) / Balanced / High
- Zero cost during daytime for sky effects

## Current Features

- Custom night sky (1400 stars, Milky Way, Andromeda, comets)
- Low-res soft shadow system (ready for implementation)
- Improved sun & moon framework
- Water improvements framework
- Enhanced lightning framework
- God rays framework

## Building

```bash
gradle build
```

GitHub Actions automatically builds on every push.  
Artifact name: `aetherlux-1.20.1`

## License

MIT

---

*Aetherlux – light from the upper sky.*
