package dev.hackfire.aetherlux;

import dev.hackfire.aetherlux.render.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AetherluxClient implements ClientModInitializer {
    public static final String MOD_ID = "aetherlux";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final SkyRenderer SKY_RENDERER = new SkyRenderer();
    public static final CelestialRenderer CELESTIAL = new CelestialRenderer();
    public static final ShadowMap SHADOWS = new ShadowMap();
    public static final WaterEffects WATER = new WaterEffects();
    public static final LightningEffects LIGHTNING = new LightningEffects();
    public static final GodRays GOD_RAYS = new GodRays();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Aetherlux initializing...");

        // Custom sky + sun/moon
        DimensionRenderingRegistry.registerSkyRenderer(World.OVERWORLD, context -> {
            SKY_RENDERER.render(context);

            var matrices = context.matrixStack();
            if (matrices != null) {
                CELESTIAL.render(matrices, context.tickDelta());
            }
        });

        // Default to Performance preset
        SHADOWS.setQuality(ShadowMap.Quality.PERFORMANCE);

        // Tick water + lightning
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            float delta = 1.0f; // approximate
            WATER.tick(delta);
            LIGHTNING.tick(delta);
        });

        // Render lightning flash at the end of the world render
        WorldRenderEvents.END.register(context -> {
            var matrices = context.matrixStack();
            if (matrices != null) {
                LIGHTNING.renderFlash(matrices);
                SHADOWS.applySoftShadows(matrices, context.tickDelta());
            }
        });

        LOGGER.info("Aetherlux ready — Sky, Sun/Moon, Shadows, Water, Lightning active");
    }
}
