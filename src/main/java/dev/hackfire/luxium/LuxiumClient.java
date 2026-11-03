package dev.hackfire.luxium;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuxiumClient implements ClientModInitializer {
    public static final String MOD_ID = "luxium";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Luxium initializing – preparing the sky...");

        // TODO:
        // - Register custom sky renderer
        // - Initialize low-res shadow map system
        // - Set up god ray / volumetric light passes
        // - Load config (quality presets)
    }
}
