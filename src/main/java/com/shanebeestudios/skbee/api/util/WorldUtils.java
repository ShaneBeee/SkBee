package com.shanebeestudios.skbee.api.util;

import com.shanebeestudios.skbee.api.reflection.ReflectionUtils;
import org.bukkit.World;

public class WorldUtils {

    private static final boolean SUPPORT_WORLD_MAX_HEIGHT = ReflectionUtils.methodExists("org.bukkit.generator.WorldInfo", "getMaxHeight");
    private static final boolean SUPPORT_WORLD_MIN_HEIGHT = ReflectionUtils.methodExists("org.bukkit.generator.WorldInfo", "getMinHeight");

    public static int getMinHeight(World world) {
        if (SUPPORT_WORLD_MIN_HEIGHT) {
            return world.getMinHeight();
        }
        return 0;
    }

    public static int getMaxHeight(World world) {
        if (SUPPORT_WORLD_MAX_HEIGHT) {
            return world.getMaxHeight() - 1;
        }
        return 255;
    }

}
