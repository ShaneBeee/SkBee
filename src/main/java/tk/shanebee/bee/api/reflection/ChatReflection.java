package tk.shanebee.bee.api.reflection;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Reflection class for chat related stuff
 */
public class ChatReflection {

    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

    private enum Ver {
        V_1_14_R1("v1_14_R1", "k"),
        V_1_15_R1("v1_15_R1", "l"),
        V_1_16_R1("v1_16_R1", "l"),
        V_1_16_R2("v1_16_R2", "l");

        private final String version;
        private final String pretty;

        Ver(String version, String pretty) {
            this.version = version;
            this.pretty = pretty;
        }

        private static String getPretty() {
            for (Ver value : values()) {
                if (value.version.equalsIgnoreCase(VERSION)) {
                    return value.pretty;
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
    public static String getPrettyNBT(NBTCompound compound) {
        String prettyM = Ver.getPretty();
        if (prettyM == null) return null;

        Object nmsNBT = compound.getCompound();
        Class<?> iChatBaseComponent = ReflectionUtils.getNMSClass("IChatBaseComponent");
        Class<?> craftChatMessageClass = ReflectionUtils.getOBCClass("util.CraftChatMessage");
        try {
            Method prettyMethod = nmsNBT.getClass().getMethod(prettyM);
            Object prettyComponent = prettyMethod.invoke(nmsNBT);
            assert craftChatMessageClass != null;
            Method fromComponent = craftChatMessageClass.getMethod("fromComponent", iChatBaseComponent);

            return ((String) fromComponent.invoke(craftChatMessageClass, prettyComponent));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

}
