package com.shanebeestudios.skbee.api.registration;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shanebeestudios.skbee.api.registration.Registration.ConditionRegistrar;
import com.shanebeestudios.skbee.api.registration.Registration.EffectRegistrar;
import com.shanebeestudios.skbee.api.registration.Registration.EventRegistrar;
import com.shanebeestudios.skbee.api.registration.Registration.ExpressionRegistrar;
import com.shanebeestudios.skbee.api.registration.Registration.FunctionRegistrar;
import com.shanebeestudios.skbee.api.registration.Registration.SectionRegistrar;
import com.shanebeestudios.skbee.api.registration.Registration.StructureRegistrar;
import com.shanebeestudios.skbee.api.registration.Registration.TypeRegistrar;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.common.function.DefaultFunction;
import org.skriptlang.skript.common.function.Parameter;
import org.skriptlang.skript.lang.entry.EntryData;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.SectionEntryData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Locale;

public class JsonDocGenerator {

    private final Plugin plugin;
    private final String addonName;
    private final Registration registration;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private int total = 0;

    public JsonDocGenerator(Plugin plugin, Registration registration) {
        this.plugin = plugin;
        this.addonName = plugin.getPluginMeta().getName().toLowerCase().replace(" ", "_");
        this.registration = registration;
    }

    public void generateDocs() {
        long start = System.currentTimeMillis();
        Util.log("Generating docs...");
        JsonObject mainDoc = new JsonObject();
        addMeta(mainDoc);

        generateTypes(mainDoc);
        generateStructures(mainDoc);
        generateEvents(mainDoc);
        generateSections(mainDoc);
        generateEffects(mainDoc);
        generateExpressions(mainDoc);
        generateConditions(mainDoc);
        generateFunctions(mainDoc);

        // Print to file
        printToFile(mainDoc);
        long fin = System.currentTimeMillis() - start;
        Util.log("Finished generating %s docs in %sms", this.total, fin);
    }

    private void generateTypes(JsonObject mainDoc) {
        Util.log("Generating types...");
        JsonArray typesArray = new JsonArray();

        for (TypeRegistrar<?> type : this.registration.getTypes()) {
            JsonObject syntaxObject = new JsonObject();

            Documentation documentation = type.getDocumentation();
            if (documentation.isNoDoc()) continue;

            if (documentation.getName() == null) {
                Util.log("&cMissing name for Type '%s'", type.type.getSimpleName());
                continue;
            }

            // Generic
            gemerateGeneric("type", documentation, syntaxObject, type.user);

            // Usage
            if (type.usage != null) {
                syntaxObject.addProperty("usage", type.usage);
            }

            typesArray.add(syntaxObject);
        }
        this.total += typesArray.size();
        Util.log("Generated %s types", typesArray.size());
        mainDoc.add("types", typesArray);
    }

    private void generateStructures(JsonObject mainDoc) {
        Util.log("Generating structures...");
        JsonArray structuresArray = new JsonArray();

        for (StructureRegistrar<?> structure : this.registration.getStructures()) {
            JsonObject syntaxObject = new JsonObject();
            Documentation documentation = structure.getDocumentation();
            if (documentation.isNoDoc()) continue;

            if (documentation.getName() == null) {
                Util.log("&cMissing name for Structure '%s'", structure.structureClass.getSimpleName());
                continue;
            }

            // Generic
            gemerateGeneric("structure", documentation, syntaxObject, structure.patterns);

            // Entires
            EntryValidator validator = structure.validator;
            if (validator != null) {
                JsonArray entriesArray = new JsonArray();
                for (EntryData<?> entryDatum : validator.getEntryData()) {
                    JsonObject entryObject = new JsonObject();
                    entryObject.addProperty("name", entryDatum.getKey());
                    entryObject.addProperty("isRequired", !entryDatum.isOptional());
                    entryObject.addProperty("isSection", SectionEntryData.class.isAssignableFrom(entryDatum.getClass()));
                    entriesArray.add(entryObject);
                }
                syntaxObject.add("entries", entriesArray);
            }

            structuresArray.add(syntaxObject);
        }

        this.total += structuresArray.size();
        Util.log("Generated %s structures", structuresArray.size());
        mainDoc.add("structures", structuresArray);
    }

