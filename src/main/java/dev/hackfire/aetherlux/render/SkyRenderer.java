package dev.hackfire.aetherlux.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
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
 * Custom sky renderer for Aetherlux – PERFORMANCE FIRST.
 * Tuned for stable 60 FPS on Redmi Note 12s.
 */
public class SkyRenderer implements net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry.SkyRenderer {

    private static final Identifier MILKY_WAY = new Identifier("aetherlux", "textures/sky/milky_way.png");
    private static final Identifier ANDROMEDA = new Identifier("aetherlux", "textures/sky/andromeda.png");

    private static final int MAX_STARS = 1200;
    private static final int MAX_COMETS = 2;
    private static final float COMET_SPAWN_CHANCE = 0.0028f;

    private final List<Comet> comets = new ArrayList<>(MAX_COMETS);

    // Precomputed star data (built once)
    private float[] starX;
    private float[] starY;
    private float[] starZ;
    private float[] starSize;
    private int[] starR;
    private int[] starG;
    private int[] starB;
    private int[] starA;
    private boolean starsBuilt = false;

    public boolean showMilkyWay = true;
    public boolean showAndromeda = true;
    public boolean showComets = true;
    public float milkyWayBrightness = 1.05f;

    @Override
    public void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        long time = client.world.getTimeOfDay() % 24000L;
        if (time < 13000L || time > 23000L) return;

        MatrixStack matrices = context.matrixStack();
        if (matrices == null) return;

        float tickDelta = context.tickDelta();

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if (showMilkyWay) renderMilkyWay(matrices);
        if (showAndromeda) renderAndromeda(matrices);

        renderStars(matrices, tickDelta);

