package me.katanya04.minespawners.loot.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.katanya04.minespawners.loot.LootRegistration;
import me.katanya04.minespawners.tags.DynamicTags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.context.ContextParameter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

/**
 * A Loot Item Condition that checks if the tool used matches the given predicate and has a specified {@link me.katanya04.minespawners.tags.DynamicTags}
 * @param predicate the predicate to check
 * @param dynamicTag the dynamic tag to check
 */
public record MatchToolWithDynamicTag(Optional<ItemPredicate> predicate, TagKey<Item> dynamicTag) implements LootCondition {
    public static final MapCodec<MatchToolWithDynamicTag> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ItemPredicate.CODEC.optionalFieldOf("predicate").forGetter(MatchToolWithDynamicTag::predicate),
                    TagKey.codec(Registries.ITEM.getKey()).fieldOf("dynamicTag").forGetter(MatchToolWithDynamicTag::dynamicTag)
            ).apply(instance, MatchToolWithDynamicTag::new)
    );

    @Override
    public @NotNull LootConditionType getType() {
        return LootRegistration.matchToolWithDynamicTagType;
    }

    @Override
    public @NotNull Set<ContextParameter<?>> getAllowedParameters() {
        return Set.of(LootContextParameters.TOOL);
    }

    @Override
    public boolean test(LootContext lootContext) {
        ItemStack itemstack = lootContext.get(LootContextParameters.TOOL);
        return itemstack != null && (this.predicate.isEmpty() || this.predicate.get().test(itemstack)) &&
                DynamicTags.isInTag(itemstack, this.dynamicTag);
    }

    public static LootCondition.Builder toolMatches(ItemPredicate.Builder predicate, TagKey<Item> dynamicTag) {
        return () -> new MatchToolWithDynamicTag(Optional.of(predicate.build()), dynamicTag);
    }
}