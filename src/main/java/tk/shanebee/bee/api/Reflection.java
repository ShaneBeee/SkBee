package tk.shanebee.bee.api;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;

public class Reflection {

    private static Method setMetaMethod;
    private static Method getMetaMethod;

    private static boolean newMeta = (Skript.methodExists(ItemType.class, "setItemMeta", ItemMeta.class));

    static {
        try {
            Class<?> ITClass = Class.forName("ch.njol.skript.aliases.ItemType");
            setMetaMethod = ITClass.getDeclaredMethod("setItemMeta", Object.class);
            setMetaMethod.setAccessible(true);
            getMetaMethod = ITClass.getDeclaredMethod("getItemMeta");
            getMetaMethod.setAccessible(true);
        } catch (NoSuchMethodException | ClassNotFoundException ignore) {}
    }

    public static void setMeta(ItemType i, ItemMeta meta) {
        if (newMeta)
            i.setItemMeta(meta);
        else
            try {
                setMetaMethod.invoke(i, meta);
            } catch (Exception ignore) {}
    }

    public static ItemMeta getMeta(ItemType i) {
        if (newMeta)
            return i.getItemMeta();
        else
            try {
                return (ItemMeta) getMetaMethod.invoke(i);
            } catch (Exception ignore) {
                return null;
            }
    }
}
