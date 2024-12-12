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
    public static String ENTITY_NO_PHYSICS_FIELD = get("noPhysics", "Q", "Q", "ae", "af", "ag", "ad");
    // net.minecraft.world.scores.PlayerTeam -> setPlayerPrefix
    public static String NMS_SCOREBOARD_TEAM_SET_PREFIX_METHOD = get("setPlayerPrefix", "b");
    // net.minecraft.world.scores.PlayerTeam -> setPlayerSuffix
    public static String NMS_SCOREBOARD_TEAM_SET_SUFFIX_METHOD = get("setPlayerSuffix", "c");
    // net.minecraft.world.level.Level -> registryAccess
    public static String LEVEL_REGISTRY_ACCESS_METHOD = get("registryAccess", "null", "null", "null", "null", "H_", "K_");
    // net.minecraft.core.component.DataComponentMap -> CODEC
    public static String COMPONENT_MAP_CODEC_FIELD = get("CODEC", "b");
    // net.minecraft.nbt.NbtOps -> INSTANCE
    public static String NBT_OPS_INSTANCE_FIELD = get("INSTANCE", "a");
    // net.minecraft.world.item.ItemStack -> getComponents
    public static String ITEM_STACK_GET_COMPONENTS_METHOD = get("getComponents", "a");
    // net.minecraft.core.HolderLookup$Provider -> createSerializationContext
    public static String HOLDER_LOOKUP_SERIALIZATION_METHOD = get("createSerializationContext", "a");

    @SuppressWarnings("SameParameterValue")
    private static String get(String mapped, String v118, String v119, String v1194, String v1202, String v1205, String v1212) {
        if (REMAPPED_SERVER) {
            return mapped;
        } else if (Skript.isRunningMinecraft(1, 21, 2)) {
            return v1212;
        } else if (Skript.isRunningMinecraft(1, 20, 5)) {
            return v1205;
        } else if (Skript.isRunningMinecraft(1, 20, 2)) {
            return v1202;
        } else if (Skript.isRunningMinecraft(1, 19, 4)) {
            return v1194;
        } else if (Skript.isRunningMinecraft(1, 19)) {
            return v119;
        } else if (Skript.isRunningMinecraft(1, 18)) {
            return v118;
        }
        throw new IllegalArgumentException("Unknown Version: " + Skript.getMinecraftVersion());
    }

    private static String get(String mapped, String obf) {
        return REMAPPED_SERVER ? mapped : obf;
    }

}
