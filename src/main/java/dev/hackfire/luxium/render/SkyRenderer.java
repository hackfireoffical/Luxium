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
import java.util.Iterator;
import java.util.List;

/**
 * Custom sky renderer for Luxium – PERFORMANCE FIRST.
 *
 * Target device: Redmi Note 12s (Helio G88 / Mali-G52)
 * Goal: Stable 60 FPS while still looking beautiful.
 *
 * Design choices for performance:
 * - Low star count (1200–1800) using STATIC VertexBuffer (built once)
 * - Max 3 comets at a time
 * - Galaxies = only 2 simple textured quads
 * - No per-frame allocations in hot path
 * - No complex math every frame
 * - Stars + galaxies only rendered at night
 */
public class SkyRenderer implements net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry.SkyRenderer {

    private static final Identifier MILKY_WAY = Identifier.of("luxium", "textures/sky/milky_way.png");
    private static final Identifier ANDROMEDA = Identifier.of("luxium", "textures/sky/andromeda.png");

    // === Performance-tuned defaults for Redmi Note 12s ===
    private static final int MAX_STARS = 1600;          // sweet spot for Mali-G52
    private static final int MAX_COMETS = 3;            // never more than 3
    private static final float COMET_SPAWN_CHANCE = 0.004f;

    private final List<Comet> comets = new ArrayList<>(MAX_COMETS);
    private boolean starsBuilt = false;
    private VertexBuffer starBuffer;

    // Runtime toggles (config later)
    public boolean showMilkyWay = true;
    public boolean showAndromeda = true;
    public boolean showComets = true;
    public float milkyWayBrightness = 1.1f;

    @Override
    public void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        // Fast night check
        long time = client.world.getTimeOfDay() % 24000L;
        if (time < 13000L || time > 23000L) {
            return; // day → let vanilla handle everything
        }

        MatrixStack matrices = context.matrixStack();
        if (matrices == null) return;

        float tickDelta = context.tickCounter().getTickDelta(false);

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Order matters for performance + looks
        if (showMilkyWay) {
            renderMilkyWay(matrices);
        }
        if (showAndromeda) {
            renderAndromeda(matrices);
        }

        renderStars(matrices, tickDelta);

        if (showComets) {
            updateAndRenderComets(matrices, tickDelta);
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private void renderMilkyWay(MatrixStack matrices) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(22.0f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-38.0f));

