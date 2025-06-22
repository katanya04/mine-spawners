package me.katanya04.minespawners.config.valuetypes;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

/**
 * Config value that holds a list as its value
 * @param <T> the type of values this list holds
 */
public abstract class ListConfigValue<T> extends ConfigValue<List<T>> {
    protected final @Nullable Predicate<T> entryChecker;
    public ListConfigValue(String key, List<T> defValue) {
        this(key, defValue, null);
    }
    public ListConfigValue(String key, List<T> defValue, @Nullable Predicate<T> entryChecker) {
        this(key, defValue, entryChecker, null);
    }
    public ListConfigValue(String key, List<T> defValue, @Nullable Predicate<T> entryChecker, @Nullable Predicate<List<T>> checker) {
        super(key, defValue, checker);
        this.entryChecker = entryChecker;
    }

    public void addValue(T value) {
        if (this.entryChecker == null || this.entryChecker.test(value))
            this.value.add(value);
    }

    public boolean removeValue(T value) {
        return this.value.remove(value);
    }

    public boolean contains(T value) {
        return this.value.contains(value);
    }
}
