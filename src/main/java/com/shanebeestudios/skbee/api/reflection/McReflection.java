package com.shanebeestudios.skbee.api.reflection;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class McReflection {

    private static final Class<?> ENTITY_NMS_CLASS = ReflectionUtils.getNMSClass("Entity", "net.minecraft.world.entity");
    private static final Class<?> CHAT_MESSAGE_CLASS = ReflectionUtils.getNMSClass("ChatMessage", "net.minecraft.network.chat");
    private static final Method GET_NMS_COPY_METHOD;

    static {
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
        ReflectionUtils.setField(ReflectionConstants.ENTITY_NO_CLIP_FIELD, ENTITY_NMS_CLASS, nmsEntity, !clip);
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean getClip(Entity entity) {
        if (entity == null || ENTITY_NMS_CLASS == null) return false;
        Object nmsEntity = ReflectionUtils.getNMSHandle(entity);
        if (nmsEntity == null) return false;
        return !Boolean.parseBoolean(ReflectionUtils.getField(ReflectionConstants.ENTITY_NO_CLIP_FIELD, ENTITY_NMS_CLASS, nmsEntity).toString());
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
                Method getName = nmsItemStack.getClass().getMethod(ReflectionConstants.NMS_ITEMSTACK_GET_HOVER_NAME_METHOD);
                Object name = getName.invoke(nmsItemStack);

                if (CHAT_MESSAGE_CLASS.isInstance(name)) {
                    Method getKey = CHAT_MESSAGE_CLASS.getMethod(ReflectionConstants.NMS_CHAT_MESSAGE_GET_KEY_METHOD);
                    return ((String) getKey.invoke(name));
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore) {
            }
        }
        return null;
    }

}
