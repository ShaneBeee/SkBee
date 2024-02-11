package com.shanebeestudios.skbee.api.property;

import ch.njol.skript.registrations.Classes;
import ch.njol.util.StringUtils;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.elements.other.type.Types;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Properties {

    static final Map<String, Property<?, ?>> PROPERTY_MAP = new HashMap<>();

    public static @Nullable Property<?, ?> getProperty(String name) {
        if (PROPERTY_MAP.containsKey(name)) return PROPERTY_MAP.get(name);
        return null;
    }

    public static <P, T> Property<P, T> registerProperty(Class<P> propertyClass, Class<T> returnType, String propertyName) {
        Property<P, T> property = new Property<>(propertyClass, returnType, propertyName);
        PROPERTY_MAP.put(propertyName, property);
        return property;
    }

    private static boolean init = false;

    public static void initializeProperties() {
        if (init) {
            throw new IllegalStateException("Properties have already been initialized");
        }
        init = true;

        // Initialize properties
        EntityProperties.init();
        BlockProperties.init();

        // Generate property names for docs
        generateDocs();
    }

    /**
     * Used to generate "usage" for docs
     */
    @SuppressWarnings("DataFlowIssue")
    public static void generateDocs() {
        // We have to wait til registration is done before getting ClassInfos
        Bukkit.getScheduler().runTaskLater(SkBee.getPlugin(), () -> {
            List<String> names = new ArrayList<>();

            PROPERTY_MAP.forEach((name, property) -> {
                Class<?> propertyClass = property.getPropertyClass();
                String usedOn = property.getUsedOn();
                String returnType = Classes.getExactClassInfo(property.getReturnType()).getName().getSingular();
                returnType = returnType.split(" \\(")[0]; // Boolean comes out as "boolean (yes/no)"
                String description = property.getDescription();
                String desc = description != null ? (" # " + description) : "";
                names.add(name + " [" + usedOn + "] (" + returnType + ")" + desc);

                Util.log("-" + name + " [" + usedOn + "] (" + returnType + ")" + desc);
            });
            Collections.sort(names);
            Types.PROPERTY_CLASS_INFO.usage(StringUtils.join(names, "\n"));
        }, 0);
    }

}
