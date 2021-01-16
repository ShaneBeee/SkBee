package tk.shanebee.bee.api.reflection;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class McReflection {

    public static void setClip(Entity entity, boolean clip) {
        if (entity == null) return;
        Object nmsEntity = ReflectionUtils.getNMSHandle(entity);
        if (nmsEntity == null) return;
        Class<?> nmsClass = ReflectionUtils.getNMSClass("Entity");
        if (nmsClass == null) return;
        ReflectionUtils.setField("noclip", nmsClass, nmsEntity, !clip);
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean getClip(Entity entity) {
        if (entity == null) return false;
        Object nmsEntity = ReflectionUtils.getNMSHandle(entity);
        if (nmsEntity == null) return false;
        Class<?> nmsClass = ReflectionUtils.getNMSClass("Entity");
        if (nmsClass == null) return false;
        return !Boolean.parseBoolean(ReflectionUtils.getField("noclip", nmsClass, nmsEntity).toString());
    }

    private static final Class<?> ChatMessage = ReflectionUtils.getNMSClass("ChatMessage");
    private static final Method getNMSCopy;

    static {
        Method getNMSCopy1;
        try {
            Class<?> CraftItemStack = ReflectionUtils.getOBCClass("inventory.CraftItemStack");
            assert CraftItemStack != null;
            getNMSCopy1 = CraftItemStack.getMethod("asNMSCopy", ItemStack.class);
        } catch (NoSuchMethodException ignore) {
            getNMSCopy1 = null;
        }
        getNMSCopy = getNMSCopy1;
    }

    public static String getTranslateKey(ItemStack itemStack) {
        if (getNMSCopy == null || ChatMessage == null) return null;

        ItemStack itemStackClone = itemStack.clone();
        ItemMeta itemMeta = itemStackClone.getItemMeta();
        itemMeta.setDisplayName(null);
        itemStackClone.setItemMeta(itemMeta);

        try {
            Object nmsItemStack = getNMSCopy.invoke(null, itemStackClone);
            Method getName = nmsItemStack.getClass().getMethod("getName");
            Object name = getName.invoke(nmsItemStack);

            if (ChatMessage.isInstance(name)) {
                Method getKey = ChatMessage.getMethod("getKey");
                return ((String) getKey.invoke(name));
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore) {
        }
        return null;
    }

}
