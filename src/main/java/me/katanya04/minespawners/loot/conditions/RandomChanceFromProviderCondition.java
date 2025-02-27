package me.katanya04.minespawners.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

/**
 * A loot condition similar to {@link net.minecraft.loot.condition.RandomChanceLootCondition} that takes in a LootNumberProvider as the provider of the chance instead of a fixed value
 * @param chance the LootNumberProvider
 */
public record RandomChanceFromProviderCondition(LootNumberProvider chance) implements LootCondition {
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

    public static class Serializer implements JsonSerializer<RandomChanceFromProviderCondition> {
        public void toJson(JsonObject jsonObject, RandomChanceFromProviderCondition randomChanceFromProviderCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("chance", jsonSerializationContext.serialize(randomChanceFromProviderCondition.chance));
        }

        public RandomChanceFromProviderCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LootNumberProvider lootNumberProvider = JsonHelper.deserialize(jsonObject, "chance", jsonDeserializationContext, LootNumberProvider.class);
            return new RandomChanceFromProviderCondition(lootNumberProvider);
        }
    }
}
