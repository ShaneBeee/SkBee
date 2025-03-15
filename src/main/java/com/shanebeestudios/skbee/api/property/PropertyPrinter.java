package com.shanebeestudios.skbee.api.property;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.skbee.api.util.Util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Printer to print all properties for Wiki
 */
public class PropertyPrinter {

    public static void printAll() {
        long start = System.currentTimeMillis();
        Map<Class<?>, List<Property<?, ?>>> mapByClass = new HashMap<>();
        List<Holder> holderList = new ArrayList<>();

        PropertyRegistry.properties().forEach((name, property) -> {
            Class<?> fromType = property.getPropertyHolder();
            if (!mapByClass.containsKey(fromType)) {
                mapByClass.put(fromType, new ArrayList<>());
            }
            mapByClass.get(fromType).add(property);
        });
        mapByClass.keySet().stream().sorted(Comparator.comparing(Class::getSimpleName)).forEach(type -> {
            String docName;
            String link = null;
            ClassInfo<?> exactClassInfo = Classes.getExactClassInfo(type);
            if (exactClassInfo != null) {
                docName = exactClassInfo.getDocName();
                if (docName == null || docName.isEmpty()) docName = type.getSimpleName();
                link = "Properties which can be used on [" + docName + "](https://docs.skriptlang.org/classes.html#" + exactClassInfo.getCodeName()+ ")";
            } else {
                docName = type.getSimpleName();
            }
            Holder holder = new Holder(docName, link, mapByClass.get(type));
            holderList.add(holder);
        });

        try {
            File file = new File("plugins/SkBee/properties/all-properties.md");
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    if (!parentFile.mkdirs()) {
                        Util.skriptError("Unable to create properties directory.");
                        return;
                    } else {
                        Util.log("Created properties directory.");
                    }
                }
                if (!file.createNewFile()) {
                    Util.skriptError("Unable to create properties file.");
                    return;
                } else {
                    Util.log("Created properties file.");
                }
            }
            PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8);
            writer.println("# Properties");
            writer.println("Properties are simplied versions of full expression, which are used in the [Property Expression](https://skripthub.net/docs/?id=13236)");
            writer.println();
            writer.println("Table of contents:");

            holderList.forEach(holder -> writer.println("- [" + holder.name + "](#" + holder.name.replace(" ", "-") + ")"));

            writer.println();

            holderList.forEach(holder -> {
                writer.println("# " + holder.name + ":");
                if (holder.description != null) {
                    writer.println(holder.description);
                }
                holder.properties.forEach(property -> printProperty(writer, property));
            });
            writer.close();
            long fin = System.currentTimeMillis() - start;
            Util.log("Properties written to file in %s ms.", fin);
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

    private static class Holder {
        String name;
        String description;
        List<Property<?,?>> properties;

        public Holder(String name, String description, List<Property<?, ?>> properties) {
            this.name = name;
            this.description = description;
            this.properties = properties;
        }
    }

}
