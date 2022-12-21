package com.shanebeestudios.skbee.api.reflection;

import ch.njol.skript.Skript;

public class ReflectionConstants {

    // net.minecraft.nbt.TagVisitor -> visitString
    public static String TAG_VISITOR_VISIT_METHOD = get("a", "a", "a");
    // net.minecraft.world.entity.Entity -> noPhysics
    public static String ENTITY_NO_PHYSICS_FIELD = get("P", "Q", "Q");
    // net.minecraft.world.scores.PlayerTeam -> setPlayerPrefix
    public static String NMS_SCOREBOARD_TEAM_SET_PREFIX_METHOD = get("setPrefix", "b", "b");
    // net.minecraft.world.scores.PlayerTeam -> setPlayerSuffix
    public static String NMS_SCOREBOARD_TEAM_SET_SUFFIX_METHOD = get("setSuffix", "c", "c");

    private static String get(String v117, String v118, String v119) {
        if (Skript.isRunningMinecraft(1, 19)) {
            return v119;
        } else if (Skript.isRunningMinecraft(1, 18)) {
            return v118;
        } else if (Skript.isRunningMinecraft(1, 17)) {
            return v117;
        }
        throw new IllegalArgumentException("Unknown Version");
    }

}
