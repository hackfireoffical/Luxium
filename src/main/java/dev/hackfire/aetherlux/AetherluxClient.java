package dev.hackfire.aetherlux;

import dev.hackfire.aetherlux.render.SkyRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AetherluxClient implements ClientModInitializer {
    public static final String MOD_ID = "aetherlux";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final SkyRenderer SKY_RENDERER = new SkyRenderer();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Aetherlux initializing – preparing the sky...");

        DimensionRenderingRegistry.registerSkyRenderer(World.OVERWORLD, SKY_RENDERER);

        LOGGER.info("Aetherlux sky renderer registered");
    }
}
