package me.katanya04.minespawners.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.katanya04.minespawners.loot.LootRegistration;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A LootTable function that sets NBT to a DataComponent of the target
 */
public class SetDataComponentFunction extends ConditionalLootFunction {
    public static final MapCodec<SetDataComponentFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> addConditionsField(instance)
                    .and(StringNbtReader.NBT_COMPOUND_CODEC.fieldOf("tag").forGetter(function -> function.tag))
                    .and(ComponentType.CODEC.fieldOf("dataComponentType").forGetter(function -> function.dataComponentType))
                    .apply(instance, (conditions, tag, dataComponentType) -> new SetDataComponentFunction(conditions, tag, (ComponentType<NbtComponent>) dataComponentType))
    );
    private final NbtCompound tag;
    private final ComponentType<NbtComponent> dataComponentType;

    private SetDataComponentFunction(List<LootCondition> conditions, NbtCompound tag, ComponentType<NbtComponent> dataComponentType) {
        super(conditions);
        this.tag = tag;
        this.dataComponentType = dataComponentType;
    }

    @Override
    public @NotNull LootFunctionType<SetDataComponentFunction> getType() {
        return LootRegistration.setDataComponentFunctionType;
    }

    @Override
    public @NotNull ItemStack process(@NotNull ItemStack item, @NotNull LootContext ignored) {
        NbtComponent.set(this.dataComponentType, item, nbt -> nbt.copyFrom(this.tag));
        return item;
    }

    public static ConditionalLootFunction.Builder<?> builder(NbtCompound nbt, ComponentType<NbtComponent> dataComponentType) {
        return builder(conditions -> new SetDataComponentFunction(conditions, nbt, dataComponentType));
    }
}
