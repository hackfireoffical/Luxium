package dev.hackfire.aetherlux.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

/**
 * Improved lightning visuals for Aetherlux.
 *
 * - Soft fullscreen flash when lightning strikes
 * - Slight blue-white tint
 * - Extremely cheap (just a fullscreen quad + intensity decay)
 */
public class LightningEffects {

    public boolean enabled = true;
    public boolean screenFlash = true;

    private float flashIntensity = 0.0f;
    private float flashTarget = 0.0f;

    /**
     * Call this when a lightning bolt is spawned / struck.
     */
    public void onLightningStrike() {
        if (!enabled) return;

        // Strong but short flash
        flashTarget = 0.72f;
        flashIntensity = 0.72f;
    }

    public void tick(float delta) {
        if (!enabled) return;

        // Smooth decay
        if (flashIntensity > 0.0f) {
            flashIntensity = Math.max(0.0f, flashIntensity - delta * 0.085f);
        }

        // Also detect vanilla lightning bolts nearby as fallback
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null && client.player != null) {
            // Very light check – only when raining / thundering
            if (client.world.isThundering() && client.world.random.nextInt(180) == 0) {
                // Occasional ambient flash during heavy storms
                if (flashIntensity < 0.15f) {
                    flashIntensity = 0.25f + client.world.random.nextFloat() * 0.2f;
                }
            }
        }
    }

    /**
     * Renders the soft fullscreen flash.
     * Call this at the end of the frame (after world rendering).
     */
    public void renderFlash(MatrixStack matrices) {
        if (!enabled || !screenFlash || flashIntensity <= 0.01f) return;

        float alpha = MathHelper.clamp(flashIntensity, 0.0f, 0.75f);

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        // Soft blue-white flash
        int a = (int) (alpha * 180);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        // Fullscreen quad in clip space-ish (big enough to cover screen)
        float s = 5000.0f;
        buffer.vertex(matrix, -s, -s, -100).color(210, 225, 255, a).next();
        buffer.vertex(matrix,  s, -s, -100).color(210, 225, 255, a).next();
        buffer.vertex(matrix,  s,  s, -100).color(210, 225, 255, a).next();
        buffer.vertex(matrix, -s,  s, -100).color(210, 225, 255, a).next();

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public float getFlashIntensity() {
        return flashIntensity;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
