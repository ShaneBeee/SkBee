package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.Skript;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;
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

}

