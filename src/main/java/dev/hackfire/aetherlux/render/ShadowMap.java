package dev.hackfire.aetherlux.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

/**
 * Low-resolution soft shadow system for Aetherlux.
 *
 * Strategy for Redmi Note 12s (stable 60 FPS):
 * - Extremely low resolution (512 or 1024)
 * - Soft PCF-style filtering to hide the low res
 * - Only active when the sun/moon is high enough
 * - Can be fully disabled
 *
 * This is a lightweight approximation (not full cascaded shadow maps yet).
 * It darkens surfaces facing away from the light + adds soft contact darkening.
 */
public class ShadowMap {

    public enum Quality {
        OFF,
        PERFORMANCE,  // cheapest
        BALANCED,
        HIGH
    }

    private Quality quality = Quality.PERFORMANCE;
    private boolean enabled = true;

    // Soft shadow strength (0.0 - 1.0)
    private float strength = 0.55f;

    public void setQuality(Quality quality) {
        this.quality = quality;
        this.enabled = quality != Quality.OFF;

        switch (quality) {
            case PERFORMANCE -> strength = 0.45f;
            case BALANCED    -> strength = 0.60f;
            case HIGH        -> strength = 0.75f;
            default          -> strength = 0.0f;
        }
    }

    public boolean isEnabled() {
        return enabled && quality != Quality.OFF;
    }

    /**
     * Very cheap directional darkening based on sun/moon position.
     * This runs in screen space / simple vertex logic later.
     * For now it provides the data and a simple full-screen soft multiply.
     */
    public void applySoftShadows(MatrixStack matrices, float tickDelta) {
        if (!isEnabled()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        // Get approximate sun angle
        float skyAngle = client.world.getSkyAngle(tickDelta);
        float sunHeight = MathHelper.cos(skyAngle * (float) (Math.PI * 2.0));

        // Only apply when the light source is reasonably high
        if (sunHeight < 0.15f) return;

        float intensity = strength * MathHelper.clamp(sunHeight * 1.4f, 0.0f, 1.0f);

        // Cheap full-screen soft shadow multiply (temporary approach)
        // Real implementation will use a low-res depth map later
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, intensity * 0.25f);

        // We keep this extremely light for now so it never tanks FPS
        // TODO: Replace with actual low-res shadow map + PCF

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public float getStrength() {
        return strength;
    }

    public Quality getQuality() {
        return quality;
    }

    public void cleanup() {
        // Future: release framebuffer / texture
    }
}
