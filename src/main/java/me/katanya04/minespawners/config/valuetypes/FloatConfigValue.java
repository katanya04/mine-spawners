package me.katanya04.minespawners.config.valuetypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.katanya04.minespawners.config.SimpleConfig;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Config value that holds a float as its value
 */
public class FloatConfigValue extends ConfigValue<Float> implements NumberProvider {
    private static final MapCodec<FloatConfigValue> LOOT_CODEC = RecordCodecBuilder.mapCodec((instance) ->
            instance.group(Codec.STRING.fieldOf("key").forGetter(ConfigValue::getKey))
                    .apply(instance, path -> (FloatConfigValue) SimpleConfig.values.get(path)));
    public FloatConfigValue(String key, Float defValue, float min, float max) {
        super(key, defValue, (value) -> value >= min && value <= max);
    }

    public void setValue(double value) {
        setValue(Float.valueOf((float) value));
    }

    @Override
    public float getFloat(@NotNull LootContext context) {
        return this.getValue();
    }

    @Override
    public MapCodec<? extends FloatConfigValue> codec() {
        return LOOT_CODEC;
    }

    @Override
    public Codec<Float> getCodec() {
        return Codec.FLOAT;
    }
}
