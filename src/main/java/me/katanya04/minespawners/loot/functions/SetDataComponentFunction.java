package me.katanya04.minespawners.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.katanya04.minespawners.loot.LootRegistration;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A LootTable function that sets NBT to a ComponentType<TypedEntityData<?>> of the target
 */
public class SetDataComponentFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetDataComponentFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> commonFields(instance)
                    .and(DataComponentType.CODEC.fieldOf("dataComponentType").forGetter(function -> function.dataComponentType))
                    .and(TypedEntityData.codec(BuiltInRegistries.BLOCK_ENTITY_TYPE.byNameCodec()).fieldOf("data").forGetter(function -> function.data))
                    .and(Mode.CODEC.fieldOf("mode").forGetter(function -> function.mode))
                    .apply(instance, (conditions, componentType, typedEntityData, mode) ->
                            new SetDataComponentFunction(conditions, (DataComponentType<TypedEntityData<BlockEntityType<?>>>) componentType, typedEntityData, mode))
    );
    private final DataComponentType<TypedEntityData<BlockEntityType<?>>> dataComponentType;
    private final TypedEntityData<BlockEntityType<?>> data;
    public enum Mode implements StringRepresentable {
        REPLACE("replace"),
        APPEND("append"),
        MERGE("merge");
        public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
        private final String name;
        Mode(String name) {
            this.name = name;
        }
        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
    private final Mode mode;

    private SetDataComponentFunction(List<LootItemCondition> conditions, DataComponentType<TypedEntityData<BlockEntityType<?>>> dataComponentType, TypedEntityData<BlockEntityType<?>> data, Mode mode) {
        super(conditions);
        this.dataComponentType = dataComponentType;
        this.data = data;
        this.mode = mode;
    }

    @Override
    public @NotNull LootItemFunctionType<SetDataComponentFunction> getType() {
        return LootRegistration.setDataComponentFunctionType;
    }

    @Override
    public @NotNull ItemStack run(@NotNull ItemStack item, @NotNull LootContext ignored) {
        TypedEntityData<BlockEntityType<?>> data;
        if (this.mode == Mode.REPLACE) {
            data = this.data;
        } else {
            TypedEntityData<BlockEntityType<?>> currentData = item.get(dataComponentType);
            if (currentData == null) {
                data = this.data;
            } else {
                Map<String, Tag> currentEntries = currentData.copyTagWithoutId().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                Map<String, Tag> newEntries = this.data.copyTagWithoutId().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                Map<String, Tag> entries;
                if (this.mode == Mode.APPEND) {
                    newEntries.putAll(currentEntries);
                    entries = newEntries;
                } else {
                    currentEntries.putAll(newEntries);
                    entries = currentEntries;
                }
                CompoundTag newData = new CompoundTag();
                for (Map.Entry<String, Tag> entry : entries.entrySet()) {
                    newData.put(entry.getKey(), entry.getValue());
                }
                data = TypedEntityData.of(this.data.type(), newData);
            }
        }
        item.set(dataComponentType, data);
        return item;
    }

    public static LootItemConditionalFunction.Builder<?> builder(DataComponentType<TypedEntityData<BlockEntityType<?>>> dataComponentType, TypedEntityData<BlockEntityType<?>> data, Mode mode) {
        return simpleBuilder(conditions -> new SetDataComponentFunction(conditions, dataComponentType, data, mode));
    }
}
