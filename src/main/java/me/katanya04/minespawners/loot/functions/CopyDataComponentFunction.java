package me.katanya04.minespawners.loot.functions;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProviders;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * A LootTable function that copies NBT from the source to DataComponents of the target
 */
public class CopyDataComponentFunction extends LootItemConditionalFunction {
    public static final MapCodec<CopyDataComponentFunction> CODEC = RecordCodecBuilder.mapCodec((instance) ->
            commonFields(instance).and(
                    instance.group(
                            NbtProviders.CODEC.fieldOf("source").forGetter((function) -> function.source),
                            BuiltInRegistries.BLOCK_ENTITY_TYPE.byNameCodec().fieldOf("blockEntityType").forGetter(function -> function.blockEntityType),
                            CopyOperation.CODEC.listOf().fieldOf("ops").forGetter((function) -> function.operations)
                    )
            ).apply(instance, CopyDataComponentFunction::new));
    private final NbtProvider source;
    private final BlockEntityType<?> blockEntityType;
    private final List<CopyOperation> operations;

    CopyDataComponentFunction(List<LootItemCondition> conditions, NbtProvider source, BlockEntityType<?> blockEntityType, List<CopyOperation> operations) {
        super(conditions);
        this.source = source;
        this.blockEntityType = blockEntityType;
        this.operations = List.copyOf(operations);
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return this.source.getReferencedContextParams();
    }

    @Override
    public ItemStack run(ItemStack item, LootContext context) {
        Tag sourceTag = this.source.get(context);
        if (sourceTag == null)
            return item;
        HashMap<DataComponentType<TypedEntityData<BlockEntityType<?>>>, TypedEntityData<BlockEntityType<?>>> tags = new HashMap<>();
        this.operations.forEach((op) -> {
            if (!tags.containsKey(op.ComponentType))
                tags.put(op.ComponentType, (TypedEntityData<BlockEntityType<?>>) item.getOrDefault(op.ComponentType, TypedEntityData.of(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY.copyTag())));
            op.apply(tags, sourceTag, blockEntityType);
        });
        tags.forEach(item::set);

        return item;
    }

    public static Builder builder(NbtProvider source, BlockEntityType<?> blockEntityType) {
        return new Builder(source, blockEntityType);
    }

    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final NbtProvider source;
        private final BlockEntityType<?> blockEntityType;
        private final List<CopyOperation> ops = Lists.newArrayList();

        Builder(NbtProvider source, BlockEntityType<?> blockEntityType) {
            this.source = source;
            this.blockEntityType = blockEntityType;
        }

        public Builder withOperation(String sourcePath, String targetPath, MergeStrategy operator, DataComponentType<TypedEntityData<BlockEntityType<?>>> ComponentType) {
            try {
                this.ops.add(new CopyOperation(NbtPathArgument.NbtPath.of(sourcePath),
                        NbtPathArgument.NbtPath.of(targetPath), operator, ComponentType));
                return this;
            } catch (CommandSyntaxException var5) {
                throw new IllegalArgumentException(var5);
            }
        }

        public Builder withOperation(String source, String target, DataComponentType<TypedEntityData<BlockEntityType<?>>> ComponentType) {
            return this.withOperation(source, target, MergeStrategy.REPLACE, ComponentType);
        }

        @Override
        protected @NotNull Builder getThis() {
            return this;
        }

