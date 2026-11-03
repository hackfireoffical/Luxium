package dev.hackfire.luxium.render;

/**
 * Low-resolution soft shadow system.
 *
 * Strategy for near-vanilla performance:
 * - Very low resolution shadow maps (e.g. 512x512 or 1024x1024 total across cascades)
 * - Soft PCF / VSM / ESM filtering to hide low resolution
 * - Cascaded shadow maps focused on near player
 * - Aggressive culling + only update when needed
 */
public class ShadowMap {

    private int resolution = 1024; // total budget – keep low

    public void setQuality(QualityPreset preset) {
        switch (preset) {
            case PERFORMANCE -> resolution = 512;
            case BALANCED    -> resolution = 1024;
            case ULTRA       -> resolution = 2048;
        }
    }

    public void renderShadows() {
        // TODO: render low-res depth from light's POV
    }

    public enum QualityPreset {
        PERFORMANCE,
        BALANCED,
        ULTRA
    }
}
