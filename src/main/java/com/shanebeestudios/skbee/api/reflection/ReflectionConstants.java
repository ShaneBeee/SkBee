package com.shanebeestudios.skbee.api.reflection;

import ch.njol.skript.Skript;

/**
 * Constants for different reflection methods
 */
public class ReflectionConstants {

    // net.minecraft.nbt.TextComponentTagVisitor -> visit(Tag)
    public static String TAG_VISITOR_VISIT_METHOD = get("a", "a", "a", "a");
    // net.minecraft.world.entity.Entity -> noPhysics
    public static String ENTITY_NO_PHYSICS_FIELD = get("P", "Q", "Q", "ae");
    // net.minecraft.world.scores.PlayerTeam -> setPlayerPrefix
    public static String NMS_SCOREBOARD_TEAM_SET_PREFIX_METHOD = get("setPrefix", "b", "b", "b");
    // net.minecraft.world.scores.PlayerTeam -> setPlayerSuffix
    public static String NMS_SCOREBOARD_TEAM_SET_SUFFIX_METHOD = get("setSuffix", "c", "c", "c");

    private static String get(String v117, String v118, String v119, String v1194) {
        if (Skript.isRunningMinecraft(1,19,4)) {
            return v1194;
        } else if (Skript.isRunningMinecraft(1, 19)) {
            return v119;
        } else if (Skript.isRunningMinecraft(1, 18)) {
            return v118;
        } else if (Skript.isRunningMinecraft(1, 17)) {
            return v117;
        }
        throw new IllegalArgumentException("Unknown Version: " + Skript.getMinecraftVersion());
    }

}
