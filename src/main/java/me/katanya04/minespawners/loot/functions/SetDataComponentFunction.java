package me.katanya04.minespawners.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.katanya04.minespawners.loot.LootRegistration;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.TypedEntityData;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A LootTable function that sets NBT to a ComponentType<TypedEntityData<?>> of the target
 */
public class SetDataComponentFunction extends ConditionalLootFunction {
    public static final MapCodec<SetDataComponentFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> addConditionsField(instance)
                    .and(ComponentType.CODEC.fieldOf("dataComponentType").forGetter(function -> function.dataComponentType))
                    .and(TypedEntityData.createCodec(Registries.BLOCK_ENTITY_TYPE.getCodec()).fieldOf("data").forGetter(function -> function.data))
                    .and(Mode.CODEC.fieldOf("mode").forGetter(function -> function.mode))
                    .apply(instance, (conditions, componentType, typedEntityData, mode) ->
                            new SetDataComponentFunction(conditions, (ComponentType<TypedEntityData<BlockEntityType<?>>>) componentType, typedEntityData, mode))
    );
    private final ComponentType<TypedEntityData<BlockEntityType<?>>> dataComponentType;
    private final TypedEntityData<BlockEntityType<?>> data;
    public enum Mode implements StringIdentifiable {
        REPLACE("replace"),
        APPEND("append"),
        MERGE("merge");
        public static final Codec<Mode> CODEC = StringIdentifiable.createCodec(Mode::values);
        private final String name;
        Mode(String name) {
            this.name = name;
        }
        @Override
        public String asString() {
            return this.name;
        }
    }
    private final Mode mode;

    private SetDataComponentFunction(List<LootCondition> conditions, ComponentType<TypedEntityData<BlockEntityType<?>>> dataComponentType, TypedEntityData<BlockEntityType<?>> data, Mode mode) {
        super(conditions);
        this.dataComponentType = dataComponentType;
        this.data = data;
        this.mode = mode;
    }

    @Override
    public @NotNull LootFunctionType<SetDataComponentFunction> getType() {
        return LootRegistration.setDataComponentFunctionType;
    }

    @Override
    public @NotNull ItemStack process(@NotNull ItemStack item, @NotNull LootContext ignored) {
        TypedEntityData<BlockEntityType<?>> data;
        if (this.mode == Mode.REPLACE) {
            data = this.data;
        } else {
            TypedEntityData<BlockEntityType<?>> currentData = item.get(dataComponentType);
            if (currentData == null) {
                data = this.data;
            } else {
                Map<String, NbtElement> currentEntries = currentData.copyNbtWithoutId().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                Map<String, NbtElement> newEntries = this.data.copyNbtWithoutId().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                Map<String, NbtElement> entries;
                if (this.mode == Mode.APPEND) {
                    newEntries.putAll(currentEntries);
                    entries = newEntries;
                } else {
                    currentEntries.putAll(newEntries);
                    entries = currentEntries;
                }
                NbtCompound newData = new NbtCompound();
                for (Map.Entry<String, NbtElement> entry : entries.entrySet()) {
                    newData.put(entry.getKey(), entry.getValue());
                }
                data = TypedEntityData.create(this.data.getType(), newData);
            }
        }
        item.set(dataComponentType, data);
        return item;
    }

    public static ConditionalLootFunction.Builder<?> builder(ComponentType<TypedEntityData<BlockEntityType<?>>> dataComponentType, TypedEntityData<BlockEntityType<?>> data, Mode mode) {
        return builder(conditions -> new SetDataComponentFunction(conditions, dataComponentType, data, mode));
    }
}
