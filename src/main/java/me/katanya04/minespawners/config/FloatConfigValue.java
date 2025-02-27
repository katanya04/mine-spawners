package me.katanya04.minespawners.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.katanya04.minespawners.Main;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Range;
import org.jetbrains.annotations.NotNull;

/**
 * Config value that holds a float as its value
 */
public class FloatConfigValue extends ConfigValue<Float> implements LootNumberProvider {
    private static final Codec<FloatConfigValue> LOOT_CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(Codec.STRING.fieldOf("key").forGetter(ConfigValue::getKey))
                    .apply(instance, path -> (FloatConfigValue) SimpleConfig.values.get(path)));
    private static final LootNumberProviderType LOOT_NUMBER_PROVIDER_TYPE =
            Registry.register(Registries.LOOT_NUMBER_PROVIDER_TYPE, Identifier.of(Main.MOD_ID, "from_config"),
                    new LootNumberProviderType(LOOT_CODEC));

    protected final Range<Float> range;
    public FloatConfigValue(String key, Float defValue, String tooltip, float min, float max) {
        super(key, defValue, tooltip);
        this.range = new Range<>(min, max);
    }

    @Override
    public void setValue(@NotNull Float value) {
        if (range.contains(value))
            this.value = value;
    }

    @Override
    Float jsonToValue(JsonElement json) {
        return json.getAsFloat();
    }

    @Override
    JsonElement valueToJson() {
        return new JsonPrimitive(this.getValue());
    }

    @Override
    Float fromString(String value) {
        try {
            return Float.parseFloat(value);
        } catch (Exception ex) {
            return this.getValue();
        }
    }

    @Override
    public float nextFloat(LootContext context) {
        return this.getValue();
    }

    @Override
    public LootNumberProviderType getType() {
        return LOOT_NUMBER_PROVIDER_TYPE;
    }
}