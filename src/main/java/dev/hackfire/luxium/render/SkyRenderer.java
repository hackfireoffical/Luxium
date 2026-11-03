package dev.hackfire.luxium.render;

/**
 * Custom sky renderer for Luxium.
 *
 * TARGET LOOK (reference: deep blue night with dense stars, bright comets,
 * large Milky Way band, smaller Andromeda galaxy):
 *
 * - Deep navy/blue gradient sky (not pure black)
 * - Extremely dense star field (thousands of stars, many tiny ones)
 * - Bright, glowing comets / shooting stars with long blue-white trails
 * - Large, prominent Milky Way (bright, detailed band across the sky)
 * - Smaller Andromeda galaxy (visible as a distinct tilted oval/spiral)
 * - Optional: faint other galaxies, nebulae, black holes later
 *
 * Performance strategy:
 * - Procedural stars where possible + a few high-quality textures for galaxies
 * - Comets as simple particle-like billboards / lines (very cheap)
 * - Galaxies as large textured quads or low-cost skybox layers
 * - Everything fades with light pollution / weather / dimension
 */
public class SkyRenderer {

    // === Visual layers (render order) ===
    // 1. Sky gradient (deep blue)
    // 2. Distant galaxies (Andromeda small, others faint)
    // 3. Milky Way (large, bright band)
    // 4. Dense star field (procedural + twinkle)
    // 5. Comets / meteors (bright trails)
    // 6. Moon / Sun (with atmospheric scattering)

    private boolean showMilkyWay = true;
    private boolean showAndromeda = true;
    private boolean showComets = true;
    private float starDensity = 1.0f;      // 0.0 - 2.0
    private float milkyWayBrightness = 1.0f;
    private float cometFrequency = 0.8f;  // how often new comets spawn

    public void render(float tickDelta, float alpha) {
        // Called every frame from a mixin into WorldRenderer / DimensionEffects

        // TODO implementation order:
        // 1. Sky color gradient (deep blue night)
        // 2. Render large Milky Way texture/band
        // 3. Render smaller Andromeda galaxy (tilted, offset)
        // 4. Dense procedural star field
        // 5. Spawn & render bright comets with glowing trails
    }

    public void reload() {
        // Called on resource reload – load galaxy textures, etc.
    }

    // === Config setters (will be driven by Cloth Config later) ===

    public void setShowMilkyWay(boolean show) { this.showMilkyWay = show; }
    public void setShowAndromeda(boolean show) { this.showAndromeda = show; }
    public void setShowComets(boolean show) { this.showComets = show; }
    public void setStarDensity(float density) { this.starDensity = density; }
    public void setMilkyWayBrightness(float brightness) { this.milkyWayBrightness = brightness; }
    public void setCometFrequency(float frequency) { this.cometFrequency = frequency; }
}
