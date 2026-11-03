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

        AutoConfig.register(AetherluxConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(AetherluxConfig.class).getConfig();
        applyConfig();

        // Sky + Sun/Moon
        DimensionRenderingRegistry.registerSkyRenderer(World.OVERWORLD, context -> {
            if (CONFIG == null || !CONFIG.customSky) return;

            SKY_RENDERER.render(context);

            if (CONFIG.improvedSun || CONFIG.improvedMoon) {
                var matrices = context.matrixStack();
                if (matrices != null) {
                    CELESTIAL.render(matrices, context.tickDelta());
                }
            }
        });

        // Tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (CONFIG == null) return;
            if (CONFIG.waterEffects) WATER.tick(1.0f);
            if (CONFIG.lightningFlash) LIGHTNING.tick(1.0f);
        });

        // End of frame
        WorldRenderEvents.END.register(context -> {
            if (CONFIG == null) return;
            var matrices = context.matrixStack();
            if (matrices == null) return;

            if (CONFIG.lightningFlash) LIGHTNING.renderFlash(matrices);
            if (SHADOWS.isEnabled()) SHADOWS.applySoftShadows(matrices, context.tickDelta());
            if (CONFIG.godRays && GOD_RAYS.isEnabled()) GOD_RAYS.render(0.0f);
        });

        LOGGER.info("Aetherlux ready — Performance preset active");
    }

    public static void applyConfig() {
        if (CONFIG == null) return;

        SKY_RENDERER.showMilkyWay = CONFIG.milkyWay;
        SKY_RENDERER.showAndromeda = CONFIG.andromeda;
        SKY_RENDERER.showComets = CONFIG.comets;

        CELESTIAL.improvedSun = CONFIG.improvedSun;
        CELESTIAL.improvedMoon = CONFIG.improvedMoon;
        CELESTIAL.sunGlowIntensity = CONFIG.sunGlowPercent / 100f;

        SHADOWS.setQuality(CONFIG.shadowQuality);

        WATER.enabled = CONFIG.waterEffects;
        WATER.betterUnderwater = CONFIG.betterUnderwater;

        LIGHTNING.enabled = CONFIG.lightningFlash;
        LIGHTNING.screenFlash = CONFIG.lightningFlash;

        GOD_RAYS.setEnabled(CONFIG.godRays);
        GOD_RAYS.setIntensity(CONFIG.godRayIntensityPercent / 100f);
    }
}