    private void generateEvents(JsonObject mainDoc) {
        Util.log("Generating events...");
        JsonArray eventsArray = new JsonArray();

        for (EventRegistrar event : this.registration.getEvents()) {
            JsonObject syntaxObject = new JsonObject();

            Documentation documentation = event.getDocumentation();
            if (documentation.isNoDoc()) continue;

            if (documentation.getName() == null) {
                Util.log("&cMissing name for Type '%s'", event.skriptEventClass.getSimpleName());
                continue;
            }

            // Generic
            gemerateGeneric("type", documentation, syntaxObject, event.patterns);

            // EventValues
            JsonArray eventValuesArray = new JsonArray();
            for (Class<? extends Event> eventClass : event.eventClasses) {
                EventValues.getPerEventEventValues().forEach((aClass, eventValueInfo) -> {
                    if (aClass.isAssignableFrom(eventClass)) {
                        ClassInfo<?> exactClassInfo = Classes.getExactClassInfo(eventValueInfo.valueClass());
                        if (exactClassInfo == null) return;
                        String singular = exactClassInfo.getName().getSingular();
                        eventValuesArray.add("event-" + singular);
                    }
                });
            }
            if (!eventValuesArray.isEmpty()) {
                syntaxObject.add("event values", eventValuesArray);
            }

            eventsArray.add(syntaxObject);
        }

        this.total += eventsArray.size();
        Util.log("Generated %s events", eventsArray.size());
        mainDoc.add("events", eventsArray);
    }

    private void generateSections(JsonObject mainDoc) {
        Util.log("Generating sections...");
        JsonArray sectionsArray = new JsonArray();

        for (SectionRegistrar section : this.registration.getSections()) {
            JsonObject syntaxObject = new JsonObject();

            Documentation documentation = section.getDocumentation();
            if (documentation.isNoDoc()) continue;

            if (documentation.getName() == null) {
                Util.log("&cMissing name for Section '%s'", section.section.getSimpleName());
                continue;
            }

            // Generic
            gemerateGeneric("section", documentation, syntaxObject, section.patterns);

            // Entires
            EntryValidator validator = section.validator;
            if (validator != null) {
                JsonArray entriesArray = new JsonArray();
                for (EntryData<?> entryDatum : validator.getEntryData()) {
                    JsonObject entryObject = new JsonObject();
                    entryObject.addProperty("name", entryDatum.getKey());
                    entryObject.addProperty("isRequired", !entryDatum.isOptional());
                    entryObject.addProperty("isSection", SectionEntryData.class.isAssignableFrom(entryDatum.getClass()));
                    entriesArray.add(entryObject);
                }
                syntaxObject.add("entries", entriesArray);
            }

            sectionsArray.add(syntaxObject);
        }

        this.total += sectionsArray.size();
        Util.log("Generated %s sections", sectionsArray.size());
        mainDoc.add("sections", sectionsArray);
    }

    private void generateEffects(JsonObject mainDoc) {
        Util.log("Generating effects...");
        JsonArray effectsArray = new JsonArray();

        for (EffectRegistrar effect : this.registration.getEffects()) {
            JsonObject syntaxObject = new JsonObject();

            Documentation documentation = effect.getDocumentation();
            if (documentation.isNoDoc()) continue;

            if (documentation.getName() == null) {
                Util.log("&cMissing name for Effect '%s'", effect.effect.getSimpleName());
                continue;
            }

            // Generic
            gemerateGeneric("effect", documentation, syntaxObject, effect.patterns);

            effectsArray.add(syntaxObject);
        }

        this.total += effectsArray.size();
        Util.log("Generated %s effects", effectsArray.size());
        mainDoc.add("effects", effectsArray);
    }

    private void generateExpressions(JsonObject mainDoc) {
        Util.log("Generating expressions...");
        JsonArray expressionsArray = new JsonArray();

        Util.log("&eChecking changers, ignore incoming errors from Expression classes");
        for (ExpressionRegistrar<?, ?> expression : this.registration.getExpressions()) {
            JsonObject syntaxObject = new JsonObject();

            Documentation documentation = expression.getDocumentation();
            if (documentation.isNoDoc()) continue;

            if (documentation.getName() == null) {
                Util.log("&cMissing name for Expression '%s'", expression.expressionClass.getSimpleName());
                continue;
            }

            // Generic
            gemerateGeneric("expression", documentation, syntaxObject, expression.patterns);

            // Return Type
            ClassInfo<?> returnInfo = Classes.getExactClassInfo(expression.returnType);
            if (returnInfo != null) {
                syntaxObject.addProperty("return type", returnInfo.getDocName());
            }

            // Changers
            JsonArray changerArray = new JsonArray();
            try {
                Expression<?> o = expression.expressionClass.getConstructor().newInstance();
                for (ChangeMode value : o.getAcceptedChangeModes().keySet()
                    .stream().sorted(Comparator.comparing(Enum::name)).toList()) {
                    changerArray.add(value.name().toLowerCase(Locale.ROOT));
                }
                if (!changerArray.isEmpty()) {
                    syntaxObject.add("changers", changerArray);
                }
            } catch (Exception ignore) {
            }

            expressionsArray.add(syntaxObject);
        }

        this.total += expressionsArray.size();
        Util.log("Generated %s expressions.", expressionsArray.size());
        mainDoc.add("expressions", expressionsArray);
    }

