package dev.hackfire.aetherlux.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

/**
 * Improved Sun and Moon for Aetherlux.
 */
public class CelestialRenderer {

    private static final Identifier MOON_PHASES = new Identifier("minecraft", "textures/environment/moon_phases.png");
    private static final Identifier SUN = new Identifier("minecraft", "textures/environment/sun.png");

    public boolean improvedSun = true;
    public boolean improvedMoon = true;
    public float sunGlowIntensity = 0.75f;
    public float moonBrightness = 1.15f;

    public void render(MatrixStack matrices, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        float skyAngle = client.world.getSkyAngle(tickDelta);
        float rainGradient = 1.0f - client.world.getRainGradient(tickDelta);

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(skyAngle * 360.0f));

        if (improvedSun) {
            renderSunWithGlow(matrices, rainGradient);
        }

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
        if (improvedMoon) {
            renderMoon(matrices, rainGradient, client.world.getMoonPhase());
        }

        matrices.pop();
    }

    private void renderSunWithGlow(MatrixStack matrices, float alpha) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, SUN);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        float size = 28.0f;
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(matrix, -size, 100.0f, -size).texture(0.0f, 0.0f).color(255, 255, 255, 255).next();
        buffer.vertex(matrix,  size, 100.0f, -size).texture(1.0f, 0.0f).color(255, 255, 255, 255).next();
        buffer.vertex(matrix,  size, 100.0f,  size).texture(1.0f, 1.0f).color(255, 255, 255, 255).next();
        buffer.vertex(matrix, -size, 100.0f,  size).texture(0.0f, 1.0f).color(255, 255, 255, 255).next();
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        if (sunGlowIntensity > 0.01f) {
            float glowSize = size * 2.8f;
            int glowAlpha = (int) (90 * sunGlowIntensity * alpha);

            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            buffer.vertex(matrix, -glowSize, 100.0f, -glowSize).texture(0.0f, 0.0f).color(255, 240, 200, glowAlpha).next();
            buffer.vertex(matrix,  glowSize, 100.0f, -glowSize).texture(1.0f, 0.0f).color(255, 240, 200, glowAlpha).next();
            buffer.vertex(matrix,  glowSize, 100.0f,  glowSize).texture(1.0f, 1.0f).color(255, 240, 200, glowAlpha).next();
            buffer.vertex(matrix, -glowSize, 100.0f,  glowSize).texture(0.0f, 1.0f).color(255, 240, 200, glowAlpha).next();
            BufferRenderer.drawWithGlobalProgram(buffer.end());
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderMoon(MatrixStack matrices, float alpha, int phase) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, MOON_PHASES);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha * moonBrightness);

        int phaseX = phase % 4;
        int phaseY = phase / 4 % 2;
        float u1 = phaseX / 4.0f;
        float v1 = phaseY / 2.0f;
        float u2 = (phaseX + 1) / 4.0f;
        float v2 = (phaseY + 1) / 2.0f;

        float size = 22.0f;
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(matrix, -size, -100.0f,  size).texture(u2, v2).color(255, 255, 255, 255).next();
        buffer.vertex(matrix,  size, -100.0f,  size).texture(u1, v2).color(255, 255, 255, 255).next();
        buffer.vertex(matrix,  size, -100.0f, -size).texture(u1, v1).color(255, 255, 255, 255).next();
        buffer.vertex(matrix, -size, -100.0f, -size).texture(u2, v1).color(255, 255, 255, 255).next();
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        float glowSize = size * 1.9f;
        int glowAlpha = (int) (70 * alpha);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(matrix, -glowSize, -100.0f,  glowSize).texture(u2, v2).color(200, 210, 255, glowAlpha).next();
        buffer.vertex(matrix,  glowSize, -100.0f,  glowSize).texture(u1, v2).color(200, 210, 255, glowAlpha).next();
        buffer.vertex(matrix,  glowSize, -100.0f, -glowSize).texture(u1, v1).color(200, 210, 255, glowAlpha).next();
        buffer.vertex(matrix, -glowSize, -100.0f, -glowSize).texture(u2, v1).color(200, 210, 255, glowAlpha).next();
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
