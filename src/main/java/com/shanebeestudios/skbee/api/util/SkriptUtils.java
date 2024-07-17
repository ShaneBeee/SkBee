package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.command.EffectCommandEvent;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.parser.ParserInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class to handle Skript things
 */
public class SkriptUtils {

    private static Field LAST_SPAWNED;

    static {
        Class<?> effSpawnClass;
        try {
            if (Skript.classExists("ch.njol.skript.sections.EffSecSpawn")) {
                effSpawnClass = Class.forName("ch.njol.skript.sections.EffSecSpawn");
            } else {
                effSpawnClass = Class.forName("ch.njol.skript.effects.EffSpawn");
            }
            LAST_SPAWNED = effSpawnClass.getDeclaredField("lastSpawned");
        } catch (ClassNotFoundException | NoSuchFieldException ignore) {
        }
    }

    /**
     * Set last spawned entity
     * <p>Skript changed the name of the EffSpawn class so now we gotta use this method</p>
     *
     * @param entity Entity that was spawned last
     */
    public static void setLastSpawned(Entity entity) {
        if (LAST_SPAWNED != null) {
            try {
                LAST_SPAWNED.set(null, entity);
            } catch (IllegalAccessException ignore) {
            }
        }
    }

    /**
     * Get counts of loaded Skript elements
     * <br>
     * In order events, effects, expressions, conditions, sections
     *
     * @return Counts of loaded Skript elements
     */
    public static int[] getElementCount() {
        int[] i = new int[5];

        i[0] = Skript.getEvents().size();
        i[1] = Skript.getEffects().size();
        AtomicInteger exprs = new AtomicInteger();
        Skript.getExpressions().forEachRemaining(e -> exprs.getAndIncrement());
        i[2] = exprs.get();
        i[3] = Skript.getConditions().size();
        i[4] = Skript.getSections().size();

        return i;
    }

    /**
     * Parse a string as an effect
     *
     * @param stringEffect String to parse
     * @param sender       Who it was sent from
     * @return True if parsed correctly else false
     */
    public static boolean parseEffect(String stringEffect, CommandSender sender) {
        ParserInstance parserInstance = ParserInstance.get();
        parserInstance.setCurrentEvent("effect command", EffectCommandEvent.class);
        Effect effect = Effect.parse(stringEffect, null);
        parserInstance.deleteCurrentEvent();
        if (effect != null) {
            return TriggerItem.walk(effect, new EffectCommandEvent(sender, stringEffect));
        } else {
            return false;
        }
    }

    /** Get a default instance of a Parser for ClassInfos
     * @param <T> ClassType
     * @return New instance of default parser
     */
    public static <T> Parser<T> getDefaultParser() {
        return new Parser<T>() {
            @SuppressWarnings("NullableProblems")
            @Override
            public boolean canParse(ParseContext context) {
                return false;
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public String toString(T o, int flags) {
                return o.toString();
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public String toVariableNameString(T o) {
                return o.toString();
            }
        };
    }

    public static Map<String,EquipmentSlotGroup> getEquipmentSlotGroups() {
        Map<String,EquipmentSlotGroup> groups = new HashMap<>();
        for (Field declaredField : EquipmentSlotGroup.class.getDeclaredFields()) {
            if (EquipmentSlotGroup.class.isAssignableFrom(declaredField.getType())) {
                try {
                    EquipmentSlotGroup equipmentSlotGroup = (EquipmentSlotGroup) declaredField.get(null);
                    String name = declaredField.getName().toLowerCase(Locale.ROOT) + "_slot_group";
                    groups.put(name, equipmentSlotGroup);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return groups;
    }

}

