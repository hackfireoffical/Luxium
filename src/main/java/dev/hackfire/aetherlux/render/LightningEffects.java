package dev.hackfire.aetherlux.render;

/**
 * Improved lightning visuals.
 *
 * Goals:
 * - Brighter, more dramatic lightning bolts
 * - Soft screen flash
 * - Optional god-ray style light shafts during strikes (very cheap)
 * - Still extremely lightweight
 */
public class LightningEffects {

    public boolean enabled = true;
    public boolean screenFlash = true;
    public boolean enhancedBolts = true;

    private float flashIntensity = 0.0f;

    public void onLightningStrike() {
        if (!enabled) return;

        // Trigger a short soft screen flash
        flashIntensity = 0.65f;
    }

    public void tick(float delta) {
        if (flashIntensity > 0.0f) {
            flashIntensity = Math.max(0.0f, flashIntensity - delta * 0.08f);
        }
    }

    public void renderFlash() {
        if (!enabled || !screenFlash || flashIntensity <= 0.01f) return;

        // TODO: Fullscreen soft white/blue flash with intensity
    }

    public float getFlashIntensity() {
        return flashIntensity;
    }
}
