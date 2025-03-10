package com.shanebeestudios.skbee.api.property;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * Registry for {@link Property Properties}
 */
public class PropertyRegistry {

    private static final Map<String, Property<?, ?>> PROPERTIES = new TreeMap<>();

    /**
     * Register a new {@link Property}
     *
     * @param name     Name of property
     * @param property Property to register
     * @param <F>      Type of property holder
     * @param <T>      Return/change type
     * @return Instance of new property for chaining
     */
    public static <F, T> Property<F, T> registerProperty(String name, Property<F, T> property) {
        property.name = name;
        PROPERTIES.put(name, property);
        return property;
    }

    /**
     * Get all registered {@link Property Properties}
     *
     * @return All registered properties
     */
    public static ImmutableMap<String, Property<?, ?>> properties() {
        return ImmutableMap.copyOf(PROPERTIES);
    }

    /**
     * Get a supplier of all {@link Property Properties}
     * <p>Internally used for ClassInfos</p>
     *
     * @return Supplier of all properties
     */
    @ApiStatus.Internal
    @SuppressWarnings("rawtypes")
    public static Supplier<Iterator<Property>> supplier() {
        List<Property> properties = new ArrayList<>(PROPERTIES.values());
        return properties::iterator;
    }

}
