package me.katanya04.minespawners.config.valuetypes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.katanya04.minespawners.Main;
import me.katanya04.minespawners.config.SimpleConfig;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

/**
 * Config value that holds a float as its value
 */
public class FloatConfigValue extends ConfigValue<Float> implements LootNumberProvider {
    private static final MapCodec<FloatConfigValue> LOOT_CODEC = RecordCodecBuilder.mapCodec((instance) ->
            instance.group(Codec.STRING.fieldOf("key").forGetter(ConfigValue::getKey))
                    .apply(instance, path -> (FloatConfigValue) SimpleConfig.values.get(path)));
    private static final LootNumberProviderType LOOT_NUMBER_PROVIDER_TYPE =
        Registry.register(Registries.LOOT_NUMBER_PROVIDER_TYPE, Identifier.of(Main.MOD_ID, "from_config"),
                new LootNumberProviderType(LOOT_CODEC));
    public FloatConfigValue(String key, Float defValue, float min, float max) {
        super(key, defValue, (value) -> value >= min && value <= max);
    }

    public void setValue(double value) {
        setValue(Float.valueOf((float) value));
    }

    @Override
    public float nextFloat(LootContext context) {
        return this.getValue();
    }

    @Override
    public LootNumberProviderType getType() {
        return LOOT_NUMBER_PROVIDER_TYPE;
    }

    @Override
    public Codec<Float> getCodec() {
        return Codecs.POSITIVE_FLOAT;
    }
}
