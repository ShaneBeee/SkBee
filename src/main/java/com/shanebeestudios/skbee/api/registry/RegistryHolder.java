package com.shanebeestudios.skbee.api.registry;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Holder class for registry elements
 *
 * @param <F> Type of registry
 * @param <T> Return type from registry (may differ from F)
 */
public class RegistryHolder<F extends Keyed, T> {

    private final RegistryKey<F> registryKey;
    private final Class<T> returnType;
    private final String registryName;
    private final Converter<F, T> converter;

    RegistryHolder(RegistryKey<F> registryKey, Class<T> returnType, String registryName, @Nullable Converter<F, T> converter) {
        this.registryKey = registryKey;
        this.returnType = returnType;
        this.registryName = registryName;
        this.converter = converter;
    }

    public RegistryKey<?> getRegistryKey() {
        return registryKey;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public String getRegistryName() {
        return registryName;
    }

    /**
     * Get all values from this {@link Registry}
     * <p>May be converted</p>
     *
     * @return List of values from registry
     */
    @SuppressWarnings({"unchecked", "NullableProblems"})
    public List<T> getValues() {
        Registry<F> registry = RegistryAccess.registryAccess().getRegistry(this.registryKey);

        if (this.converter != null) {
            List<T> values = new ArrayList<>();
            registry.forEach(value -> values.add(this.converter.convert(value)));
            return values;
        } else {
            return (List<T>) registry.stream().toList();
        }
    }

    public String getDocString() {
        ClassInfo<?> info = Classes.getExactClassInfo(this.returnType);
        String className = info != null ? info.getDocName() : "unsupported";
        return this.registryName + " [" + className + "]";
    }

}
