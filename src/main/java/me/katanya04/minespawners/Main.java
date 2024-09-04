package me.katanya04.minespawners;

import me.katanya04.minespawners.config.SimpleConfig;
import net.fabricmc.api.ModInitializer;

/**
 * Entrypoint for the mod
 */
public class Main implements ModInitializer {
    @Override
    public void onInitialize() {
        try {
            SimpleConfig.initializeConfig();
        } catch (Exception ioException) {
            ioException.printStackTrace();
            throw new RuntimeException("IO exception while accessing config file for minespawners mod: " + ioException);
        }
    }
}
