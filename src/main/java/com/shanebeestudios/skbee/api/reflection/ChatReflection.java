package com.shanebeestudios.skbee.api.reflection;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Reflection class for chat related stuff
 */
@SuppressWarnings("CallToPrintStackTrace")
public class ChatReflection {

    // Cache these classes/methods to prevent retrieving them too often
    private static final Class<?> ICHAT_BASE_COMPONENT_CLASS = ReflectionUtils.getNMSClass("net.minecraft.network.chat.Component", "IChatBaseComponent");
    private static final Class<?> CRAFT_CHAT_MESSAGE_CLASS = ReflectionUtils.getOBCClass("util.CraftChatMessage");
    private static final Class<?> TEXT_TAG_VISITOR_CLASS;
    private static final Class<?> NBT_BASE_CLASS = ReflectionUtils.getNMSClass("net.minecraft.nbt.Tag", "NBTBase");
    private static final Method FROM_COMPONENT;
    private static final Method VISIT_METHOD;

    static {
        TEXT_TAG_VISITOR_CLASS = ReflectionUtils.getNMSClass("net.minecraft.nbt.TextComponentTagVisitor");
        Method from_comp = null;
        Method visit = null;
        try {
            assert TEXT_TAG_VISITOR_CLASS != null;
            assert CRAFT_CHAT_MESSAGE_CLASS != null;
            visit = TEXT_TAG_VISITOR_CLASS.getDeclaredMethod(ReflectionConstants.TAG_VISITOR_VISIT_METHOD, NBT_BASE_CLASS);
            from_comp = CRAFT_CHAT_MESSAGE_CLASS.getMethod("fromComponent", ICHAT_BASE_COMPONENT_CLASS);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        FROM_COMPONENT = from_comp;
        VISIT_METHOD = visit;
    }

    /**
     * Get a pretty NBT string
     * <p>This is the same as what vanilla Minecraft outputs when using the '/data' command</p>
     *
     * @param compound Compound to convert to pretty
     * @param split    When null NBT will print on one long line, if not null NBT compound will be
     *                 split into lines with JSON style, and this string will start each line off
     *                 (usually spaces)
     * @return Pretty string of NBTCompound
     */
    @SuppressWarnings("deprecation")
    public static @Nullable String getPrettyNBT(NBTCompound compound, String split) {
        Object nmsNBT = new NBTContainer(compound.toString()).getCompound();
        String s = split != null ? split : "";
        try {
            Object tagVisitorInstance = TEXT_TAG_VISITOR_CLASS.getConstructor(String.class).newInstance(s);
            Object prettyComponent = VISIT_METHOD.invoke(tagVisitorInstance, nmsNBT);
            return ((String) FROM_COMPONENT.invoke(CRAFT_CHAT_MESSAGE_CLASS, prettyComponent));
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cache these classes/methods to prevent retrieving them too often
    private static final Class<?> CRAFT_CHAT_MESSAGE = ReflectionUtils.getOBCClass("util.CraftChatMessage");
    private static final Class<?> CRAFT_TEAM = ReflectionUtils.getOBCClass("scoreboard.CraftTeam");
    private static final Class<?> NMS_TEAM = ReflectionUtils.getNMSClass("net.minecraft.world.scores.ScoreboardTeam");
    private static final Method SET_PREFIX;
    private static final Method SET_SUFFIX;
    private static final Method PREFIX_COMP_METHOD;

    static {
        Method PREFIX_COMP_METHOD1 = null;
        Method SET_PREFIX1 = null;
        Method SET_SUFFIX1 = null;
        if (CRAFT_TEAM != null && NMS_TEAM != null && CRAFT_CHAT_MESSAGE != null) {
            try {
                SET_PREFIX1 = NMS_TEAM.getDeclaredMethod(ReflectionConstants.NMS_SCOREBOARD_TEAM_SET_PREFIX_METHOD, ICHAT_BASE_COMPONENT_CLASS);
                SET_SUFFIX1 = NMS_TEAM.getDeclaredMethod(ReflectionConstants.NMS_SCOREBOARD_TEAM_SET_SUFFIX_METHOD, ICHAT_BASE_COMPONENT_CLASS);
                PREFIX_COMP_METHOD1 = CRAFT_CHAT_MESSAGE.getDeclaredMethod("fromStringOrNull", String.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        PREFIX_COMP_METHOD = PREFIX_COMP_METHOD1;
        SET_PREFIX = SET_PREFIX1;
        SET_SUFFIX = SET_SUFFIX1;
    }

    /**
     * Util method for setting team prefixes on 1.13+ with no char limit
     *
     * @param team   Team to set prefix for
     * @param prefix Prefix to set
     */
    // TODO note: CraftBukkit finally removed these limits on July 2023 (MC 1.20.1)
    @SuppressWarnings("deprecation") // This is a Paper deprecation
    public static void setTeamPrefix(Team team, String prefix) {
        if (CRAFT_TEAM == null || PREFIX_COMP_METHOD == null || SET_PREFIX == null) {
            team.setPrefix("");
            team.setSuffix("");
            return;
        }

        try {
            Object nmsTeam = ReflectionUtils.getField("team", CRAFT_TEAM, team);
            Object prefixComp = PREFIX_COMP_METHOD.invoke(null, prefix);

            SET_PREFIX.invoke(nmsTeam, prefixComp);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Util method for setting team suffixes on 1.13+ with no char limit
     *
     * @param team   Team to set suffix for
     * @param suffix Suffix to set
     */
    // TODO note: CraftBukkit finally removed these limits on July 15, 2023 (MC 1.20.1)
    @SuppressWarnings("deprecation") // This is a Paper deprecation
    public static void setTeamSuffix(Team team, String suffix) {
        if (CRAFT_TEAM == null || PREFIX_COMP_METHOD == null || SET_SUFFIX == null) {
            team.setPrefix("");
            team.setSuffix("");
            return;
        }

        try {
            Object nmsTeam = ReflectionUtils.getField("team", CRAFT_TEAM, team);
            Object prefixComp = PREFIX_COMP_METHOD.invoke(null, suffix);

            SET_SUFFIX.invoke(nmsTeam, prefixComp);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
