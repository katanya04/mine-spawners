package me.katanya04.minespawners.loot;

import me.katanya04.minespawners.config.SimpleConfig;
import me.katanya04.minespawners.loot.conditions.RandomChanceFromProviderCondition;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtShort;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.tag.ItemTags;

/**
 * Modification of the vanilla spawner loot table
 */
public class SpawnerDrops {
    public static void setDrops() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (Blocks.SPAWNER.getLootTableId().equals(id) && source.isBuiltin()) {
                ItemPredicate.Builder pickaxeWithSilktouch = ItemPredicate.Builder.create();
                pickaxeWithSilktouch.tag(ItemTags.PICKAXES);
                pickaxeWithSilktouch.enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1)));

                NbtCompound blockEntityTag = new NbtCompound();
                NbtCompound removeDelayAndCoords = new NbtCompound();
                removeDelayAndCoords.put("Delay", NbtShort.of((short) -1));
                removeDelayAndCoords.put("x", NbtInt.of(0));
                removeDelayAndCoords.put("y", NbtInt.of(0));
                removeDelayAndCoords.put("z", NbtInt.of(0));
                blockEntityTag.put("BlockEntityTag", removeDelayAndCoords);

                LootPool.Builder pool = LootPool.builder()
                        .with(ItemEntry.builder(Items.SPAWNER))
                        .apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                                .withOperation("{}", "BlockEntityTag", CopyNbtLootFunction.Operator.REPLACE))
                        .apply(SetNbtLootFunction.builder(blockEntityTag))
                        .conditionally(MatchToolLootCondition.builder(pickaxeWithSilktouch))
                        .conditionally(RandomChanceFromProviderCondition.builder(SimpleConfig.DROP_CHANCE));

                // Add the loot pool to the loot table
                tableBuilder.pool(pool);
            }
        });
    }
}
