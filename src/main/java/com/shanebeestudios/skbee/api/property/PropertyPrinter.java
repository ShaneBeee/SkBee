package com.shanebeestudios.skbee.api.property;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.util.Util;
import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PropertyPrinter {

    private static final Map<String, List<Property<?, ?>>> PROPERTY_MAP = new TreeMap<>();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void run() {
        File file = new File("plugins/SkBee/properties");
        file.mkdirs();

        Util.log("Printing all properties to file.");
        Util.log("All available properties: ");
        List<String> availableProperties = new ArrayList<>();
        PropertyRegistry.PROPERTIES.forEach((string, property) -> {
            availableProperties.add(string + " [" + getClassInfo(property.getFromType()) + "]");
        });
        Util.log(String.join(", ", availableProperties));

        printRegistry("entity", PropertyRegistry.ENTITY_PROPERTIES);
        printRegistry("item", PropertyRegistry.ITEM_PROPERTIES);
    }

    @SuppressWarnings({"deprecation"})
    public static void printRegistry(String registryName, Map<String, Property<?, ?>> registry) {
        PROPERTY_MAP.clear();
        registry.forEach((name, property) -> {
            Class<?> fromType = property.getFromType();
            String classInfoName = getClassInfo(fromType);
            if (!PROPERTY_MAP.containsKey(classInfoName)) {
                PROPERTY_MAP.put(classInfoName, new ArrayList<>());
            }
            PROPERTY_MAP.get(classInfoName).add(property);
        });

        try {
            PrintWriter writer = new PrintWriter("plugins/SkBee/properties/ " + registryName + ".txt", StandardCharsets.UTF_8);
            PROPERTY_MAP.forEach((className, properties) -> {
                writer.println("# " + WordUtils.capitalize(className) + ":");
                properties.forEach(property -> printProperty(writer, property));
            });

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getClassInfo(Class<?> clazz) {
        Class<?> c = clazz.isArray() ? clazz.getComponentType() : clazz;
        ClassInfo<?> info = Classes.getExactClassInfo(c);
        if (info == null) {
            info = Classes.getSuperClassInfo(c);
        }
        return info.toString();
    }

    private static void printProperty(PrintWriter writer, Property<?, ?> property) {
        String description = property.getDescription();
        String returnType = getClassInfo(property.getReturnType());
        writer.println("## " + property.getName() + ":");
        if (description != null) writer.println("- **Description**: " + description);
        writer.println("- **Return Type**: " + returnType);
        writer.println("- **Change Modes**: " + property.getChangeModes());
        writer.println("- **Since**: " + property.getSince());
        String[] examples = property.getExamples();
        if (examples != null && examples.length > 0) {
            writer.println("- **Examples**: ");
            writer.println("```vb");
            for (String example : examples) {
                writer.println(example);
            }
            writer.println("```");
        }
        writer.println();
    }

}
