package me.katanya04.minespawners.config.valuetypes;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.Codec;
import me.katanya04.minespawners.Main;
import me.katanya04.minespawners.config.SimpleConfig;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.registry.Registry;

/**
 * Config value that holds a float as its value
 */
public class FloatConfigValue extends ConfigValue<Float> implements LootNumberProvider {
    private static final LootNumberProviderType LOOT_NUMBER_PROVIDER_TYPE =
        Registry.register(Registry.LOOT_NUMBER_PROVIDER_TYPE, Identifier.tryParse(Main.MOD_ID + ":from_config"),
                new LootNumberProviderType(new Serializer()));
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
        return Codec.FLOAT;
    }

    public static class Serializer implements JsonSerializer<FloatConfigValue> {
        public void toJson(JsonObject jsonObject, FloatConfigValue floatConfigValue, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("key", floatConfigValue.key);
        }

        public FloatConfigValue fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return (FloatConfigValue) SimpleConfig.values.get(JsonHelper.getString(jsonObject, "key"));
        }
    }
}
