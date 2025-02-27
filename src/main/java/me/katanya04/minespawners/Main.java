package me.katanya04.minespawners;

import me.katanya04.minespawners.config.SimpleConfig;
import me.katanya04.minespawners.loot.LootFunctions;
import me.katanya04.minespawners.loot.SpawnerDrops;
import net.fabricmc.api.ModInitializer;

/**
 * Entrypoint for the mod
 */
public class Main implements ModInitializer {
    public static final String MOD_ID = "mine_spawners";
    @Override
    public void onInitialize() {
        LootFunctions.register();
        SpawnerDrops.setDrops();
        try {
            SimpleConfig.initializeConfig();
        } catch (Exception ioException) {
            ioException.printStackTrace();
            throw new RuntimeException("IO exception while accessing config file for minespawners mod: " + ioException);
        }
    }
}
