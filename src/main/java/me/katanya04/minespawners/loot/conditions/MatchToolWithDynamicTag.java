package me.katanya04.minespawners.loot.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.katanya04.minespawners.tags.DynamicTags;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.Set;

/**
 * A Loot Item Condition that checks if the tool used matches the given predicate and has a specified {@link me.katanya04.minespawners.tags.DynamicTags}
 * @param predicate the predicate to check
 * @param dynamicTag the dynamic tag to check
 */
public record MatchToolWithDynamicTag(Optional<ItemPredicate> predicate, TagKey<Item> dynamicTag) implements LootItemCondition {
    public static final MapCodec<MatchToolWithDynamicTag> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ItemPredicate.CODEC.optionalFieldOf("predicate").forGetter(MatchToolWithDynamicTag::predicate),
                    TagKey.hashedCodec(BuiltInRegistries.ITEM.key()).fieldOf("dynamicTag").forGetter(MatchToolWithDynamicTag::dynamicTag)
            ).apply(instance, MatchToolWithDynamicTag::new)
    );

    @Override
    public @NotNull Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.TOOL);
    }

    @Override
    public boolean test(LootContext lootContext) {
        ItemInstance iteminstance = lootContext.getOptionalParameter(LootContextParams.TOOL);
        return iteminstance != null && (this.predicate.isEmpty() || this.predicate.get().test(iteminstance)) &&
                DynamicTags.isInTag(iteminstance.typeHolder().value().getDefaultInstance(), this.dynamicTag);
    }

    public static LootItemCondition.Builder toolMatches(ItemPredicate.Builder predicate, TagKey<Item> dynamicTag) {
        return () -> new MatchToolWithDynamicTag(Optional.of(predicate.build()), dynamicTag);
    }

    @Override
    public @NotNull MapCodec<? extends LootItemCondition> codec() {
        return CODEC;
    }
}