package com.shanebeestudios.skbee.api.reflection;

import ch.njol.skript.Skript;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class McReflection {

    private static final Class<?> ENTITY_NMS_CLASS = ReflectionUtils.getNMSClass("Entity", "net.minecraft.world.entity");
    private static final boolean HAS_PAPER_TRANSLATION_KEY_METHOD = Skript.methodExists(ItemStack.class, "translationKey");

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
        // Paper has a semi-new method for this (added probably in 1.17.x)
        if (HAS_PAPER_TRANSLATION_KEY_METHOD) {
            return itemStack.translationKey();
        }
        return null;
    }

}