        if (showComets) updateAndRenderComets(matrices, tickDelta);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private void renderMilkyWay(MatrixStack matrices) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(22.0f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-38.0f));

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, MILKY_WAY);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.48f * milkyWayBrightness);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        float w = 290.0f;
        float h = 46.0f;

        buffer.vertex(matrix, -w, -h, -100.0f).texture(0.0f, 0.0f).color(255, 255, 255, 165).next();
        buffer.vertex(matrix,  w, -h, -100.0f).texture(1.0f, 0.0f).color(255, 255, 255, 165).next();
        buffer.vertex(matrix,  w,  h, -100.0f).texture(1.0f, 1.0f).color(255, 255, 255, 165).next();
        buffer.vertex(matrix, -w,  h, -100.0f).texture(0.0f, 1.0f).color(255, 255, 255, 165).next();

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        matrices.pop();
    }

    private void renderAndromeda(MatrixStack matrices) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(125.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-32.0f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(22.0f));

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, ANDROMEDA);
        RenderSystem.setShaderColor(0.92f, 0.88f, 1.0f, 0.55f);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        float size = 24.0f;

        buffer.vertex(matrix, -size, -size * 0.5f, -100.0f).texture(0.0f, 0.0f).color(220, 210, 255, 150).next();
        buffer.vertex(matrix,  size, -size * 0.5f, -100.0f).texture(1.0f, 0.0f).color(220, 210, 255, 150).next();
        buffer.vertex(matrix,  size,  size * 0.5f, -100.0f).texture(1.0f, 1.0f).color(220, 210, 255, 150).next();
        buffer.vertex(matrix, -size,  size * 0.5f, -100.0f).texture(0.0f, 1.0f).color(220, 210, 255, 150).next();

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        matrices.pop();
    }

    private void renderStars(MatrixStack matrices, float tickDelta) {
        if (!starsBuilt) {
            buildStars();
            starsBuilt = true;
        }

        matrices.push();
        float angle = (MinecraftClient.getInstance().world.getTime() + tickDelta) * 0.007f;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.88f);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        for (int i = 0; i < starX.length; i++) {
            float px = starX[i];
            float py = starY[i];
            float pz = starZ[i];
            float s = starSize[i];
            int r = starR[i];
            int g = starG[i];
            int b = starB[i];
            int a = starA[i];

            buffer.vertex(matrix, px - s, py - s, pz).color(r, g, b, a).next();
            buffer.vertex(matrix, px + s, py - s, pz).color(r, g, b, a).next();
            buffer.vertex(matrix, px + s, py + s, pz).color(r, g, b, a).next();
            buffer.vertex(matrix, px - s, py + s, pz).color(r, g, b, a).next();
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        matrices.pop();
    }

    private void buildStars() {
        Random random = Random.create(10842L);

        List<Float> xs = new ArrayList<>(MAX_STARS);
        List<Float> ys = new ArrayList<>(MAX_STARS);
        List<Float> zs = new ArrayList<>(MAX_STARS);
        List<Float> sizes = new ArrayList<>(MAX_STARS);
        List<Integer> rs = new ArrayList<>(MAX_STARS);
        List<Integer> gs = new ArrayList<>(MAX_STARS);
        List<Integer> bs = new ArrayList<>(MAX_STARS);
        List<Integer> as = new ArrayList<>(MAX_STARS);

        int created = 0;
        while (created < MAX_STARS) {
            double x = random.nextDouble() * 2.0 - 1.0;
            double y = random.nextDouble() * 2.0 - 1.0;
            double z = random.nextDouble() * 2.0 - 1.0;
            double distSq = x * x + y * y + z * z;

            if (distSq < 0.06 || distSq > 1.0) continue;

            double dist = Math.sqrt(distSq);
            x /= dist; y /= dist; z /= dist;

            xs.add((float) (x * 100.0));
            ys.add((float) (y * 100.0));
            zs.add((float) (z * 100.0));
            sizes.add(0.10f + random.nextFloat() * 0.18f);
            rs.add(190 + random.nextInt(45));
            gs.add(195 + random.nextInt(40));
            bs.add(220 + random.nextInt(30));
            as.add(150 + random.nextInt(75));

            created++;
        }

        int n = xs.size();
        starX = new float[n];
        starY = new float[n];
        starZ = new float[n];
        starSize = new float[n];
        starR = new int[n];
        starG = new int[n];
        starB = new int[n];
        starA = new int[n];

        for (int i = 0; i < n; i++) {
            starX[i] = xs.get(i);
            starY[i] = ys.get(i);
            starZ[i] = zs.get(i);
            starSize[i] = sizes.get(i);
            starR[i] = rs.get(i);
            starG[i] = gs.get(i);
            starB[i] = bs.get(i);
            starA[i] = as.get(i);
        }
    }

    private void updateAndRenderComets(MatrixStack matrices, float tickDelta) {
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
            if (c.isDead()) it.remove();
            else c.render(matrices);
        }

        matrices.pop();
    }

    public void reload() {
        starsBuilt = false;
        comets.clear();
    }

    private static class Comet {
        float x, y, z, vx, vy, vz, life, maxLife, size;

        Comet() {
            Random r = Random.create();
            x = (r.nextFloat() - 0.5f) * 150f;
            y = 50f + r.nextFloat() * 45f;
            z = -90f - r.nextFloat() * 25f;
            vx = 0.85f + r.nextFloat() * 1.3f;
            vy = -0.45f - r.nextFloat() * 0.65f;
            vz = 0.45f + r.nextFloat() * 0.7f;
            maxLife = 60f + r.nextFloat() * 40f;
            life = maxLife;
            size = 1.4f + r.nextFloat() * 1.5f;
        }

        void tick(float delta) {
            x += vx * delta;
            y += vy * delta;
            z += vz * delta;
            life -= delta;
        }

        boolean isDead() { return life <= 0f; }

        void render(MatrixStack matrices) {
            float alpha = MathHelper.clamp(life / maxLife, 0f, 1f);
            int a = (int) (alpha * 220);

            Matrix4f matrix = matrices.peek().getPositionMatrix();
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            float s = size;
            buffer.vertex(matrix, x - s, y - s, z).color(200, 230, 255, a).next();
            buffer.vertex(matrix, x + s, y - s, z).color(200, 230, 255, a).next();
            buffer.vertex(matrix, x + s, y + s, z).color(200, 230, 255, a).next();
            buffer.vertex(matrix, x - s, y + s, z).color(200, 230, 255, a).next();

            float trail = 18f * alpha;
            float tx = x - vx * trail;
            float ty = y - vy * trail;
            float tz = z - vz * trail;

            buffer.vertex(matrix, x, y, z).color(120, 190, 255, a).next();
            buffer.vertex(matrix, x + 0.6f, y, z).color(120, 190, 255, a).next();
            buffer.vertex(matrix, tx + 0.25f, ty, tz).color(40, 100, 220, 0).next();
            buffer.vertex(matrix, tx, ty, tz).color(40, 100, 220, 0).next();

            BufferRenderer.drawWithGlobalProgram(buffer.end());
        }
    }
}