        RenderSystem.setShaderTexture(0, MILKY_WAY);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.50f * milkyWayBrightness);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        float w = 290.0f;
        float h = 48.0f;

        buffer.vertex(matrix, -w, -h, -100.0f).texture(0.0f, 0.0f).color(255, 255, 255, 170);
        buffer.vertex(matrix,  w, -h, -100.0f).texture(1.0f, 0.0f).color(255, 255, 255, 170);
        buffer.vertex(matrix,  w,  h, -100.0f).texture(1.0f, 1.0f).color(255, 255, 255, 170);
        buffer.vertex(matrix, -w,  h, -100.0f).texture(0.0f, 1.0f).color(255, 255, 255, 170);

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        matrices.pop();
    }

    private void renderAndromeda(MatrixStack matrices) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(125.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-32.0f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(22.0f));

        RenderSystem.setShaderTexture(0, ANDROMEDA);
        RenderSystem.setShaderColor(0.92f, 0.88f, 1.0f, 0.60f);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        float size = 26.0f;

        buffer.vertex(matrix, -size, -size * 0.5f, -100.0f).texture(0.0f, 0.0f).color(220, 210, 255, 155);
        buffer.vertex(matrix,  size, -size * 0.5f, -100.0f).texture(1.0f, 0.0f).color(220, 210, 255, 155);
        buffer.vertex(matrix,  size,  size * 0.5f, -100.0f).texture(1.0f, 1.0f).color(220, 210, 255, 155);
        buffer.vertex(matrix, -size,  size * 0.5f, -100.0f).texture(0.0f, 1.0f).color(220, 210, 255, 155);

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        matrices.pop();
    }

    private void renderStars(MatrixStack matrices, float tickDelta) {
        if (!starsBuilt) {
            buildStars();
            starsBuilt = true;
        }
        if (starBuffer == null) return;

        matrices.push();
        // Very slow rotation – almost free
        float angle = (MinecraftClient.getInstance().world.getTime() + tickDelta) * 0.008f;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.92f);

        starBuffer.bind();
        starBuffer.draw(
                matrices.peek().getPositionMatrix(),
                RenderSystem.getProjectionMatrix(),
                GameRenderer.getPositionColorProgram()
        );
        VertexBuffer.unbind();

        matrices.pop();
    }

    private void buildStars() {
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Random random = Random.create(10842L);

        int created = 0;
        while (created < MAX_STARS) {
            double x = random.nextDouble() * 2.0 - 1.0;
            double y = random.nextDouble() * 2.0 - 1.0;
            double z = random.nextDouble() * 2.0 - 1.0;
            double distSq = x * x + y * y + z * z;

            if (distSq < 0.05 || distSq > 1.0) continue;

            double dist = Math.sqrt(distSq);
            x /= dist;
            y /= dist;
            z /= dist;

            // Smaller stars = cheaper + denser look
            float size = 0.12f + random.nextFloat() * 0.22f;

            float px = (float) (x * 100.0);
            float py = (float) (y * 100.0);
            float pz = (float) (z * 100.0);

            // Slight color variation (cool white → soft blue)
            int r = 190 + random.nextInt(50);
            int g = 195 + random.nextInt(45);
            int b = 220 + random.nextInt(35);
            int a = 160 + random.nextInt(80);

            // Tiny axis-aligned quad (very cheap)
            buffer.vertex(px - size, py - size, pz).color(r, g, b, a);
            buffer.vertex(px + size, py - size, pz).color(r, g, b, a);
            buffer.vertex(px + size, py + size, pz).color(r, g, b, a);
            buffer.vertex(px - size, py + size, pz).color(r, g, b, a);

            created++;
        }

        starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        starBuffer.bind();
        starBuffer.upload(buffer.end());
        VertexBuffer.unbind();
    }

    private void updateAndRenderComets(MatrixStack matrices, float tickDelta) {
        // Spawn
        if (comets.size() < MAX_COMETS &&
                MinecraftClient.getInstance().world.random.nextFloat() < COMET_SPAWN_CHANCE) {
            comets.add(new Comet());
        }

        if (comets.isEmpty()) return;

        matrices.push();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Iterator<Comet> it = comets.iterator();
        while (it.hasNext()) {
            Comet c = it.next();
            c.tick(tickDelta);
            if (c.isDead()) {
                it.remove();
            } else {
                c.render(matrices);
            }
        }

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

    // === Ultra-light comet ===
    private static class Comet {
        float x, y, z;
        float vx, vy, vz;
        float life;
        float maxLife;
        float size;

        Comet() {
            Random r = Random.create();
            x = (r.nextFloat() - 0.5f) * 160f;
            y = 50f + r.nextFloat() * 50f;
            z = -90f - r.nextFloat() * 30f;

            vx = 0.9f + r.nextFloat() * 1.4f;
            vy = -0.5f - r.nextFloat() * 0.7f;
            vz = 0.5f + r.nextFloat() * 0.8f;

            maxLife = 70f + r.nextFloat() * 50f;
            life = maxLife;
            size = 1.6f + r.nextFloat() * 1.8f;
        }

        void tick(float delta) {
            x += vx * delta;
            y += vy * delta;
            z += vz * delta;
            life -= delta;
        }

        boolean isDead() {
            return life <= 0f;
        }

        void render(MatrixStack matrices) {
            float alpha = MathHelper.clamp(life / maxLife, 0f, 1f);
            int a = (int) (alpha * 240);

            Matrix4f matrix = matrices.peek().getPositionMatrix();
            BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            // Bright head (small)
            float s = size;
            buffer.vertex(matrix, x - s, y - s, z).color(200, 230, 255, a);
            buffer.vertex(matrix, x + s, y - s, z).color(200, 230, 255, a);
            buffer.vertex(matrix, x + s, y + s, z).color(200, 230, 255, a);
            buffer.vertex(matrix, x - s, y + s, z).color(200, 230, 255, a);

            // Simple long trail (only 1 extra quad)
            float trail = 22f * alpha;
            float tx = x - vx * trail;
            float ty = y - vy * trail;
            float tz = z - vz * trail;

            buffer.vertex(matrix, x, y, z).color(120, 190, 255, a);
            buffer.vertex(matrix, x + 0.8f, y, z).color(120, 190, 255, a);
            buffer.vertex(matrix, tx + 0.4f, ty, tz).color(40, 100, 220, 0);
            buffer.vertex(matrix, tx, ty, tz).color(40, 100, 220, 0);

            BufferRenderer.drawWithGlobalProgram(buffer.end());
        }
    }
}
