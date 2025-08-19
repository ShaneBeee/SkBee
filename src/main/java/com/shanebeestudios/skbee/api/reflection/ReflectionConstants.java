package com.shanebeestudios.skbee.api.reflection;

import ch.njol.skript.Skript;

/**
 * Constants for different reflection methods
 */
public class ReflectionConstants {

    // If running a remapped server jar
    private static final boolean REMAPPED_SERVER = Skript.classExists("net.minecraft.server.level.ServerPlayer");

    // net.minecraft.nbt.TextComponentTagVisitor -> visit(Tag)
    public static String TAG_VISITOR_VISIT_METHOD = get("visit", "a");
    // net.minecraft.world.entity.Entity -> noPhysics
    public static String ENTITY_NO_PHYSICS_FIELD = get("noPhysics", "ag", "ad");
    // net.minecraft.world.scores.PlayerTeam -> setPlayerPrefix
    public static String NMS_SCOREBOARD_TEAM_SET_PREFIX_METHOD = get("setPlayerPrefix", "b");
    // net.minecraft.world.scores.PlayerTeam -> setPlayerSuffix
    public static String NMS_SCOREBOARD_TEAM_SET_SUFFIX_METHOD = get("setPlayerSuffix", "c");

    @SuppressWarnings("SameParameterValue")
    private static String get(String mapped, String v1205, String v1212) {
        if (REMAPPED_SERVER) {
            return mapped;
        } else if (Skript.isRunningMinecraft(1, 21, 2)) {
            return v1212;
        } else if (Skript.isRunningMinecraft(1, 20, 5)) {
            return v1205;
        }
        throw new IllegalArgumentException("Unknown Version: " + Skript.getMinecraftVersion());
    }

    private static String get(String mapped, String obf) {
        return REMAPPED_SERVER ? mapped : obf;
    }

}
