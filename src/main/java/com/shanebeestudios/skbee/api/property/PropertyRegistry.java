package com.shanebeestudios.skbee.api.property;

import java.util.Map;
import java.util.TreeMap;

public class PropertyRegistry {

    public static Map<String, Property<?, ?>> PROPERTIES = new TreeMap<>();
    public static Map<String, Property<?, ?>> ENTITY_PROPERTIES = new TreeMap<>();
    public static Map<String, Property<?, ?>> ITEM_PROPERTIES = new TreeMap<>();

    public static Property<?, ?> registerEntityProperty(String name, Property<?, ?> property) {
        property.name = name;
        PROPERTIES.put(name, property);
        ENTITY_PROPERTIES.put(name, property);
        return property;
    }

    public static Property<?, ?> registerItemProperty(String name, Property<?, ?> property) {
        property.name = name;
        PROPERTIES.put(name, property);
        ITEM_PROPERTIES.put(name, property);
        return property;
    }

}
