package dev.hackfire.aetherlux.render;

/**
 * Cheap volumetric god rays / light shafts.
 *
 * Performance approach for mobile:
 * - Classic screen-space radial blur from sun/moon
 * - Or very low-res raymarch with heavy temporal accumulation
 * - Can be fully disabled
 */
public class GodRays {

    public boolean enabled = true;
    public float intensity = 0.55f;

    public void render(float sunAngle) {
        if (!enabled) return;

        // TODO: Cheap screen-space god rays
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
