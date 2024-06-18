package com.shanebeestudios.skbee.api.wrapper;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.util.StringUtils;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link ClassInfo} wrapper class for {@link Registry Bukkit Registries}
 *
 * @param <T> Type of item in the registry
 */
@SuppressWarnings({"unused"})
public class RegistryWrapper<T extends Keyed> extends ClassInfo<T> {

    /**
     * Create a Registry ClassInfo
     *
     * @param registry      Registry to wrap
     * @param registryClass Class of registry
     * @param codename      Codename for ClassInfo
     * @return ClassInfo from Registry
     */
    public static <T extends Keyed> RegistryWrapper<T> getClassInfo(@NotNull Registry<T> registry, Class<T> registryClass, String codename) {
        return getClassInfo(registry, registryClass, codename, null, null);
    }

    /**
     * Create a Registry ClassInfo with optional prefix and suffix
     *
     * @param registry      Registry to wrap
     * @param registryClass Class of registry
     * @param codename      Codename for ClassInfo
     * @param prefix        Optional prefix to prepend to items in registry
     * @param suffix        Optional suffix to append to items in registry
     * @return ClassInfo from Registry
     */
    public static <T extends Keyed> RegistryWrapper<T> getClassInfo(@NotNull Registry<T> registry, Class<T> registryClass, String codename, @Nullable String prefix, @Nullable String suffix) {
        return new RegistryWrapper<>(registry, registryClass, codename, prefix, suffix);
    }


    private final Registry<T> registry;
    @Nullable
    private final String prefix, suffix;

    private RegistryWrapper(Registry<T> registry, Class<T> registryClass, String codename, @Nullable String prefix, @Nullable String suffix) {
        super(registryClass, codename);
        this.registry = registry;
        this.prefix = prefix;
        this.suffix = suffix;
        Comparators.registerComparator(registryClass, registryClass, (o1, o2) -> Relation.get(o1.equals(o2)));
        this.usage(getNames());
        this.parser(new Parser<>() {
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
        });
    }

    /**
     * Get names of all items in registry
     *
     * @return Names of all items
     */
    public String getNames() {
        List<String> keys = new ArrayList<>();
        this.registry.iterator().forEachRemaining(object -> keys.add(getName(object)));
        Collections.sort(keys);
        return StringUtils.join(keys, ", ");
    }

    private String getName(T object) {
        String key = object.getKey().getKey();
        if (this.prefix != null && !this.prefix.isEmpty()) key = prefix + "_" + key;
        if (this.suffix != null && !this.suffix.isEmpty()) key = key + "_" + suffix;
        return key;
    }

    /**
     * Convert to string for use in Skript
     *
     * @param object Item to put into string
     * @return String form of item
     */
    public @NotNull String toString(T object) {
        NamespacedKey namespacedKey;
        try {
            namespacedKey = object.getKey();
        } catch (IllegalArgumentException ignore) {
            return "invalid key for: " + object;
        }
        String key = namespacedKey.getKey();
        if (this.prefix != null && !this.prefix.isEmpty()) key = prefix + "_" + key;
        if (this.suffix != null && !this.suffix.isEmpty()) key = key + "_" + suffix;
        return namespacedKey.getNamespace() + ":" + key;
    }

    /**
     * Parse the string as a registry item
     *
     * @param string String to parse
     * @return Item from registry
     */
    @Nullable
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

}
