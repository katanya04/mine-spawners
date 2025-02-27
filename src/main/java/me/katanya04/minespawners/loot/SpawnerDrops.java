package me.katanya04.minespawners.loot;

import me.katanya04.minespawners.config.SimpleConfig;
import me.katanya04.minespawners.loot.functions.CopyDataComponentFunction;
import me.katanya04.minespawners.loot.functions.SetDataComponentFunction;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtShort;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.EnchantmentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.predicate.item.ItemSubPredicateTypes;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.ItemTags;

import java.util.List;

/**
 * Modification of the vanilla spawner loot table
 */
public class SpawnerDrops {
    public static void setDrops() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if ((Blocks.SPAWNER.getLootTableKey().get() == key || Blocks.TRIAL_SPAWNER.getLootTableKey().get() == key) && source.isBuiltin()) {

                var enchantmentsPredicate = EnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(
                        registries.getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH),
                        NumberRange.IntRange.atLeast(1))));

                ItemPredicate.Builder pickaxeWithSilktouch = ItemPredicate.Builder.create();
                pickaxeWithSilktouch.tag(registries.getOrThrow(RegistryKeys.ITEM), ItemTags.PICKAXES);
                pickaxeWithSilktouch.subPredicate(ItemSubPredicateTypes.ENCHANTMENTS, enchantmentsPredicate);

                NbtCompound removeDelayAndCoords = new NbtCompound();
                removeDelayAndCoords.put("Delay", NbtShort.of((short) -1));
                removeDelayAndCoords.put("x", NbtInt.of(0));
                removeDelayAndCoords.put("y", NbtInt.of(0));
                removeDelayAndCoords.put("z", NbtInt.of(0));

                LootPool.Builder pool = LootPool.builder()
                        .with(ItemEntry.builder(Blocks.SPAWNER.getLootTableKey().get() == key ? Items.SPAWNER : Items.TRIAL_SPAWNER))
                        .apply(CopyDataComponentFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                                .withOperation("{}", "{}", CopyDataComponentFunction.MergeStrategy.REPLACE, DataComponentTypes.BLOCK_ENTITY_DATA))
                        .apply(SetDataComponentFunction.builder(removeDelayAndCoords, DataComponentTypes.BLOCK_ENTITY_DATA))
                        .conditionally(MatchToolLootCondition.builder(pickaxeWithSilktouch))
                        .conditionally(RandomChanceLootCondition.builder(SimpleConfig.DROP_CHANCE));

                // Add the loot pool to the loot table
                tableBuilder.pool(pool);
            }
        });
    }
}
