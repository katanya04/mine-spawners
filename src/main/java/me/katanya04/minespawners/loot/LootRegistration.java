package me.katanya04.minespawners.loot;

import me.katanya04.minespawners.Main;
import me.katanya04.minespawners.loot.conditions.MatchToolWithDynamicTag;
import me.katanya04.minespawners.loot.conditions.RandomChanceFromProviderCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Registration of the {@link MatchToolWithDynamicTag} loot condition
 */
public class LootRegistration {
    public static LootConditionType matchToolWithDynamicTagType;
    public static LootConditionType randomChanceFromConfigConditionType;
    public static void register() {
        matchToolWithDynamicTagType = Registry.register(Registry.LOOT_CONDITION_TYPE,
                Identifier.tryParse(Main.MOD_ID + ":match_tool_with_dynamic_tag"),
                new LootConditionType(new MatchToolWithDynamicTag.Serializer()));
        randomChanceFromConfigConditionType = Registry.register(Registry.LOOT_CONDITION_TYPE,
                Identifier.tryParse(Main.MOD_ID + ":random_chance_from_provider"),
                new LootConditionType(new RandomChanceFromProviderCondition.Serializer()));
    }
}
