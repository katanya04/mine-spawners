package me.katanya04.minespawners;

import me.katanya04.minespawners.config.ConfigValue;
import me.katanya04.minespawners.config.ConfigValues;
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
            System.err.println("IO exception while accessing config file for minespawners mod: " + ioException);
            System.err.println("Setting default value of 100% of dropping");
            ConfigValues.DROP_CHANCE.setValue(100.f);
        }
    }
}
