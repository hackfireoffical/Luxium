# Luxium

**Stunning visuals. Near-vanilla performance.**

Luxium is a client-side Minecraft mod that brings cinematic lighting and an otherworldly night sky without the usual shader tax.

## Performance Target

**Primary device: Redmi Note 12s (Helio G88 / Mali-G52)**  
**Goal: Stable 60 FPS**

All visual features are designed and tuned with this weak mobile GPU in mind first.

### Current performance choices:
- Only **1600 stars** (static VertexBuffer, built once)
- Maximum **3 comets** at once
- Galaxies = only 2 simple textured quads
- No per-frame allocations in the hot path
- Custom sky only active at night
- Extremely cheap star + comet rendering

## Vision

### Night Sky
- Deep navy/blue night sky
- Dense but performance-friendly star field
- **Large bright Milky Way**
- **Smaller tilted Andromeda**
- Bright blue-white comets with clean trails

### Lighting & Shadows (planned)
- Soft god rays
- Very low-resolution soft shadows (biggest performance win)

## Current Status

**First optimized sky version is live.**

- Dense procedural stars (1600)
- Large Milky Way band (needs texture)
- Smaller Andromeda (needs texture)
- Bright comets (max 3)
- Heavily optimized for Redmi Note 12s

### Roadmap

- [x] Repository + Fabric project
- [x] Performance-first sky renderer (stars + Milky Way + Andromeda + comets)
- [ ] Add real galaxy textures
- [ ] Better sky gradient
- [ ] Low-res soft shadows
- [ ] God rays (cheap version)
- [ ] Config screen with quality presets (Performance / Balanced)

## Building

```bash
./gradlew build
```

### Sky textures
Put these in `src/main/resources/assets/luxium/textures/sky/`:

- `milky_way.png`
- `andromeda.png`

## License

MIT

---

*Luxium – light, redefined. Built for 60 FPS on real devices.*
