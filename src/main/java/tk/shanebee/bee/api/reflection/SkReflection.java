package tk.shanebee.bee.api.reflection;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class SkReflection {

    private static Method setMetaMethod;
    private static Method getMetaMethod;

    private static final boolean newMeta = (Skript.methodExists(ItemType.class, "setItemMeta", ItemMeta.class));

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

    @Nullable
    public static ItemMeta getMeta(ItemType i) {
        if (newMeta)
            return i.getItemMeta();
        else
            try {
                ItemMeta itemMeta = (ItemMeta) getMetaMethod.invoke(i);
                if (itemMeta == null) {
                    ItemStack itemStack = i.getRandom();
                    if (itemStack != null) {
                        itemMeta = itemStack.getItemMeta();
                    }
                }
                return itemMeta;
            } catch (Exception ignore) {
                return null;
            }
    }

}
