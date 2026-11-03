package dev.hackfire.aetherlux.render;

import net.minecraft.client.util.math.MatrixStack;

/**
 * Improved Sun and Moon rendering.
 *
 * Goals (while staying cheap):
 * - More beautiful sun (soft glow / corona)
 * - Detailed moon with phases
 * - Subtle atmospheric scattering around them
 * - Works with the custom sky
 */
public class CelestialRenderer {

    public boolean improvedSun = true;
    public boolean improvedMoon = true;
    public float sunGlowIntensity = 0.7f;
    public float moonBrightness = 1.1f;

    public void renderSun(MatrixStack matrices, float tickDelta, float alpha) {
        if (!improvedSun) return;

        // TODO:
        // - Soft glow / halo around the sun
        // - Slight color temperature change at sunrise/sunset
        // - Very cheap screen-space glow
    }

    public void renderMoon(MatrixStack matrices, float tickDelta, float alpha, int phase) {
        if (!improvedMoon) return;

        // TODO:
        // - Better moon texture / shading
        // - Soft glow
        // - Correct phase rendering
    }

    public void renderAtmosphericScattering(MatrixStack matrices, float sunAngle) {
        // Cheap approximation of scattering around sun/moon
        // Important for nice sunsets without heavy cost
    }
}
