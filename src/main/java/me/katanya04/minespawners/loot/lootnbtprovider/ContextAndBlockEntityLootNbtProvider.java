package me.katanya04.minespawners.loot.lootnbtprovider;

import java.util.Set;

import net.minecraft.world.level.storage.loot.LootContextArg;
import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import me.katanya04.minespawners.loot.LootRegistration;
import net.minecraft.advancements.criterion.NbtPredicate;
import net.minecraft.nbt.Tag;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;
import org.jspecify.annotations.Nullable;

/**
 * A similar class to {@link net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider}, except you can also create one using a
 * {@link LootContext.BlockEntityTarget} instance (you can set the source to be the block entity)
 */
public class ContextAndBlockEntityLootNbtProvider implements NbtProvider {
    private static final Codec<LootContextArg<Tag>> GETTER_CODEC =
            LootContextArg.createArgCodec((argCodecBuilder) ->
                    argCodecBuilder.anyBlockEntity(ContextAndBlockEntityLootNbtProvider.BlockEntitySource::new)
                            .anyEntity(ContextAndBlockEntityLootNbtProvider.EntitySource::new));
    public static final MapCodec<ContextAndBlockEntityLootNbtProvider> MAP_CODEC =
            RecordCodecBuilder.mapCodec((instance) ->
                    instance.group(GETTER_CODEC.fieldOf("target")
                            .forGetter((contextNbtProvider) -> contextNbtProvider.source))
                            .apply(instance, ContextAndBlockEntityLootNbtProvider::new));
    public static final Codec<ContextAndBlockEntityLootNbtProvider> INLINE_CODEC;
    private final LootContextArg<Tag> source;

    private ContextAndBlockEntityLootNbtProvider(LootContextArg<Tag> lootContextArg) {
        this.source = lootContextArg;
    }

    public @NotNull LootNbtProviderType getType() {
        return LootRegistration.ContextAndBlockEntityLootNbtProviderType;
    }

    public @Nullable Tag get(@NotNull LootContext lootContext) {
        return this.source.get(lootContext);
    }

    public @NotNull Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(this.source.contextParam());
    }

    public static NbtProvider forContextEntity(LootContext.EntityTarget entityTarget) {
        return new ContextAndBlockEntityLootNbtProvider(new EntitySource(entityTarget.contextParam()));
    }

    public static NbtProvider forContextBlockEntity(LootContext.BlockEntityTarget target) {
        return new ContextAndBlockEntityLootNbtProvider(new BlockEntitySource(target.contextParam()));
    }

    static {
        INLINE_CODEC = GETTER_CODEC.xmap(ContextAndBlockEntityLootNbtProvider::new,
                (contextNbtProvider) -> contextNbtProvider.source);
    }

    record BlockEntitySource(ContextKey<? extends BlockEntity> contextParam) implements LootContextArg.Getter<BlockEntity, Tag> {
        public Tag get(BlockEntity blockEntity) {
            return blockEntity.saveWithFullMetadata(blockEntity.getLevel().registryAccess());
        }
    }

    record EntitySource(ContextKey<? extends Entity> contextParam) implements LootContextArg.Getter<Entity, Tag> {
        public Tag get(@NotNull Entity entity) {
            return NbtPredicate.getEntityTagToCompare(entity);
        }
    }
}
