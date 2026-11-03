package dev.hackfire.luxium.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom sky renderer for Luxium.
 *
 * Target look:
 * - Deep navy/blue night sky
 * - Extremely dense star field
 * - Large bright Milky Way band
 * - Smaller Andromeda galaxy
 * - Bright glowing comets with long trails
 */
public class SkyRenderer implements net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry.SkyRenderer {

    private static final Identifier MILKY_WAY = Identifier.of("luxium", "textures/sky/milky_way.png");
    private static final Identifier ANDROMEDA = Identifier.of("luxium", "textures/sky/andromeda.png");

    private final List<Comet> comets = new ArrayList<>();
    private boolean starsBuilt = false;
    private VertexBuffer starBuffer;

    // Config (will be driven by config screen later)
    public boolean showMilkyWay = true;
    public boolean showAndromeda = true;
    public boolean showComets = true;
    public float starDensity = 1.4f;
    public float milkyWayBrightness = 1.15f;
    public float cometFrequency = 0.7f;

    @Override
    public void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        float tickDelta = context.tickCounter().getTickDelta(false);
        float timeOfDay = client.world.getTimeOfDay() % 24000;
        boolean isNight = timeOfDay > 13000 && timeOfDay < 23000;

        // Only do the full custom night sky at night for now
        if (!isNight) {
            // Let vanilla handle day sky for the moment
            return;
        }

        MatrixStack matrices = context.matrixStack();
        if (matrices == null) return;

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 1. Deep blue sky disc (simple for now)
        renderDeepBlueSky(matrices);

        // 2. Large Milky Way
        if (showMilkyWay) {
            renderMilkyWay(matrices, tickDelta);
        }

        // 3. Smaller Andromeda
        if (showAndromeda) {
            renderAndromeda(matrices, tickDelta);
        }

        // 4. Dense stars
        renderStars(matrices, tickDelta);

