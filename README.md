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

**First real sky version is in!**

- Dense procedural star field
- Large Milky Way band (needs texture)
- Smaller tilted Andromeda (needs texture)
- Bright blue-white comets with trails

### Roadmap

- [x] Repository + basic Fabric project structure
- [x] SkyRenderer design locked to target look
- [x] First custom sky implementation (stars + Milky Way + Andromeda + comets)
- [ ] Add real galaxy textures
- [ ] Improve sky gradient & atmospheric scattering
- [ ] Low-res cascaded shadow maps with soft filtering
- [ ] God rays / volumetric light shafts
- [ ] Improved moon & sun
- [ ] Config screen (ModMenu + Cloth Config)
- [ ] Performance-first presets

## Building

```bash
./gradlew build
```

The built jar will be in `build/libs/`.

### Sky textures

Drop these into `src/main/resources/assets/luxium/textures/sky/`:

- `milky_way.png` (large band)
- `andromeda.png` (smaller galaxy)

Until then, stars and comets still work.

## Contributing

This is the starting point. Issues and PRs are welcome.

---

*Luxium – light, redefined.*
