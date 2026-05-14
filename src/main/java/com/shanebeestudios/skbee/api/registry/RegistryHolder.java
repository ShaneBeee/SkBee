package com.shanebeestudios.skbee.api.registry;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.Converter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Holder class for {@link Registry} elements
 *
 * @param <F> Type of registry
 * @param <T> Return type from registry (may differ from F)
 */
@SuppressWarnings({"UnstableApiUsage", "unused"})
public class RegistryHolder<F extends Keyed, T> {

    private final RegistryKey<F> registryKey;
    private final Class<T> returnType;
    private final String registryName;
    private final Converter<F, T> converter;
    private final Converter<T, F> reverser;

    RegistryHolder(RegistryKey<F> registryKey, Class<T> returnType, String registryName,
                   @Nullable Converter<F, T> converter, @Nullable Converter<T, F> reverser) {
        this.registryKey = registryKey;
        this.returnType = returnType;
        this.registryName = registryName;
        this.converter = converter;
        this.reverser = reverser;
    }

    /**
     * Get the RegistryKey that belongs to this {@link Registry}.
     *
     * @return RegistryKey
     */
    public RegistryKey<?> getRegistryKey() {
        return registryKey;
    }

    /**
     * Get the type of class returned by this {@link Registry}.
     *
     * @return Class type
     */
    public Class<?> getReturnType() {
        return returnType;
    }

    /**
     * Get the name of this {@link Registry}.
     *
     * @return Registry name
     */
    public String getRegistryName() {
        return registryName;
    }

    /**
     * Get all values from this {@link Registry}.
     * <p>May be converted</p>
     *
     * @return List of values from the registry
     */
    @SuppressWarnings({"unchecked", "NullableProblems"})
    public List<T> getValues() {
        Registry<F> registry = RegistryAccess.registryAccess().getRegistry(this.registryKey);

        if (this.converter != null) {
            List<T> values = new ArrayList<>();
            registry.forEach(value -> values.add(this.converter.convert(value)));
            values.sort(Comparator.comparing(Object::toString));
            return values;
        } else {
            return (List<T>) registry.stream().sorted(Comparator.comparing(keyed -> keyed.key().toString())).toList();
        }
    }

    /**
     * Get all values of a TagKey from this {@link Registry}.
     *
     * @param tagKey TagKey to get values from
     * @return Values from tagkey
     */
    @SuppressWarnings("unchecked")
    public List<T> getTagValues(TagKey<F> tagKey) {
        Registry<F> registry = RegistryAccess.registryAccess().getRegistry(this.registryKey);
        List<T> values = new ArrayList<>();
        Tag<F> tag = registry.getTag(tagKey);
        if (tag != null) {
            for (F value : tag.resolve(registry)) {
                if (this.converter != null) {
                    values.add(this.converter.convert(value));
                } else {
                    values.add((T) value);
                }
            }
            values.sort(Comparator.comparing(Object::toString));
        }
        return values;
    }

    /**
     * Get the value of a TypedKey from this {@link Registry}.
     *
     * @param typedKey TypedKey to get value from
     * @return Value from registry
     */
    @SuppressWarnings("unchecked")
    public @Nullable T getValue(TypedKey<F> typedKey) {
        Registry<F> registry = RegistryAccess.registryAccess().getRegistry(this.registryKey);
        F f = registry.get(typedKey);
        if (f == null) return null;

        if (this.converter != null) {
            return this.converter.convert(f);
        }
        return (T) f;
    }

    /**
     * Get the original value from the registry.
     *
     * @param value Value to convert back
     * @return Original value from registry
     */
    @SuppressWarnings("unchecked")
    public @Nullable F reverse(T value) {
        if (this.reverser != null) {
            try {
                return this.reverser.convert(value);
            } catch (ClassCastException ignored) {
                return null;
            }
        } else {
            return (F) value;
        }
    }

    /**
     * @hidden
     */
    @ApiStatus.Internal
    public String getDocString() {
        ClassInfo<?> info = Classes.getExactClassInfo(this.returnType);
        String className = info != null ? info.getDocName() : "unsupported";
        return this.registryName + " [" + className + "]";
    }

}
