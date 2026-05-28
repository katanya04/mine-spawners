package me.katanya04.minespawners;

import me.katanya04.minespawners.config.SimpleConfig;
import me.katanya04.minespawners.loot.SpawnerDrops;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.impl.util.log.LogCategory;

/**
 * Entrypoint for the mod
 */
public class Main implements ModInitializer {
    public static final String MOD_ID = "mine_spawners";
    public static final LogCategory logCategory = LogCategory.create("Mine Spawners");
    @Override
    public void onInitialize() {
        SpawnerDrops.setDrops();
        SimpleConfig.initializeConfig();
    }
}
