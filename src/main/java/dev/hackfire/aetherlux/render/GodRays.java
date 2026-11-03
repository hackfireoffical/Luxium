package dev.hackfire.aetherlux.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

/**
 * Cheap god rays / light shafts for Aetherlux.
 *
 * Technique: simple radial screen-space glow from the sun direction.
 * Extremely lightweight so it stays friendly to Mali-G52 / Redmi Note 12s.
 */
public class GodRays {

    private boolean enabled = true;
    private float intensity = 0.55f;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setIntensity(float intensity) {
        this.intensity = MathHelper.clamp(intensity, 0.0f, 1.0f);
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Call at the end of the frame.
     * @param sunAngle currently unused (we read it from the world)
     */
    public void render(float sunAngle) {
        if (!enabled || intensity < 0.01f) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        // Only show god rays when the sun is reasonably high and it's not raining hard
        float skyAngle = client.world.getSkyAngle(1.0f);
        float sunHeight = MathHelper.cos(skyAngle * (float) (Math.PI * 2.0));
        if (sunHeight < 0.2f) return;

        float rain = client.world.getRainGradient(1.0f);
        float finalIntensity = intensity * sunHeight * (1.0f - rain * 0.7f);
        if (finalIntensity < 0.05f) return;

        // Very cheap approximation: soft radial brightening toward the top of the screen
        // (real radial blur would be more expensive)
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        MatrixStack matrices = new MatrixStack();
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        int alpha = (int) (finalIntensity * 55);

        // Soft vertical shafts from the top (sun direction approximation)
        float s = 3000.0f;
        buffer.vertex(matrix, -s,  s * 0.6f, -50).color(255, 240, 200, alpha).next();
        buffer.vertex(matrix,  s,  s * 0.6f, -50).color(255, 240, 200, alpha).next();
        buffer.vertex(matrix,  s, -s, -50).color(255, 240, 200, 0).next();
        buffer.vertex(matrix, -s, -s, -50).color(255, 240, 200, 0).next();

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}
