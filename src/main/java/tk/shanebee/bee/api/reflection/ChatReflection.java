package tk.shanebee.bee.api.reflection;

import ch.njol.skript.Skript;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Reflection class for chat related stuff
 */
public class ChatReflection {

    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    private static final boolean NEW_PRETTY_NBT = Skript.isRunningMinecraft(1, 17);

    private enum Ver {
        V_1_13_R2("v1_13_R2", "k", "a"),
        V_1_14_R1("v1_14_R1", "k", "a"),
        V_1_15_R1("v1_15_R1", "l", "a"),
        V_1_16_R1("v1_16_R1", "l", "a"),
        V_1_16_R2("v1_16_R2", "l", "a"),
        V_1_16_R3("v1_16_R3", "l", "a");

        private final String version;
        private final String pretty;
        private final String prettySplit;

        Ver(String version, String pretty, String prettySplit) {
            this.version = version;
            this.pretty = pretty;
            this.prettySplit = prettySplit;
        }

        private static String getPretty(boolean split) {
            for (Ver value : values()) {
                if (value.version.equalsIgnoreCase(VERSION)) {
                    if (split) {
                        return value.prettySplit;
                    } else {
                        return value.pretty;
                    }
                }
            }
            return null;
        }
    }

    /**
     * Get a pretty NBT string
     * <p>This is the same as what vanilla Minecraft outputs when using the '/data' command</p>
     *
     * @param compound Compound to convert to pretty
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
    private static final Class<?> ICHAT_BASE_COMPONENT_CLASS;
    private static final Class<?> CRAFT_CHAT_MESSAGE_CLASS = ReflectionUtils.getOBCClass("util.CraftChatMessage");
    private static final Class<?> TEXT_TAG_VISITOR_CLASS;
    private static final Class<?> NBT_BASE_CLASS;
    private static final Method FROM_COMPONENT;
    private static final Method VISIT_METHOD;

    static {
        if (NEW_PRETTY_NBT) {
            ICHAT_BASE_COMPONENT_CLASS = ReflectionUtils.getNewNMSClass("net.minecraft.network.chat.IChatBaseComponent");
            TEXT_TAG_VISITOR_CLASS = ReflectionUtils.getNewNMSClass("net.minecraft.nbt.TextComponentTagVisitor");
            NBT_BASE_CLASS = ReflectionUtils.getNewNMSClass("net.minecraft.nbt.NBTBase");
        } else {
            ICHAT_BASE_COMPONENT_CLASS = ReflectionUtils.getNMSClass("IChatBaseComponent");
            NBT_BASE_CLASS = null;
            TEXT_TAG_VISITOR_CLASS = null;
        }
        Method from_comp = null;
        Method visit = null;
        try {
            if (NEW_PRETTY_NBT) {
                assert TEXT_TAG_VISITOR_CLASS != null;
                visit = TEXT_TAG_VISITOR_CLASS.getDeclaredMethod("a", NBT_BASE_CLASS);
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
        String prettyM = Ver.getPretty(split != null);
        if (prettyM == null) return null;

        Object nmsNBT = new NBTContainer(compound.toString()).getCompound();
        try {
            Method prettyMethod;
            Object prettyComponent;
            if (split != null) {
                prettyMethod = nmsNBT.getClass().getMethod(prettyM, String.class, int.class);
                prettyComponent = prettyMethod.invoke(nmsNBT, split, 0);
            } else {
                prettyMethod = nmsNBT.getClass().getMethod(prettyM);
                prettyComponent = prettyMethod.invoke(nmsNBT);
            }
            assert CRAFT_CHAT_MESSAGE_CLASS != null;
            return ((String) FROM_COMPONENT.invoke(CRAFT_CHAT_MESSAGE_CLASS, prettyComponent));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getPretty_17(NBTCompound compound, String split) {
        Object nmsNBT = compound.getCompound();
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
    private static final Class<?> NMS_TEAM;
    private static final Method SET_PREFIX;
    private static final Method PREFIX_COMP_METHOD;

    static {
        if (NEW_PRETTY_NBT) {
            NMS_TEAM = ReflectionUtils.getNewNMSClass("net.minecraft.world.scores.ScoreboardTeam");
        } else {
            NMS_TEAM = ReflectionUtils.getNMSClass("ScoreboardTeam");
        }
        Method PREFIX_COMP_METHOD1 = null;
        Method SET_PREFIX1 = null;
        if (CRAFT_TEAM != null && NMS_TEAM != null && CRAFT_CHAT_MESSAGE != null) {
            try {
                SET_PREFIX1 = NMS_TEAM.getDeclaredMethod("setPrefix", ICHAT_BASE_COMPONENT_CLASS);
                PREFIX_COMP_METHOD1 = CRAFT_CHAT_MESSAGE.getDeclaredMethod("fromStringOrNull", String.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        PREFIX_COMP_METHOD = PREFIX_COMP_METHOD1;
        SET_PREFIX = SET_PREFIX1;
    }

    /**
     * Util method for setting team prefixes on 1.13+ with no char limit
     *
     * @param team   Team to set prefix for
     * @param prefix Prefix to set
     */
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

}
