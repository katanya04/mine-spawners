package me.katanya04.minespawners.loot;

import me.katanya04.minespawners.Main;
import me.katanya04.minespawners.loot.conditions.RandomChanceFromProviderCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Registration of the {@link RandomChanceFromProviderCondition} loot condition
 */
public class LootRegistration {
    public static LootConditionType randomChanceFromConfigConditionType;
    public static void register() {
        randomChanceFromConfigConditionType = Registry.register(Registries.LOOT_CONDITION_TYPE,
                Identifier.of(Main.MOD_ID, "random_chance_from_provider"), new LootConditionType(RandomChanceFromProviderCondition.CODEC));
    }
}
