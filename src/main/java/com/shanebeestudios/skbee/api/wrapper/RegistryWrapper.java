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

public class RegistryWrapper<T extends Keyed> {

    private final Registry<T> registry;
    private final String prefix, suffix;

    public RegistryWrapper(Registry<T> registry, String prefix, String suffix) {
        this.registry = registry;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getNames() {
        List<String> keys = new ArrayList<>();
        this.registry.iterator().forEachRemaining(object -> keys.add(toString(object)));
        Collections.sort(keys);
        return StringUtils.join(keys, ", ");
    }

    public String toString(T object) {
        String key = object.getKey().getKey();
        if (this.prefix != null && this.prefix.length() > 0) key = prefix + "_" + key;
        if (this.suffix != null && this.suffix.length() > 0) key = key + "_" + suffix;
        return key;
    }

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

    public Parser<T> getParser() {
        return new Parser<>() {

            @SuppressWarnings("NullableProblems")
            @Override
            public @Nullable T parse(String string, ParseContext context) {
                return RegistryWrapper.this.parse(string);
            }

            @Override
            public @NotNull String toString(T o, int flags) {
                String className = o.getClass().getSimpleName().replace("Craft", "");
                return className + ":" + RegistryWrapper.this.toString(o);
            }

            @Override
            public @NotNull String toVariableNameString(T o) {
                return toString(o, 0);
            }
        };
    }

}