        // 5. Comets
        if (showComets) {
            updateAndRenderComets(matrices, tickDelta);
        }

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private void renderDeepBlueSky(MatrixStack matrices) {
        // Very simple solid deep blue for now – will be replaced with proper gradient later
        RenderSystem.setShaderColor(0.02f, 0.03f, 0.12f, 1.0f);
        // We intentionally leave most of the sky to vanilla + our overlays for performance
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    private void renderMilkyWay(MatrixStack matrices, float tickDelta) {
        matrices.push();

        // Rotate the band so it looks like a real Milky Way
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(25.0f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-40.0f));

        // Large textured band
        RenderSystem.setShaderTexture(0, MILKY_WAY);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.55f * milkyWayBrightness);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        float size = 140.0f;
        // Simple large quad stretched into a band
        buffer.vertex(matrix, -size * 2.2f, -size * 0.35f, -100.0f).texture(0.0f, 0.0f).color(255, 255, 255, 180);
        buffer.vertex(matrix,  size * 2.2f, -size * 0.35f, -100.0f).texture(1.0f, 0.0f).color(255, 255, 255, 180);
        buffer.vertex(matrix,  size * 2.2f,  size * 0.35f, -100.0f).texture(1.0f, 1.0f).color(255, 255, 255, 180);
        buffer.vertex(matrix, -size * 2.2f,  size * 0.35f, -100.0f).texture(0.0f, 1.0f).color(255, 255, 255, 180);

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        matrices.pop();
    }

    private void renderAndromeda(MatrixStack matrices, float tickDelta) {
        matrices.push();

        // Position Andromeda in a different part of the sky
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(130.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-35.0f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(25.0f)); // tilt

        RenderSystem.setShaderTexture(0, ANDROMEDA);
        RenderSystem.setShaderColor(0.9f, 0.85f, 1.0f, 0.65f);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        float size = 28.0f; // much smaller than Milky Way
        buffer.vertex(matrix, -size, -size * 0.55f, -100.0f).texture(0.0f, 0.0f).color(220, 210, 255, 160);
        buffer.vertex(matrix,  size, -size * 0.55f, -100.0f).texture(1.0f, 0.0f).color(220, 210, 255, 160);
        buffer.vertex(matrix,  size,  size * 0.55f, -100.0f).texture(1.0f, 1.0f).color(220, 210, 255, 160);
        buffer.vertex(matrix, -size,  size * 0.55f, -100.0f).texture(0.0f, 1.0f).color(220, 210, 255, 160);

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        matrices.pop();
    }

    private void renderStars(MatrixStack matrices, float tickDelta) {
        if (!starsBuilt) {
            buildStars();
            starsBuilt = true;
        }

        if (starBuffer == null) return;

        matrices.push();
        // Slow rotation of the whole star field
        float angle = (MinecraftClient.getInstance().world.getTime() + tickDelta) * 0.01f;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.95f);

        starBuffer.bind();
        starBuffer.draw(matrices.peek().getPositionMatrix(), RenderSystem.getProjectionMatrix(), GameRenderer.getPositionColorProgram());
        VertexBuffer.unbind();

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        matrices.pop();
    }

    private void buildStars() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        Random random = Random.create(10842L); // fixed seed for consistent sky
        int starCount = (int) (3500 * starDensity);

        for (int i = 0; i < starCount; i++) {
            double x = random.nextDouble() * 2.0 - 1.0;
            double y = random.nextDouble() * 2.0 - 1.0;
            double z = random.nextDouble() * 2.0 - 1.0;
            double dist = Math.sqrt(x * x + y * y + z * z);
            if (dist < 0.01 || dist > 1.0) continue;

            x /= dist;
            y /= dist;
            z /= dist;

            double size = 0.15 + random.nextDouble() * 0.35;
            // Project onto a large sphere
            double px = x * 100.0;
            double py = y * 100.0;
            double pz = z * 100.0;

            // Simple billboard-ish star (axis-aligned for speed)
            float brightness = 0.6f + random.nextFloat() * 0.4f;
            int r = (int) (180 + random.nextFloat() * 75);
            int g = (int) (180 + random.nextFloat() * 75);
            int b = (int) (200 + random.nextFloat() * 55);

            // 4 vertices of a small quad facing the camera roughly
            buffer.vertex((float)(px - size), (float)(py - size), (float)pz).color(r, g, b, (int)(brightness * 255));
            buffer.vertex((float)(px + size), (float)(py - size), (float)pz).color(r, g, b, (int)(brightness * 255));
            buffer.vertex((float)(px + size), (float)(py + size), (float)pz).color(r, g, b, (int)(brightness * 255));
            buffer.vertex((float)(px - size), (float)(py + size), (float)pz).color(r, g, b, (int)(brightness * 255));
        }

        starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        starBuffer.bind();
        starBuffer.upload(buffer.end());
        VertexBuffer.unbind();
    }

    private void updateAndRenderComets(MatrixStack matrices, float tickDelta) {
        // Spawn new comets occasionally
        if (MinecraftClient.getInstance().world.random.nextFloat() < 0.008f * cometFrequency) {
            if (comets.size() < 6) {
                comets.add(new Comet());
            }
        }

        matrices.push();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        List<Comet> toRemove = new ArrayList<>();
        for (Comet comet : comets) {
            comet.tick(tickDelta);
            if (comet.isDead()) {
                toRemove.add(comet);
                continue;
            }
            comet.render(matrices);
        }
        comets.removeAll(toRemove);

        matrices.pop();
    }

    public void reload() {
        if (starBuffer != null) {
            starBuffer.close();
            starBuffer = null;
        }
        starsBuilt = false;
        comets.clear();
    }

    // Simple comet
    private static class Comet {
        float x, y, z;
        float vx, vy, vz;
        float life;
        float maxLife;
        float size;

        Comet() {
            Random r = Random.create();
            // Start from one side of the sky
            x = (r.nextFloat() - 0.5f) * 180f;
            y = 40f + r.nextFloat() * 60f;
            z = -80f - r.nextFloat() * 40f;

            vx = (r.nextFloat() - 0.3f) * 1.8f;
            vy = -0.4f - r.nextFloat() * 0.8f;
            vz = 0.6f + r.nextFloat() * 0.9f;

            maxLife = 80f + r.nextFloat() * 60f;
            life = maxLife;
            size = 1.8f + r.nextFloat() * 2.2f;
        }

        void tick(float delta) {
            x += vx * delta;
            y += vy * delta;
            z += vz * delta;
            life -= delta;
        }

        boolean isDead() {
            return life <= 0;
        }

        void render(MatrixStack matrices) {
            float alpha = MathHelper.clamp(life / maxLife, 0f, 1f);
            Matrix4f matrix = matrices.peek().getPositionMatrix();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            // Bright head
            float s = size;
            int a = (int) (alpha * 255);
            buffer.vertex(matrix, x - s, y - s, z).color(180, 220, 255, a);
            buffer.vertex(matrix, x + s, y - s, z).color(180, 220, 255, a);
            buffer.vertex(matrix, x + s, y + s, z).color(180, 220, 255, a);
            buffer.vertex(matrix, x - s, y + s, z).color(180, 220, 255, a);

            // Long glowing trail
            float trailLen = 28f * alpha;
            buffer.vertex(matrix, x, y, z).color(100, 180, 255, a);
            buffer.vertex(matrix, x - vx * trailLen * 0.3f, y - vy * trailLen * 0.3f, z - vz * trailLen * 0.3f).color(80, 150, 255, a / 2);
            buffer.vertex(matrix, x - vx * trailLen, y - vy * trailLen, z - vz * trailLen).color(40, 100, 220, 0);
            buffer.vertex(matrix, x - vx * trailLen * 0.5f, y - vy * trailLen * 0.5f, z - vz * trailLen * 0.5f).color(60, 130, 240, a / 3);

            BufferRenderer.drawWithGlobalProgram(buffer.end());
        }
    }
}
