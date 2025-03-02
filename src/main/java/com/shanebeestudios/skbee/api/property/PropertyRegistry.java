package com.shanebeestudios.skbee.api.property;

import java.util.Map;
import java.util.TreeMap;

public class PropertyRegistry {

    public static Map<String, Property<?, ?>> PROPERTIES = new TreeMap<>();

    public static Property<?, ?> registerProperty(String name, Property<?, ?> property) {
        property.name = name;
        PROPERTIES.put(name, property);
        return property;
    }

}
