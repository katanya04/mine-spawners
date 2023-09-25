package me.marco2124.minespawners;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

public class Mine_spawners implements ModInitializer {
    public static final String MOD_ID = "mine-spawners";
    private static final String path = "spawner";
    private static final Identifier SPAWNER_LOOT_TABLE_ID = Blocks.SPAWNER.getLootTableId();
    @Override
    public void onInitialize() {
        LootTableEvents.REPLACE.register((resourceManager, lootManager, id, tableBuilder, source) ->
                source.isBuiltin() && SPAWNER_LOOT_TABLE_ID.equals(id) ?
                lootManager.getLootTable(new Identifier(MOD_ID, path)) :
                null);
    }
}
