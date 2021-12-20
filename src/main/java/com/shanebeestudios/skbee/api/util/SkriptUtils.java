package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.Skript;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;

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
}

