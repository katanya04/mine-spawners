package me.katanya04.minespawners.loot.lootnbtprovider;

import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import me.katanya04.minespawners.loot.LootRegistration;
import net.minecraft.advancements.criterion.NbtPredicate;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;

/**
 * A similar class to {@link net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider}, except you can also create one using a
 * {@link LootContext.BlockEntityTarget} instance (you can set the source to be the block entity)
 */
public class ContextAndBlockEntityLootNbtProvider implements NbtProvider {
    private static final ExtraCodecs.LateBoundIdMapper<String, ContextAndBlockEntityLootNbtProvider.Target<?>> TARGETS = new ExtraCodecs.LateBoundIdMapper<>();
    private static final Codec<ContextAndBlockEntityLootNbtProvider.Target<?>> TARGET_CODEC;
    public static final MapCodec<ContextAndBlockEntityLootNbtProvider> CODEC;
    public static final Codec<ContextAndBlockEntityLootNbtProvider> INLINE_CODEC;
    private final ContextAndBlockEntityLootNbtProvider.Target<?> target;

    private ContextAndBlockEntityLootNbtProvider(ContextAndBlockEntityLootNbtProvider.Target<?> target) {
        this.target = target;
    }

    @Override
    public @NotNull LootNbtProviderType getType() {
        return LootRegistration.ContextAndBlockEntityLootNbtProviderType;
    }

    @Nullable
    @Override
    public Tag get(@NotNull LootContext context) {
        return this.target.getNbt(context);
    }

    @Override
    public @NotNull Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(this.target.contextParam());
    }

    public static NbtProvider fromTarget(LootContext.EntityTarget target) {
        return new ContextAndBlockEntityLootNbtProvider(new ContextAndBlockEntityLootNbtProvider.EntityTarget(target.contextParam()));
    }

    public static NbtProvider fromBlockEntityTarget(LootContext.BlockEntityTarget target) {
        return new ContextAndBlockEntityLootNbtProvider(new ContextAndBlockEntityLootNbtProvider.BlockEntityTarget(target.contextParam()));
    }

    static {
        for (LootContext.EntityTarget entityReference : LootContext.EntityTarget.values()) {
            TARGETS.put(entityReference.getSerializedName(), new ContextAndBlockEntityLootNbtProvider.EntityTarget(entityReference.contextParam()));
        }

        for (LootContext.BlockEntityTarget blockEntityReference : LootContext.BlockEntityTarget.values()) {
            TARGETS.put(blockEntityReference.getSerializedName(), new ContextAndBlockEntityLootNbtProvider.BlockEntityTarget(blockEntityReference.contextParam()));
        }

        TARGET_CODEC = TARGETS.codec(Codec.STRING);
        CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(TARGET_CODEC.fieldOf("target").forGetter(provider -> provider.target)).apply(instance, ContextAndBlockEntityLootNbtProvider::new)
        );
        INLINE_CODEC = TARGET_CODEC.xmap(ContextAndBlockEntityLootNbtProvider::new, provider -> provider.target);
    }

    record BlockEntityTarget(ContextKey<? extends BlockEntity> contextParam) implements ContextAndBlockEntityLootNbtProvider.Target<BlockEntity> {
        public Tag getNbt(BlockEntity blockEntity) {
            return blockEntity.saveWithFullMetadata(blockEntity.getLevel().registryAccess());
        }
    }

    record EntityTarget(ContextKey<? extends Entity> contextParam) implements ContextAndBlockEntityLootNbtProvider.Target<Entity> {
        public Tag getNbt(Entity entity) {
            return NbtPredicate.getEntityTagToCompare(entity);
        }
    }

    interface Target<T> {
        ContextKey<? extends T> contextParam();

        @Nullable
        Tag getNbt(T value);

        @Nullable
        default Tag getNbt(LootContext context) {
            T object = context.getOptionalParameter((ContextKey<T>)this.contextParam());
            return object != null ? this.getNbt(object) : null;
        }
    }
}
