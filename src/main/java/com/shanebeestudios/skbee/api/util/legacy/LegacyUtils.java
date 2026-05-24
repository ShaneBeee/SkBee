package com.shanebeestudios.skbee.api.util.legacy;

import ch.njol.skript.Skript;

/**
 * Utility class to handle legacy Minecraft versions
 */
public class LegacyUtils {

    // Shortcut for finding stuff to remove later
    public static final boolean IS_RUNNING_MC_1_21_11 = Skript.isRunningMinecraft(1, 21, 11);
    public static final boolean IS_RUNNING_MC_26_1_1 = Skript.isRunningMinecraft(26, 1, 1);
    public static final boolean IS_RUNNING_MC_26_1_2 = Skript.isRunningMinecraft(26, 1, 2);

    // Not sure when this was added, maybe 26.1.2?!?
    public static final boolean HAS_LUNGE_EVENT = Skript.classExists("io.papermc.paper.event.entity.EntityLungeEvent");

}
