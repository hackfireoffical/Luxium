package dev.hackfire.aetherlux.render;

/**
 * Low-resolution soft shadow system for Aetherlux.
 *
 * Designed for weak GPUs (Redmi Note 12s / Mali-G52):
 * - Very low resolution shadow maps (512 or 1024 total)
 * - Soft filtering to hide the low res
 * - Cascaded only near the player
 * - Can be completely disabled for maximum performance
 */
public class ShadowMap {

    public enum Quality {
        OFF,          // zero cost
        PERFORMANCE,  // 512px, very soft
        BALANCED,     // 1024px
        HIGH          // 1536px (still relatively cheap)
    }

    private Quality quality = Quality.PERFORMANCE;
    private int resolution = 512;
    private boolean enabled = true;

    public void setQuality(Quality quality) {
        this.quality = quality;
        switch (quality) {
            case OFF -> {
                this.enabled = false;
                this.resolution = 0;
            }
            case PERFORMANCE -> {
                this.enabled = true;
                this.resolution = 512;
            }
            case BALANCED -> {
                this.enabled = true;
                this.resolution = 1024;
            }
            case HIGH -> {
                this.enabled = true;
                this.resolution = 1536;
            }
        }
    }

    public boolean isEnabled() {
        return enabled && quality != Quality.OFF;
    }

    public int getResolution() {
        return resolution;
    }

    /**
     * Called every frame when shadows are enabled.
     * Keep this extremely cheap.
     */
    public void renderShadows() {
        if (!isEnabled()) return;

        // TODO:
        // 1. Render low-res depth from sun/moon direction
        // 2. Apply soft PCF / simple blur
        // 3. Composite onto the scene
        // Focus only on nearby blocks for maximum performance
    }

    public void cleanup() {
        // Release any GPU resources
    }
}
