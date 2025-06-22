package me.katanya04.minespawners.config.valuetypes;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Key-Value element with a tooltip that gets displayed when seen on a screen. Can also have a condition that gets check
 * when setting a new value
 * @param <T> the class of the value (the key is always a String)
 */
public abstract class ConfigValue<T> {
    protected final String key;
    protected T value;
    protected final @Nullable Predicate<T> checker;
    public ConfigValue(String key, T defValue) {
        this(key, defValue, null);
    }
    public ConfigValue(String key, T defValue, @Nullable Predicate<T> checker) {
        this.key = key;
        this.value = defValue;
        this.checker = checker;
    }
    public String getKey() {
        return key;
    }
    public T getValue() {
        return value;
    }
    public void setValue(@NotNull T value) {
        if (checker == null || checker.test(value))
            this.value = value;
    }
    public void setValueFromJson(@NotNull JsonObject json) {
        T newValue = getCodec().parse(JsonOps.INSTANCE, json.get(this.getKey())).getOrThrow(false, str -> {
            throw new RuntimeException(str);
        });
        setValue(newValue);
    }
    public void setValueToJson(@NotNull JsonObject json) {
        json.add(this.getKey(), getCodec().encodeStart(JsonOps.INSTANCE, this.value).getOrThrow(false, str -> {
            throw new RuntimeException(str);
        }));
    }
    @Override
    public String toString() {
        return this.getKey() + ": " + this.value;
    }
    public abstract Codec<T> getCodec();
}
