package dev.hackfire.luxium.render;

/**
 * Volumetric god rays / light shafts.
 *
 * Performance approach:
 * - Radial blur from sun/moon position in screen space (classic cheap technique)
 * - Or low-res volumetric raymarch with temporal accumulation
 * - Only enabled when looking toward the light source and quality allows
 */
public class GodRays {

    private boolean enabled = true;
    private float intensity = 0.6f;

    public void render(float sunAngle) {
        if (!enabled) return;

        // TODO: screen-space radial blur or cheap volumetric pass
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
