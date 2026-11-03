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
| God Rays            | ✅ Working      | Cheap light shafts                        |
| Config Screen       | ✅ Working      | Mod Menu + Cloth Config                   |

## Config

Install **Mod Menu** + **Cloth Config**.  
Then open: **Mods → Aetherlux → Config**

You can toggle every feature and choose a quality preset:
- **Performance** (default – best for Redmi Note 12s)
- **Balanced**
- **High**

## Performance Philosophy

- Low resolution techniques + smart filtering
- Features can be fully disabled
- Zero cost during daytime for sky effects

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
