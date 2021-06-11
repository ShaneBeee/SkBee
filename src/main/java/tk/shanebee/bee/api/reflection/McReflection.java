package tk.shanebee.bee.api.reflection;

import ch.njol.skript.Skript;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class McReflection {

    private static final boolean NEW_NMS = Skript.isRunningMinecraft(1, 17);

    private static final Class<?> ENTITY_NMS_CLASS;
    private static final String NO_CLIP = NEW_NMS ? "P" : "noclip"; // "P" will probably change in future versions
    private static final Class<?> CHAT_MESSAGE_CLASS;
    private static final Method GET_NMS_COPY_METHOD;

    static {
        if (NEW_NMS) {
            ENTITY_NMS_CLASS = ReflectionUtils.getNewNMSClass("net.minecraft.world.entity.Entity");
            CHAT_MESSAGE_CLASS = ReflectionUtils.getNewNMSClass("net.minecraft.network.chat.ChatMessage");
        } else {
            ENTITY_NMS_CLASS = ReflectionUtils.getNMSClass("Entity");
            CHAT_MESSAGE_CLASS = ReflectionUtils.getNMSClass("ChatMessage");
        }
        Method getNMSCopy1;
        try {
            Class<?> CraftItemStack = ReflectionUtils.getOBCClass("inventory.CraftItemStack");
            assert CraftItemStack != null;
            getNMSCopy1 = CraftItemStack.getMethod("asNMSCopy", ItemStack.class);
        } catch (NoSuchMethodException ignore) {
            getNMSCopy1 = null;
        }
        GET_NMS_COPY_METHOD = getNMSCopy1;
    }

    public static void setClip(Entity entity, boolean clip) {
        if (entity == null || ENTITY_NMS_CLASS == null) return;
        Object nmsEntity = ReflectionUtils.getNMSHandle(entity);
        if (nmsEntity == null) return;
        ReflectionUtils.setField(NO_CLIP, ENTITY_NMS_CLASS, nmsEntity, !clip);
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean getClip(Entity entity) {
        if (entity == null || ENTITY_NMS_CLASS == null) return false;
        Object nmsEntity = ReflectionUtils.getNMSHandle(entity);
        if (nmsEntity == null) return false;
        return !Boolean.parseBoolean(ReflectionUtils.getField(NO_CLIP, ENTITY_NMS_CLASS, nmsEntity).toString());
    }

    public static String getTranslateKey(ItemStack itemStack) {
        if (GET_NMS_COPY_METHOD == null || CHAT_MESSAGE_CLASS == null) return null;

        ItemStack itemStackClone = itemStack.clone();
        ItemMeta itemMeta = itemStackClone.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(null);
            itemStackClone.setItemMeta(itemMeta);

            try {
                Object nmsItemStack = GET_NMS_COPY_METHOD.invoke(null, itemStackClone);
                Method getName = nmsItemStack.getClass().getMethod("getName");
                Object name = getName.invoke(nmsItemStack);

                if (CHAT_MESSAGE_CLASS.isInstance(name)) {
                    Method getKey = CHAT_MESSAGE_CLASS.getMethod("getKey");
                    return ((String) getKey.invoke(name));
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore) {
            }
        }
        return null;
    }

}
