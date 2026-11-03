package dev.hackfire.luxium;

import dev.hackfire.luxium.render.SkyRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuxiumClient implements ClientModInitializer {
    public static final String MOD_ID = "luxium";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final SkyRenderer SKY_RENDERER = new SkyRenderer();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Luxium initializing – preparing the sky...");

        // Register our custom sky for the Overworld
        DimensionRenderingRegistry.registerSkyRenderer(World.OVERWORLD, SKY_RENDERER);

        LOGGER.info("Luxium sky renderer registered");
    }
}
