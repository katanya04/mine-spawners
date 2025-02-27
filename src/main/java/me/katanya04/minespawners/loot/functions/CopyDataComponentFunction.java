package me.katanya04.minespawners.loot.functions;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.katanya04.minespawners.loot.LootFunctions;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider;
import net.minecraft.loot.provider.nbt.LootNbtProvider;
import net.minecraft.loot.provider.nbt.LootNbtProviderTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.context.ContextParameter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * A LootTable function that copies NBT from the source to a DataComponent of the target
 */
public class CopyDataComponentFunction extends ConditionalLootFunction {
    public static final MapCodec<CopyDataComponentFunction> CODEC = RecordCodecBuilder.mapCodec((instance) ->
            addConditionsField(instance).and(
                    instance.group(LootNbtProviderTypes.CODEC.fieldOf("source").forGetter((function) -> function.source),
                    CopyOperation.CODEC.listOf().fieldOf("ops").forGetter((function) -> function.operations))
            ).apply(instance, CopyDataComponentFunction::new));
    private final LootNbtProvider source;
    private final List<CopyOperation> operations;

    CopyDataComponentFunction(List<LootCondition> conditions, LootNbtProvider source, List<CopyOperation> operations) {
        super(conditions);
        this.source = source;
        this.operations = List.copyOf(operations);
    }

    public @NotNull LootFunctionType<CopyDataComponentFunction> getType() {
        return LootFunctions.copyDataComponentFunctionType;
    }

    @Override
    public Set<ContextParameter<?>> getAllowedParameters() {
        return this.source.getRequiredParameters();
    }

    @Override
    public ItemStack process(ItemStack item, LootContext context) {
        NbtElement sourceTag = this.source.getNbt(context);
        if (sourceTag == null)
            return item;
        HashMap<ComponentType<NbtComponent>, NbtCompound> tags = new HashMap<>();
        this.operations.forEach((op) -> {
            if (!tags.containsKey(op.ComponentType))
                tags.put(op.ComponentType, item.getOrDefault(op.ComponentType, NbtComponent.DEFAULT).copyNbt());
            op.apply(tags, sourceTag);
        });
        tags.forEach((type, compoundTag) -> NbtComponent.set(type, item, compoundTag));

        return item;
    }

    public static Builder builder(LootNbtProvider source) {
        return new Builder(source);
    }

    public static Builder builder(LootContext.EntityTarget target) {
        return new Builder(ContextLootNbtProvider.fromTarget(target));
    }

    public static class Builder extends ConditionalLootFunction.Builder<Builder> {
        private final LootNbtProvider source;
        private final List<CopyOperation> ops = Lists.newArrayList();

        Builder(LootNbtProvider source) {
            this.source = source;
        }

        public Builder withOperation(String sourcePath, String targetPath, MergeStrategy operator, ComponentType<NbtComponent> ComponentType) {
            try {
                this.ops.add(new CopyOperation(NbtPathArgumentType.NbtPath.parse(sourcePath),
                        NbtPathArgumentType.NbtPath.parse(targetPath), operator, ComponentType));
                return this;
            } catch (CommandSyntaxException var5) {
                throw new IllegalArgumentException(var5);
            }
        }

        public Builder withOperation(String source, String target, ComponentType<NbtComponent> ComponentType) {
            return this.withOperation(source, target, MergeStrategy.REPLACE, ComponentType);
        }

        @Override
        protected @NotNull Builder getThisBuilder() {
            return this;
        }

        @Override
        public @NotNull LootFunction build() {
            return new CopyDataComponentFunction(this.getConditions(), this.source, this.ops);
        }
    }

    record CopyOperation(NbtPathArgumentType.NbtPath sourcePath, NbtPathArgumentType.NbtPath targetPath, MergeStrategy op, ComponentType<NbtComponent> ComponentType) {
        public static final Codec<CopyOperation> CODEC = RecordCodecBuilder.create((instance) ->
                instance.group(NbtPathArgumentType.NbtPath.CODEC.fieldOf("source").forGetter(CopyOperation::sourcePath),
                                NbtPathArgumentType.NbtPath.CODEC.fieldOf("target").forGetter(CopyOperation::targetPath),
                                MergeStrategy.CODEC.fieldOf("op").forGetter(CopyOperation::op),
                                net.minecraft.component.ComponentType.CODEC.fieldOf("ComponentType").forGetter(CopyOperation::ComponentType))
                .apply(instance, ((nbtPath, nbtPath2, mergeStrategy, ComponentType1) ->
                        new CopyOperation(nbtPath, nbtPath2, mergeStrategy, (ComponentType<NbtComponent>) ComponentType1)))
        );

        public void apply(HashMap<ComponentType<NbtComponent>, NbtCompound> tags, NbtElement sourceTag) {
            try {
                List<NbtElement> sourceNBT = this.sourcePath.get(sourceTag);
                if (!sourceNBT.isEmpty()) {
                    this.op.merge(tags, this.ComponentType, this.targetPath, sourceNBT);
                }
            } catch (CommandSyntaxException ignored) {}
        }
    }

    public enum MergeStrategy implements StringIdentifiable {
        REPLACE("replace") {
            public void merge(HashMap<ComponentType<NbtComponent>, NbtCompound> tags,
                              ComponentType<NbtComponent> ComponentType, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceNbts) throws CommandSyntaxException {
                NbtElement newValue = Iterables.getLast(sourceNbts).copy();
                tags.put(ComponentType, (NbtCompound) newValue);
                targetPath.put(tags.get(ComponentType), newValue);
            }
        },
        APPEND("append") {
            public void merge(HashMap<ComponentType<NbtComponent>, NbtCompound> tags,
                              ComponentType<NbtComponent> ComponentType, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceNbts) throws CommandSyntaxException {
                List<NbtElement> list = targetPath.getOrInit(tags.get(ComponentType), NbtList::new);
                list.forEach((foundNbt) -> {
                    if (foundNbt instanceof NbtList) {
                        sourceNbts.forEach((sourceNbt) -> ((NbtList)foundNbt).add(sourceNbt.copy()));
                    }
                });
            }
        },
        MERGE("merge") {
            public void merge(HashMap<ComponentType<NbtComponent>, NbtCompound> tags,
                              ComponentType<NbtComponent> ComponentType, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceNbts) throws CommandSyntaxException {
                List<NbtElement> list = targetPath.getOrInit(tags.get(ComponentType), NbtCompound::new);
                list.forEach((foundNbt) -> {
                    if (foundNbt instanceof NbtCompound) {
                        sourceNbts.forEach((sourceNbt) -> {
                            if (sourceNbt instanceof NbtCompound) {
                                ((NbtCompound)foundNbt).copyFrom((NbtCompound)sourceNbt);
                            }

                        });
                    }

                });
            }
        };

        public static final Codec<MergeStrategy> CODEC = StringIdentifiable.createCodec(MergeStrategy::values);
        private final String name;

        public abstract void merge(HashMap<ComponentType<NbtComponent>, NbtCompound> tags,
                                   ComponentType<NbtComponent> ComponentType, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceNbts)
                throws CommandSyntaxException;

        MergeStrategy(final String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}