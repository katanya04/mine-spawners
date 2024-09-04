package me.katanya04.minespawners.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Key-Value element with a tooltip that gets displayed when seen on a screen. Can also have a condition that gets check
 * when setting a new value
 * @param <T> the class of the value (the key is always a String)
 */
public abstract class ConfigValue<T> {
    public final String key;
    private T value;
    public Predicate<T> condition;
    public final String tooltip;
    public ConfigValue(String key, T defValue, Predicate<T> condition, String tooltip) {
        this.key = key;
        this.value = defValue;
        this.condition = condition;
        this.tooltip = tooltip;

    }
    abstract T jsonToValue(JsonElement json);
    abstract JsonElement valueToJson();
    abstract T fromString(String value);
    public T getValue() {
        return value;
    }
    public void setValue(T value) {
        if (Objects.equals(value.getClass(), this.value.getClass()) &&
                (condition == null || condition.test(value)))
            this.value = value;
    }
    public void setValue(String value) {
        setValue(fromString(value));
    }
    public void setValueFromJson(JsonObject json) {
        T newValue = jsonToValue(json.get(this.key));
        setValue(newValue);
    }
    public void setValueToJson(JsonObject json) {
        json.add(this.key, valueToJson());
    }
    @Override
    public String toString() {
        return this.key + ": " + this.value;
    }
}
