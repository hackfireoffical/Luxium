# Luxium

**Stunning visuals. Near-vanilla performance.**

Luxium is a client-side Minecraft mod that brings cinematic lighting and an otherworldly night sky without the usual shader tax.

## Vision

### Night Sky (primary focus)
Inspired by high-end shader screenshots:

- Deep navy/blue night sky (not pure black)
- Extremely dense star field
- **Large, bright Milky Way** stretching across the sky
- **Smaller Andromeda galaxy** visible as a distinct tilted spiral
- Bright glowing comets / shooting stars with long blue-white trails
- Later: nebulae, more distant galaxies, black holes

### Lighting & Shadows
- Soft god rays / volumetric light shafts
- Low-resolution soft shadows (huge performance win while still looking good)
- Improved ambient lighting

**Philosophy**: Looks like a high-end shader pack. Runs close to vanilla.

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
- [x] SkyRenderer design locked to target look (dense stars + big Milky Way + small Andromeda + bright comets)
- [ ] Implement custom sky renderer
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
