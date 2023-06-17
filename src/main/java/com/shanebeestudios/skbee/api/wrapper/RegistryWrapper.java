package com.shanebeestudios.skbee.api.wrapper;

import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.util.StringUtils;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Wrapper class for {@link Registry Bukkit Registries}
 *
 * @param <T> Type of item in the registry
 */
@SuppressWarnings("unused")
public class RegistryWrapper<T extends Keyed> {

    /**
     * Wrap a registry with optional prefix and suffix
     *
     * @param registry Registry to wrap
     * @return Wrapped registry
     */
    public static <T extends Keyed> RegistryWrapper<T> wrap(@NotNull Registry<T> registry) {
        return wrap(registry, null, null);
    }

    /**
     * Wrap a registry with optional prefix and suffix
     *
     * @param registry Registry to wrap
     * @param prefix   Optional prefix to prepend to items in registry
     * @param suffix   Optional suffix to append to items in registry
     * @return Wrapped registry
     */
    public static <T extends Keyed> RegistryWrapper<T> wrap(@NotNull Registry<T> registry, @Nullable String prefix, @Nullable String suffix) {
        return new RegistryWrapper<>(registry, prefix, suffix);
    }

    private final Registry<T> registry;
    @Nullable
    private final String prefix, suffix;

    private RegistryWrapper(Registry<T> registry, @Nullable String prefix, @Nullable String suffix) {
        this.registry = registry;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    /**
     * Get names of all items in registry
     *
     * @return Names of all items
     */
    public String getNames() {
        List<String> keys = new ArrayList<>();
        this.registry.iterator().forEachRemaining(object -> keys.add(toString(object)));
        Collections.sort(keys);
        return StringUtils.join(keys, ", ");
    }

    /**
     * Convert to string for use in Skript
     *
     * @param object Item to put into string
     * @return String form of item
     */
    public String toString(T object) {
        String key = object.getKey().getKey();
        if (this.prefix != null && this.prefix.length() > 0) key = prefix + "_" + key;
        if (this.suffix != null && this.suffix.length() > 0) key = key + "_" + suffix;
        return key;
    }

    /**
     * Parse the string as a registry item
     *
     * @param string String to parse
     * @return Item from registry
     */
    private T parse(String string) {
        string = string.replace(" ", "_");
        if (this.prefix != null) {
            if (!string.contains(this.prefix)) return null;
            string = string.replace(prefix + "_", "").replace(prefix, "");
        }
        if (this.suffix != null) {
            if (!string.contains(this.suffix)) return null;
            string = string.replace("_" + suffix, "").replace(suffix, "");
        }
        string = string.trim();

        NamespacedKey key = Util.getMCNamespacedKey(string, false);
        if (key == null) return null;
        return this.registry.get(key);
    }

    /**
     * Get a {@link Parser} to be used in {@link ch.njol.skript.classes.ClassInfo}
     *
     * @return Parser for classinfo
     */
    public Parser<T> getParser() {
        return new Parser<>() {

            @SuppressWarnings("NullableProblems")
            @Override
            public @Nullable T parse(String string, ParseContext context) {
                return RegistryWrapper.this.parse(string);
            }

            @Override
            public @NotNull String toString(T o, int flags) {
                return RegistryWrapper.this.toString(o);
            }

            @Override
            public @NotNull String toVariableNameString(T o) {
                return toString(o, 0);
            }
        };
    }

}
