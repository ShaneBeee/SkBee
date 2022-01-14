package com.shanebeestudios.skbee.api.reflection;

import ch.njol.skript.Skript;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Reflection class for chat related stuff
 */
public class ChatReflection {

    private static final boolean NEW_PRETTY_NBT = Skript.isRunningMinecraft(1, 17);

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
    public static String getPrettyNBT(NBTCompound compound, String split) {
        if (NEW_PRETTY_NBT) {
            return getPretty_17(compound, split);
        } else {
            return getPretty_16(compound, split);
        }
    }

    // Cache these classes/methods to prevent retrieving them too often
    private static final Class<?> ICHAT_BASE_COMPONENT_CLASS = ReflectionUtils.getNMSClass("IChatBaseComponent", "net.minecraft.network.chat");
    private static final Class<?> CRAFT_CHAT_MESSAGE_CLASS = ReflectionUtils.getOBCClass("util.CraftChatMessage");
    private static final Class<?> TEXT_TAG_VISITOR_CLASS;
    private static final Class<?> NBT_BASE_CLASS = ReflectionUtils.getNMSClass("NBTBase", "net.minecraft.nbt");
    private static final Method FROM_COMPONENT;
    private static final Method VISIT_METHOD;

    static {
        // new class in MC 1.17
        if (NEW_PRETTY_NBT) {
            TEXT_TAG_VISITOR_CLASS = ReflectionUtils.getNMSClass("TextComponentTagVisitor", "net.minecraft.nbt");
        } else {
            TEXT_TAG_VISITOR_CLASS = null;
        }
        Method from_comp = null;
        Method visit = null;
        try {
            if (NEW_PRETTY_NBT) {
                assert TEXT_TAG_VISITOR_CLASS != null;
                visit = TEXT_TAG_VISITOR_CLASS.getDeclaredMethod(ReflectionConstants.TAG_VISITOR_VISIT_METHOD, NBT_BASE_CLASS);
            }
            assert CRAFT_CHAT_MESSAGE_CLASS != null;
            from_comp = CRAFT_CHAT_MESSAGE_CLASS.getMethod("fromComponent", ICHAT_BASE_COMPONENT_CLASS);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        FROM_COMPONENT = from_comp;
        VISIT_METHOD = visit;

    }

    private static String getPretty_16(NBTCompound compound, String split) {
        Object nmsNBT = new NBTContainer(compound.toString()).getCompound();
        String s = split != null ? split : "";
        try {
            Method prettyMethod = nmsNBT.getClass().getMethod("a", String.class, int.class);
            Object prettyComponent = prettyMethod.invoke(nmsNBT, s, 0);
            assert CRAFT_CHAT_MESSAGE_CLASS != null;
            return ((String) FROM_COMPONENT.invoke(CRAFT_CHAT_MESSAGE_CLASS, prettyComponent));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getPretty_17(NBTCompound compound, String split) {
        Object nmsNBT = new NBTContainer(compound.toString()).getCompound();
        String s = split != null ? split : "";
        try {
            Object tagVisitorInstance = TEXT_TAG_VISITOR_CLASS.getConstructor(String.class, int.class).newInstance(s, 0);
            Object prettyComponent = VISIT_METHOD.invoke(tagVisitorInstance, nmsNBT);
            return ((String) FROM_COMPONENT.invoke(CRAFT_CHAT_MESSAGE_CLASS, prettyComponent));
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cache these classes/methods to prevent retrieving them too often
    private static final Class<?> CRAFT_CHAT_MESSAGE = ReflectionUtils.getOBCClass("util.CraftChatMessage");
    private static final Class<?> CRAFT_TEAM = ReflectionUtils.getOBCClass("scoreboard.CraftTeam");
    private static final Class<?> NMS_TEAM = ReflectionUtils.getNMSClass("ScoreboardTeam", "net.minecraft.world.scores");
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
    @SuppressWarnings("deprecation") // This is a Paper deprecation
    public static void setTeamSuffix(Team team, String suffix) {
        if (CRAFT_TEAM == null || PREFIX_COMP_METHOD == null || SET_PREFIX == null) {
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