        @Override
        public @NotNull LootItemFunction build() {
            return new CopyDataComponentFunction(this.getConditions(), this.source, this.blockEntityType, this.ops);
        }
    }

    record CopyOperation(NbtPathArgument.NbtPath sourcePath, NbtPathArgument.NbtPath targetPath, MergeStrategy op, DataComponentType<TypedEntityData<BlockEntityType<?>>> ComponentType) {
        public static final Codec<CopyOperation> CODEC = RecordCodecBuilder.create((instance) ->
                instance.group(NbtPathArgument.NbtPath.CODEC.fieldOf("source").forGetter(CopyOperation::sourcePath),
                                NbtPathArgument.NbtPath.CODEC.fieldOf("target").forGetter(CopyOperation::targetPath),
                                MergeStrategy.CODEC.fieldOf("op").forGetter(CopyOperation::op),
                                net.minecraft.core.component.DataComponentType.CODEC.fieldOf("ComponentType").forGetter(CopyOperation::ComponentType))
                .apply(instance, ((nbtPath, nbtPath2, mergeStrategy, ComponentType1) ->
                        new CopyOperation(nbtPath, nbtPath2, mergeStrategy, (DataComponentType<TypedEntityData<BlockEntityType<?>>>) ComponentType1)))
        );

        public void apply(HashMap<DataComponentType<TypedEntityData<BlockEntityType<?>>>, TypedEntityData<BlockEntityType<?>>> tags, Tag sourceTag, BlockEntityType<?> blockEntityType) {
            try {
                List<Tag> sourceNBT = this.sourcePath.get(sourceTag);
                if (!sourceNBT.isEmpty()) {
                    this.op.merge(tags, this.ComponentType, this.targetPath, sourceNBT, blockEntityType);
                }
            } catch (CommandSyntaxException ignored) {}
        }
    }

    public enum MergeStrategy implements StringRepresentable {
        REPLACE("replace") {
            public void merge(HashMap<DataComponentType<TypedEntityData<BlockEntityType<?>>>, TypedEntityData<BlockEntityType<?>>> tags,
                              DataComponentType<TypedEntityData<BlockEntityType<?>>> ComponentType, NbtPathArgument.NbtPath targetPath, List<Tag> sourceNbts,
                              BlockEntityType<?> blockEntityType) throws CommandSyntaxException {
                Tag newValue = Iterables.getLast(sourceNbts).copy();
                tags.put(ComponentType, TypedEntityData.of(blockEntityType, newValue.asCompound().get()));
                targetPath.set(tags.get(ComponentType).copyTagWithoutId(), newValue);
            }
        },
        APPEND("append") {
            public void merge(HashMap<DataComponentType<TypedEntityData<BlockEntityType<?>>>, TypedEntityData<BlockEntityType<?>>> tags,
                              DataComponentType<TypedEntityData<BlockEntityType<?>>> ComponentType, NbtPathArgument.NbtPath targetPath, List<Tag> sourceNbts,
                              BlockEntityType<?> blockEntityType) throws CommandSyntaxException {
                List<Tag> list = targetPath.getOrCreate(tags.get(ComponentType).copyTagWithoutId(), ListTag::new);
                list.forEach((foundNbt) -> {
                    if (foundNbt instanceof ListTag) {
                        sourceNbts.forEach((sourceNbt) -> ((ListTag)foundNbt).add(sourceNbt.copy()));
                    }
                });
            }
        },
        MERGE("merge") {
            public void merge(HashMap<DataComponentType<TypedEntityData<BlockEntityType<?>>>, TypedEntityData<BlockEntityType<?>>> tags,
                              DataComponentType<TypedEntityData<BlockEntityType<?>>> ComponentType, NbtPathArgument.NbtPath targetPath, List<Tag> sourceNbts,
                              BlockEntityType<?> blockEntityType) throws CommandSyntaxException {
                List<Tag> list = targetPath.getOrCreate(tags.get(ComponentType).copyTagWithoutId(), CompoundTag::new);
                list.forEach((foundNbt) -> {
                    if (foundNbt instanceof CompoundTag) {
                        sourceNbts.forEach((sourceNbt) -> {
                            if (sourceNbt instanceof CompoundTag) {
                                ((CompoundTag)foundNbt).merge((CompoundTag)sourceNbt);
                            }
                        });
                    }
                });
            }
        };

        public static final Codec<MergeStrategy> CODEC = StringRepresentable.fromEnum(MergeStrategy::values);
        private final String name;

        public abstract void merge(HashMap<DataComponentType<TypedEntityData<BlockEntityType<?>>>, TypedEntityData<BlockEntityType<?>>> tags,
                                   DataComponentType<TypedEntityData<BlockEntityType<?>>> ComponentType, NbtPathArgument.NbtPath targetPath, List<Tag> sourceNbts,
                                   BlockEntityType<?> blockEntityType)
                throws CommandSyntaxException;

        MergeStrategy(final String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    @Override
    public @NotNull MapCodec<? extends CopyDataComponentFunction> codec() {
        return CODEC;
    }
}