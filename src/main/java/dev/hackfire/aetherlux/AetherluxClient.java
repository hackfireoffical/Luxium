package dev.hackfire.aetherlux;

import dev.hackfire.aetherlux.config.AetherluxConfig;
import dev.hackfire.aetherlux.render.*;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
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

    public static AetherluxConfig CONFIG;

    public static final SkyRenderer SKY_RENDERER = new SkyRenderer();
    public static final CelestialRenderer CELESTIAL = new CelestialRenderer();
    public static final ShadowMap SHADOWS = new ShadowMap();
    public static final WaterEffects WATER = new WaterEffects();
    public static final LightningEffects LIGHTNING = new LightningEffects();
    public static final GodRays GOD_RAYS = new GodRays();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Aetherlux initializing...");

        // Config
        AutoConfig.register(AetherluxConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(AetherluxConfig.class).getConfig();
        applyConfig();

        // Custom sky + sun/moon
        DimensionRenderingRegistry.registerSkyRenderer(World.OVERWORLD, context -> {
            if (!CONFIG.customSky) return;

            SKY_RENDERER.render(context);

            var matrices = context.matrixStack();
            if (matrices != null && (CONFIG.improvedSun || CONFIG.improvedMoon)) {
                CELESTIAL.render(matrices, context.tickDelta());
            }
        });

        // Tick water + lightning
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            WATER.tick(1.0f);
            LIGHTNING.tick(1.0f);
        });

        // End-of-frame effects
        WorldRenderEvents.END.register(context -> {
            var matrices = context.matrixStack();
            if (matrices == null) return;

            if (CONFIG.lightningFlash) {
                LIGHTNING.renderFlash(matrices);
            }

            SHADOWS.applySoftShadows(matrices, context.tickDelta());

            if (CONFIG.godRays) {
                GOD_RAYS.render(0.0f); // angle filled later
            }
        });

        LOGGER.info("Aetherlux ready — Config screen available in Mod Menu");
    }

    /** Call this after the user changes settings */
    public static void applyConfig() {
        if (CONFIG == null) return;

        // Sky
        SKY_RENDERER.showMilkyWay = CONFIG.milkyWay;
        SKY_RENDERER.showAndromeda = CONFIG.andromeda;
        SKY_RENDERER.showComets = CONFIG.comets;

        // Celestial
        CELESTIAL.improvedSun = CONFIG.improvedSun;
        CELESTIAL.improvedMoon = CONFIG.improvedMoon;
        CELESTIAL.sunGlowIntensity = CONFIG.sunGlowPercent / 100f;

        // Shadows
        SHADOWS.setQuality(CONFIG.shadowQuality);

        // Water
        WATER.enabled = CONFIG.waterEffects;
        WATER.betterUnderwater = CONFIG.betterUnderwater;

        // Lightning
        LIGHTNING.enabled = CONFIG.lightningFlash;
        LIGHTNING.screenFlash = CONFIG.lightningFlash;

        // God Rays
        GOD_RAYS.setEnabled(CONFIG.godRays);
        GOD_RAYS.setIntensity(CONFIG.godRayIntensityPercent / 100f);
    }
}
