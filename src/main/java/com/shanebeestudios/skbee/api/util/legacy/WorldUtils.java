package com.shanebeestudios.skbee.api.util.legacy;

import com.shanebeestudios.skbee.api.reflection.ReflectionUtils;
import org.bukkit.World;

/**
 * Utility class for {@link World Worlds}
 */
public class WorldUtils {

    private static final boolean SUPPORT_WORLD_MIN_HEIGHT = ReflectionUtils.methodExists("org.bukkit.generator.WorldInfo", "getMinHeight");

    public static int getMinHeight(World world) {
        if (SUPPORT_WORLD_MIN_HEIGHT) {
            return world.getMinHeight();
        }
        return 0;
    }

    public static int getMaxHeight(World world) {
        return world.getMaxHeight() - 1;
    }

}
