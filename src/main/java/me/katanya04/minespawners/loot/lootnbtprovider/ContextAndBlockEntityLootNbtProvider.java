package me.katanya04.minespawners.loot.lootnbtprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;

import me.katanya04.minespawners.loot.LootRegistration;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.nbt.LootNbtProvider;
import net.minecraft.loot.provider.nbt.LootNbtProviderType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

/**
 * A similar class to {@link net.minecraft.loot.provider.nbt.ContextLootNbtProvider}, except you can also create one using a
 * {@link LootContext.BlockEntityReference} instance (you can set the source to be the block entity)
 */
public class ContextAndBlockEntityLootNbtProvider implements LootNbtProvider {
    private static final Codecs.IdMapper<String, ContextAndBlockEntityLootNbtProvider.Target<?>> TARGETS = new Codecs.IdMapper<>();
    private static final Codec<ContextAndBlockEntityLootNbtProvider.Target<?>> TARGET_CODEC;
    public static final MapCodec<ContextAndBlockEntityLootNbtProvider> CODEC;
    public static final Codec<ContextAndBlockEntityLootNbtProvider> INLINE_CODEC;
    private final ContextAndBlockEntityLootNbtProvider.Target<?> target;

    private ContextAndBlockEntityLootNbtProvider(ContextAndBlockEntityLootNbtProvider.Target<?> target) {
        this.target = target;
    }

    @Override
    public LootNbtProviderType getType() {
        return LootRegistration.ContextAndBlockEntityLootNbtProviderType;
    }

    @Nullable
    @Override
    public NbtElement getNbt(LootContext context) {
        return this.target.getNbt(context);
    }

    @Override
    public Set<ContextParameter<?>> getRequiredParameters() {
        return Set.of(this.target.contextParam());
    }

    public static LootNbtProvider fromTarget(LootContext.EntityReference target) {
        return new ContextAndBlockEntityLootNbtProvider(new ContextAndBlockEntityLootNbtProvider.EntityTarget(target.getParameter()));
    }

    public static LootNbtProvider fromBlockEntityTarget(LootContext.BlockEntityReference target) {
        return new ContextAndBlockEntityLootNbtProvider(new ContextAndBlockEntityLootNbtProvider.BlockEntityTarget(target.getParameter()));
    }

    static {
        for (LootContext.EntityReference entityReference : LootContext.EntityReference.values()) {
            TARGETS.put(entityReference.asString(), new ContextAndBlockEntityLootNbtProvider.EntityTarget(entityReference.getParameter()));
        }

        for (LootContext.BlockEntityReference blockEntityReference : LootContext.BlockEntityReference.values()) {
            TARGETS.put(blockEntityReference.asString(), new ContextAndBlockEntityLootNbtProvider.BlockEntityTarget(blockEntityReference.getParameter()));
        }

        TARGET_CODEC = TARGETS.getCodec(Codec.STRING);
        CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(TARGET_CODEC.fieldOf("target").forGetter(provider -> provider.target)).apply(instance, ContextAndBlockEntityLootNbtProvider::new)
        );
        INLINE_CODEC = TARGET_CODEC.xmap(ContextAndBlockEntityLootNbtProvider::new, provider -> provider.target);
    }

    record BlockEntityTarget(ContextParameter<? extends BlockEntity> contextParam) implements ContextAndBlockEntityLootNbtProvider.Target<BlockEntity> {
        public NbtElement getNbt(BlockEntity blockEntity) {
            return blockEntity.createNbtWithIdentifyingData(blockEntity.getWorld().getRegistryManager());
        }
    }

    record EntityTarget(ContextParameter<? extends Entity> contextParam) implements ContextAndBlockEntityLootNbtProvider.Target<Entity> {
        public NbtElement getNbt(Entity entity) {
            return NbtPredicate.entityToNbt(entity);
        }
    }

    interface Target<T> {
        ContextParameter<? extends T> contextParam();

        @Nullable
        NbtElement getNbt(T value);

        @Nullable
        default NbtElement getNbt(LootContext context) {
            T object = context.get((ContextParameter<T>)this.contextParam());
            return object != null ? this.getNbt(object) : null;
        }
    }
}
