package me.katanya04.minespawners.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import me.katanya04.minespawners.loot.LootRegistration;
import me.katanya04.minespawners.tags.DynamicTags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.registry.Registry;

import java.util.Set;

/**
 * A Loot Item Condition that checks if the tool used matches the given predicate and has a specified {@link me.katanya04.minespawners.tags.DynamicTags}
 */
public class MatchToolWithDynamicTag implements LootCondition {
    final ItemPredicate predicate;
    final TagKey<Item> dynamicTag;

    public MatchToolWithDynamicTag(ItemPredicate predicate, TagKey<Item> dynamicTag) {
        this.predicate = predicate;
        this.dynamicTag = dynamicTag;
    }

    @Override
    public LootConditionType getType() {
        return LootRegistration.matchToolWithDynamicTagType;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.TOOL);
    }

    public boolean test(LootContext lootContext) {
        ItemStack itemstack = lootContext.get(LootContextParameters.TOOL);
        return itemstack != null && (this.predicate.test(itemstack)) && DynamicTags.isInTag(itemstack, this.dynamicTag);
    }

    public static LootCondition.Builder builder(ItemPredicate.Builder predicate, TagKey<Item> dynamicTag) {
        return () -> new MatchToolWithDynamicTag(predicate.build(), dynamicTag);
    }

    public static class Serializer implements JsonSerializer<MatchToolWithDynamicTag> {
        public void toJson(JsonObject jsonObject, MatchToolWithDynamicTag matchToolLootCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("predicate", matchToolLootCondition.predicate.toJson());
            jsonObject.add("dynamicTag", new JsonPrimitive(matchToolLootCondition.dynamicTag.id().toString()));
        }

        public MatchToolWithDynamicTag fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("predicate"));
            Identifier dynamicTagIdentifier = new Identifier(JsonHelper.getString(jsonObject, "dynamicTag"));
            TagKey<Item> dynamicTag = TagKey.of(Registry.ITEM_KEY, dynamicTagIdentifier);
            return new MatchToolWithDynamicTag(itemPredicate, dynamicTag);
        }
    }
}