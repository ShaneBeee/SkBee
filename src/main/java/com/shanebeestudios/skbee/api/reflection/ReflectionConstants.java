package com.shanebeestudios.skbee.api.reflection;

import ch.njol.skript.Skript;

public class ReflectionConstants {

    public static String MINECRAFT_KEY_GET_KEY_METHOD = get("getKey", "getKey", "a", "a");
    public static String TAG_VISITOR_VISIT_METHOD = get("null", "a", "a", "a");
    public static String ENTITY_NO_CLIP_FIELD = get("noclip", "P", "Q", "Q");
    public static String NMS_SCOREBOARD_TEAM_SET_PREFIX_METHOD = get("setPrefix", "setPrefix", "b", "b");
    public static String NMS_SCOREBOARD_TEAM_SET_SUFFIX_METHOD = get("setSuffix", "setSuffix", "c", "c");
    public static String NMS_ITEMSTACK_GET_HOVER_NAME_METHOD = get("getName", "getName", "v", "v");
    public static String NMS_CHAT_MESSAGE_GET_KEY_METHOD = get("getKey", "getKey", "i", "i");

    private static String get(String v116, String v117, String v118, String v119) {
        if (Skript.isRunningMinecraft(1, 19)) { // 1.19 might not be correct, TEST when 1.19 comes out
            return v119;
        } else if (Skript.isRunningMinecraft(1, 18)) {
            return v118;
        } else if (Skript.isRunningMinecraft(1, 17)) {
            return v117;
        } else if (Skript.isRunningMinecraft(1, 16)) {
            return v116;
        }
        throw new IllegalArgumentException("Unknown Version");
    }

}
