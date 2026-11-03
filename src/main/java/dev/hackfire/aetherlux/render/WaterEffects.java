package dev.hackfire.aetherlux.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;

/**
 * Lightweight water improvements for Aetherlux.
 */
public class WaterEffects {

    public boolean enabled = true;
    public boolean betterUnderwater = true;
    public boolean surfaceTint = true;

    public void tick(float tickDelta) {
        if (!enabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        if (betterUnderwater && client.player.isSubmergedInWater()) {
            // Soft underwater tint (very cheap)
            RenderSystem.setShaderFogColor(0.12f, 0.28f, 0.42f);
        }
    }

    public void applySurfaceTint() {
        if (!enabled || !surfaceTint) return;
        RenderSystem.setShaderColor(0.92f, 0.96f, 1.0f, 1.0f);
    }

    public void resetTint() {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
