package dev.hackfire.aetherlux;

import dev.hackfire.aetherlux.render.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AetherluxClient implements ClientModInitializer {
    public static final String MOD_ID = "aetherlux";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Core systems
    public static final SkyRenderer SKY_RENDERER = new SkyRenderer();
    public static final CelestialRenderer CELESTIAL = new CelestialRenderer();
    public static final ShadowMap SHADOWS = new ShadowMap();
    public static final WaterEffects WATER = new WaterEffects();
    public static final LightningEffects LIGHTNING = new LightningEffects();
    public static final GodRays GOD_RAYS = new GodRays();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Aetherlux initializing...");

        // Custom sky (stars, Milky Way, Andromeda, comets)
        DimensionRenderingRegistry.registerSkyRenderer(World.OVERWORLD, SKY_RENDERER);

        // Default to Performance preset for weak devices
        SHADOWS.setQuality(ShadowMap.Quality.PERFORMANCE);

        LOGGER.info("Aetherlux ready – Sky, Shadows, Water, Lightning systems loaded");
    }
}
