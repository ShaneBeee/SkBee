package tk.shanebee.bee.api.reflection;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Reflection class for chat related stuff
 */
public class ChatReflection {

    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

    private enum Ver {
        V_1_13_R2("v1_13_R2", "k", "a"),
        V_1_14_R1("v1_14_R1", "k", "a"),
        V_1_15_R1("v1_15_R1", "l", "a"),
        V_1_16_R1("v1_16_R1", "l", "a"),
        V_1_16_R2("v1_16_R2", "l", "a");

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
        String prettyM = Ver.getPretty(split != null);
        if (prettyM == null) return null;

        Object nmsNBT = new NBTContainer(compound.toString()).getCompound();
        Class<?> iChatBaseComponent = ReflectionUtils.getNMSClass("IChatBaseComponent");
        Class<?> craftChatMessageClass = ReflectionUtils.getOBCClass("util.CraftChatMessage");
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
            assert craftChatMessageClass != null;
            Method fromComponent = craftChatMessageClass.getMethod("fromComponent", iChatBaseComponent);

            return ((String) fromComponent.invoke(craftChatMessageClass, prettyComponent));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

}
