package me.katanya04.minespawners.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Key-Value element with a tooltip that gets displayed when seen on a screen. Can also have a condition that gets check
 * when setting a new value
 * @param <T> the class of the value (the key is always a String)
 */
public abstract class ConfigValue<T> {
    protected final String key;
    protected T value;
    protected final String tooltip;
    public ConfigValue(String key, T defValue, String tooltip) {
        this.key = key;
        this.value = defValue;
        this.tooltip = tooltip;
    }
    public String getKey() {
        return key;
    }
    abstract T jsonToValue(JsonElement json);
    abstract JsonElement valueToJson();
    abstract T fromString(String value);
    public T getValue() {
        return value;
    }
    public void setValue(@NotNull T value) {
        if (Objects.equals(value.getClass(), this.value.getClass()))
            this.value = value;
    }
    public void setValue(String value) {
        setValue(fromString(value));
    }
    public void setValueFromJson(@NotNull JsonObject json) {
        T newValue = jsonToValue(json.get(this.getKey()));
        setValue(newValue);
    }
    public void setValueToJson(@NotNull JsonObject json) {
        json.add(this.getKey(), valueToJson());
    }
    @Override
    public String toString() {
        return this.getKey() + ": " + this.value;
    }
}
