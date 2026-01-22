package me.katanya04.minespawners.loot;

import me.katanya04.minespawners.Main;
import me.katanya04.minespawners.loot.functions.CopyDataComponentFunction;
import me.katanya04.minespawners.loot.functions.SetDataComponentFunction;
import me.katanya04.minespawners.loot.conditions.MatchToolWithDynamicTag;
import me.katanya04.minespawners.loot.lootnbtprovider.ContextAndBlockEntityLootNbtProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;

/**
 * Registration of the {@link SetDataComponentFunction} and {@link CopyDataComponentFunction} loot functions, and
 * {@link MatchToolWithDynamicTag} loot condition
 */
public class LootRegistration {
    public static LootItemFunctionType<SetDataComponentFunction> setDataComponentFunctionType;
    public static LootItemFunctionType<CopyDataComponentFunction> copyDataComponentFunctionType;
    public static LootItemConditionType matchToolWithDynamicTagType;
    public static LootNbtProviderType ContextAndBlockEntityLootNbtProviderType;
    public static void register() {
        setDataComponentFunctionType = Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE,
                Identifier.fromNamespaceAndPath(Main.MOD_ID, "set_data_component"), new LootItemFunctionType<>(SetDataComponentFunction.CODEC));
        copyDataComponentFunctionType = Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE,
                Identifier.fromNamespaceAndPath(Main.MOD_ID, "copy_data_component"), new LootItemFunctionType<>(CopyDataComponentFunction.CODEC));
        matchToolWithDynamicTagType = Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE,
                Identifier.fromNamespaceAndPath(Main.MOD_ID, "match_tool_with_dynamic_tag"), new LootItemConditionType(MatchToolWithDynamicTag.CODEC));
        ContextAndBlockEntityLootNbtProviderType = Registry.register(BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE,
                Identifier.fromNamespaceAndPath(Main.MOD_ID, "context_and_block_entity"), new LootNbtProviderType(ContextAndBlockEntityLootNbtProvider.CODEC));
    }
}
