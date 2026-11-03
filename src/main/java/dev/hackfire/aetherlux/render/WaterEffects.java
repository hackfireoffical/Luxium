package dev.hackfire.aetherlux.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

/**
 * Lightweight water improvements for Aetherlux.
 *
 * Designed for weak GPUs (Redmi Note 12s):
 * - Better underwater fog & color
 * - Subtle surface darkening / wet look
 * - Very cheap, can be fully disabled
 */
public class WaterEffects {

    public boolean enabled = true;
    public boolean betterUnderwater = true;
    public boolean surfaceTint = true;

    // Underwater fog color (soft blue-cyan)
    private static final float FOG_R = 0.12f;
    private static final float FOG_G = 0.28f;
    private static final float FOG_B = 0.42f;

    public void tick(float tickDelta) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        if (betterUnderwater && client.player.isSubmergedInWater()) {
            applyUnderwaterFog(tickDelta);
        }
    }

    private void applyUnderwaterFog(float tickDelta) {
        // Soft underwater color grading
        // This is intentionally very cheap (just shader color)
        float intensity = 0.55f;

        RenderSystem.setShaderFogColor(
                MathHelper.lerp(intensity, 0.0f, FOG_R),
                MathHelper.lerp(intensity, 0.0f, FOG_G),
                MathHelper.lerp(intensity, 0.0f, FOG_B)
        );
    }

    /**
     * Called when rendering translucent blocks / water.
     * Adds a very subtle darkening so water feels wetter.
     */
    public void applySurfaceTint() {
        if (!enabled || !surfaceTint) return;

        // Extremely cheap multiply
        RenderSystem.setShaderColor(0.92f, 0.96f, 1.0f, 1.0f);
    }

    public void resetTint() {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
