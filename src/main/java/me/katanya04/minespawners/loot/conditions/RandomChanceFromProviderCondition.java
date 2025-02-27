package me.katanya04.minespawners.loot.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;

/**
 * A loot condition similar to {@link net.minecraft.loot.condition.RandomChanceLootCondition} that takes in a LootNumberProvider as the provider of the chance instead of a fixed value
 * @param chance the LootNumberProvider
 */
public record RandomChanceFromProviderCondition(LootNumberProvider chance) implements LootCondition {
    public static final Codec<RandomChanceFromProviderCondition> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(LootNumberProviderTypes.CODEC.fieldOf("chance").forGetter(RandomChanceFromProviderCondition::chance))
                    .apply(instance, RandomChanceFromProviderCondition::new)
    );
    @Override
    public LootConditionType getType() {
        return null;
    }

    @Override
    public boolean test(LootContext lootContext) {
        float f = this.chance.nextFloat(lootContext);
        return lootContext.getRandom().nextFloat() < f;
    }

    public static LootCondition.Builder builder(LootNumberProvider lootNumberProvider) {
        return () -> new RandomChanceFromProviderCondition(lootNumberProvider);
    }
}
