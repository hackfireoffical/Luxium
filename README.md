# Luxium

**Stunning visuals. Near-vanilla performance.**

High-performance client-side visual mod focused on beautiful night skies while targeting **stable 60 FPS on Redmi Note 12s**.

## Target

- **Minecraft**: 1.20.1
- **Loader**: Fabric
- **Device goal**: Redmi Note 12s (Helio G88 / Mali-G52) → stable 60 FPS

## Features (current)

- Dense but cheap star field (1400 stars, static buffer)
- Large Milky Way band
- Smaller tilted Andromeda galaxy
- Bright blue-white comets (max 3)
- Completely disabled during daytime (zero cost)

## Performance Optimizations

- Only 1400 stars
- Max 3 comets
- Static `VertexBuffer` for stars (built once)
- No per-frame allocations in hot path
- Galaxies = only 2 simple textured quads
- Sky rendering fully skipped during day

## Building

### Locally
```bash
gradle build
```

### GitHub Actions
Every push to `main` automatically builds the mod for **1.20.1 Fabric**.

You can also trigger it manually:
**Actions → Build Luxium (1.20.1 Fabric) → Run workflow**

The built jar will appear in the Actions artifacts (`luxium-1.20.1`).

## Project Structure

- `SkyRenderer` – performance-first custom night sky
- Ready for low-res soft shadows and god rays next

## License

MIT

---

*Luxium – light, redefined. Built for real devices.*
