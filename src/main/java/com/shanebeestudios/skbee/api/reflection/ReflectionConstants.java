package com.shanebeestudios.skbee.api.reflection;

import ch.njol.skript.Skript;

public class ReflectionConstants {

    public static String MINECRAFT_KEY_GET_KEY_METHOD = get("getKey", "getKey", "a");
    public static String TAG_VISITOR_VISIT_METHOD = get("null", "a", "a");
    public static String ENTITY_NO_CLIP_FIELD = get("noclip", "P", "Q");
    public static String NMS_SCOREBOARD_TEAM_SET_PREFIX_METHOD = get("setPrefix", "setPrefix", "b");
    public static String NMS_ITEMSTACK_GET_HOVER_NAME_METHOD = get("getName", "getName", "v");
    public static String NMS_CHAT_MESSAGE_GET_KEY_METHOD = get("getKey", "getKey", "i");

    private static String get(String v116, String v117, String v118) {
        if (Skript.isRunningMinecraft(1, 18)) {
            return v118;
        } else if (Skript.isRunningMinecraft(1, 17)) {
            return v117;
        } else if (Skript.isRunningMinecraft(1, 16)) {
            return v116;
        }
        throw new IllegalArgumentException("Unknown Version");
    }

}