    private void generateConditions(JsonObject mainDoc) {
        Util.log("Generating conditions...");
        JsonArray conditonsArray = new JsonArray();

        for (ConditionRegistrar condition : this.registration.getConditions()) {
            JsonObject syntaxObject = new JsonObject();

            Documentation documentation = condition.getDocumentation();
            if (documentation.isNoDoc()) continue;

            if (documentation.getName() == null) {
                Util.log("&cMissing name for Condition '%s'", condition.condition.getSimpleName());
                continue;
            }

            // Generic
            gemerateGeneric("condition", documentation, syntaxObject, condition.patterns);

            conditonsArray.add(syntaxObject);
        }

        this.total += conditonsArray.size();
        Util.log("Generated %s conditions", conditonsArray.size());
        mainDoc.add("conditions", conditonsArray);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void generateFunctions(JsonObject mainDoc) {
        Util.log("Generating functions...");
        JsonArray functionsArray = new JsonArray();

        for (FunctionRegistrar<?> function : this.registration.getFunctions()) {
            JsonObject syntaxObject = new JsonObject();

            Documentation documentation = function.getDocumentation();
            if (documentation.isNoDoc()) continue;

            if (documentation.getName() == null) {
                documentation.setName(function.function.name());
            }

            // Generic
            String pattern = generateFunctionPattern(function.function);
            gemerateGeneric("function", documentation, syntaxObject, new String[]{pattern});

            // Return Type
            ClassInfo<?> returnInfo = Classes.getExactClassInfo(function.function.signature().returnType());
            if (returnInfo != null) {
                syntaxObject.addProperty("return type", returnInfo.getDocName());
            }

            functionsArray.add(syntaxObject);
        }

        this.total += functionsArray.size();
        Util.log("Generated %s functions", functionsArray.size());
        mainDoc.add("functions", functionsArray);
    }

    private void addMeta(JsonObject mainDoc) {
        JsonObject meta = new JsonObject();
        meta.addProperty("version", this.plugin.getPluginMeta().getVersion());
        mainDoc.add("metadata", meta);
    }

    private void gemerateGeneric(String type, Documentation documentation, JsonObject syntaxObject,
                                 @Nullable String[] patterns) {
        // Generate ID
        String id = generateId(type, documentation.getName());
        syntaxObject.addProperty("id", id);

        // Name
        String name = documentation.getName();
        syntaxObject.addProperty("name", name);

        // Description
        String[] description = documentation.getDescription();
        if (description != null) {
            JsonArray descriptionArray = new JsonArray();
            for (String line : description) {
                descriptionArray.add(line);
            }
            syntaxObject.add("description", descriptionArray);
        }

        // Examples
        String[] examples = documentation.getExamples();
        if (examples != null) {
            JsonArray examplesArray = new JsonArray();
            for (String example : examples) {
                examplesArray.add(example);
            }
            syntaxObject.add("examples", examplesArray);
        }

        // Since
        String[] since = documentation.getSince();
        if (since != null) {
            JsonArray sinceArray = new JsonArray();
            for (String s : since) {
                sinceArray.add(s);
            }
            syntaxObject.add("since", sinceArray);
        } else {
            Util.log("&cMissing 'since' for '%s'", name);
        }

        if (patterns != null) {
            JsonArray patternArray = new JsonArray();
            for (String pattern : parsePatterns(patterns)) {
                patternArray.add(pattern);
            }
            syntaxObject.add("patterns", patternArray);
        }
    }

    private String generateId(String type, String name) {
        type = type.toLowerCase(Locale.ROOT).replace(" ", "_");
        name = name.toLowerCase(Locale.ROOT).replace(" ", "_");
        return String.format("%s:%s:%s", this.addonName, type, name);
    }

    @SuppressWarnings("UnstableApiUsage")
    private String generateFunctionPattern(DefaultFunction<?> function) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(function.name());
        stringBuilder.append("(");

        Parameter<?>[] all = function.signature().parameters().all();
        for (int i = 0; i < all.length; i++) {
            stringBuilder.append(all[i].toFormattedString());
            if (i < all.length - 1) {
                stringBuilder.append(", ");
            }
        }


        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    private String[] parsePatterns(String[] patterns) {
        String[] returns = new String[patterns.length];

        for (int i = 0; i < patterns.length; i++) {
            //Pattern compile = Pattern.compile(patterns[i]);
            returns[i] = patterns[i]
                .replaceAll("\\((.+?)\\)\\?", "[$1]")
                .replaceAll("(.)\\?", "[$1]");
        }
        return returns;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void printToFile(JsonObject jsonElement) {
        File dataFolder = this.plugin.getDataFolder();
        File file = new File(dataFolder, "json-docs.json");
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(jsonElement, writer);
            Util.log("&aSuccessfully wrote JSON element to 'json-docs.json'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
