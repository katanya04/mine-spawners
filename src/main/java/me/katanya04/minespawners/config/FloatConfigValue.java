package me.katanya04.minespawners.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.function.Predicate;

/**
 * Config value that holds a float as its value
 */
public class FloatConfigValue extends ConfigValue<Float> {
    public FloatConfigValue(String key, Float defValue, Predicate<Float> condition, String tooltip) {
        super(key, defValue, condition, tooltip);
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
}
