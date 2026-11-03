# Luxium

**Stunning visuals. Near-vanilla performance.**

Luxium is a client-side Minecraft mod that brings cinematic lighting and an otherworldly night sky without the usual shader tax.

## Vision

- **Night sky**: Beautiful sun & moon, dense galaxies, nebulae, and black holes that feel alive.
- **Lighting**: Soft god rays, volumetric light shafts, and improved ambient lighting.
- **Shadows**: Low-resolution soft shadows for huge performance wins while still looking great.
- **Philosophy**: Looks like a high-end shader pack. Runs close to vanilla.

## Why a mod instead of a pure shader?

Making this a **mod** (Fabric first) gives us:
- Full control over the rendering pipeline (custom sky renderer, custom shadow maps, post-processing).
- Ability to use low-resolution shadow maps + smart upscaling/filtering that pure GLSL shader packs struggle with.
- Better integration with the game (config GUI via ModMenu, keybinds, quality presets).
- Easier future expansion.

We can still ship GLSL shaders *inside* the mod for the heavy visual lifting.

## Target

- Minecraft 1.21.1+ (Fabric)
- Client-side only
- Compatible with Sodium / Iris as much as possible
- Configurable quality presets (Performance / Balanced / Ultra)

## Current Status

🚧 **Early scaffolding** – repository is ready for development.

### Roadmap

- [x] Repository + basic Fabric project structure
- [ ] Custom sky renderer (procedural galaxies + black holes)
- [ ] Low-res cascaded shadow maps with soft filtering
- [ ] God rays / volumetric light shafts
- [ ] Improved moon & sun + atmospheric scattering
- [ ] Config screen (ModMenu + Cloth Config)
- [ ] Performance-first presets

## Building

```bash
./gradlew build
```

The built jar will be in `build/libs/`.

## Contributing

This is the starting point. Issues and PRs are welcome once the core rendering skeleton lands.

---

*Luxium – light, redefined.*
