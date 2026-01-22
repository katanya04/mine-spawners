package me.katanya04.minespawners.loot;

import me.katanya04.minespawners.config.SimpleConfig;
import me.katanya04.minespawners.loot.conditions.MatchToolWithDynamicTag;
import me.katanya04.minespawners.loot.functions.CopyDataComponentFunction;
import me.katanya04.minespawners.loot.functions.SetDataComponentFunction;
import me.katanya04.minespawners.loot.lootnbtprovider.ContextAndBlockEntityLootNbtProvider;
import me.katanya04.minespawners.tags.DynamicTags;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import java.util.List;

/**
 * Modification of the vanilla spawner loot table
 */
public class SpawnerDrops {
    public static void setDrops() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if ((Blocks.SPAWNER.getLootTable().map(v -> v == key).orElse(false) || Blocks.TRIAL_SPAWNER.getLootTable().map(v -> v == key).orElse(false)) && source.isBuiltin()) {

                var enchantmentsPredicate = EnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(
                        registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH),
                        MinMaxBounds.Ints.atLeast(1))));

                ItemPredicate.Builder pickaxeWithSilktouch = ItemPredicate.Builder.item();
                pickaxeWithSilktouch.withComponents(DataComponentMatchers.Builder.components()
                        .partial(DataComponentPredicates.ENCHANTMENTS, enchantmentsPredicate).build());

                CompoundTag removeDelayAndCoords = new CompoundTag();
                removeDelayAndCoords.put("Delay", ShortTag.valueOf((short) -1));
                removeDelayAndCoords.put("x", IntTag.valueOf(0));
                removeDelayAndCoords.put("y", IntTag.valueOf(0));
                removeDelayAndCoords.put("z", IntTag.valueOf(0));

                BlockEntityType<?> type = Blocks.SPAWNER.getLootTable().get() == key ? BlockEntityType.MOB_SPAWNER : BlockEntityType.TRIAL_SPAWNER;

                LootPool.Builder pool = LootPool.lootPool()
                        .add(LootItem.lootTableItem(Blocks.SPAWNER.getLootTable().get() == key ? Items.SPAWNER : Items.TRIAL_SPAWNER))
                        .apply(CopyDataComponentFunction.builder(ContextAndBlockEntityLootNbtProvider.fromBlockEntityTarget(LootContext.BlockEntityTarget.BLOCK_ENTITY), type)
                                .withOperation("{}", "{}", CopyDataComponentFunction.MergeStrategy.REPLACE, DataComponents.BLOCK_ENTITY_DATA))
                        .apply(SetDataComponentFunction.builder(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(type, removeDelayAndCoords), SetDataComponentFunction.Mode.MERGE))
                        .when(MatchTool.toolMatches(pickaxeWithSilktouch))
                        .when(LootItemRandomChanceCondition.randomChance(SimpleConfig.DROP_CHANCE))
                        .when(InvertedLootItemCondition.invert(MatchToolWithDynamicTag.toolMatches(ItemPredicate.Builder.item(), DynamicTags.BLACKLISTED)));

                // Add the loot pool to the loot table
                tableBuilder.withPool(pool);
            }
        });
    }
}
