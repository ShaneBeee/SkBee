package com.shanebeestudios.skbee.api.util;

import com.shanebeestudios.skbee.api.reflection.ReflectionConstants;
import com.shanebeestudios.skbee.api.reflection.ReflectionUtils;
import org.bukkit.entity.Entity;

/**
 * Utility class for {@link Entity Entities}
 */
public class EntityUtils {

    private static final Class<?> ENTITY_NMS_CLASS = ReflectionUtils.getNMSClass("net.minecraft.world.entity.Entity");

    public static void setNoPhysics(Entity entity, boolean clip) {
        if (entity == null || ENTITY_NMS_CLASS == null) return;
        Object nmsEntity = ReflectionUtils.getNMSEntity(entity);
        if (nmsEntity == null) return;
        ReflectionUtils.setField(ReflectionConstants.ENTITY_NO_PHYSICS_FIELD, ENTITY_NMS_CLASS, nmsEntity, clip);
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean getNoPhysics(Entity entity) {
        if (entity == null || ENTITY_NMS_CLASS == null) return false;
        Object nmsEntity = ReflectionUtils.getNMSEntity(entity);
        if (nmsEntity == null) return false;
        return Boolean.parseBoolean(ReflectionUtils.getField(ReflectionConstants.ENTITY_NO_PHYSICS_FIELD, ENTITY_NMS_CLASS, nmsEntity).toString());
    }

}
