package com.shanebeestudios.skbee.api.util;

import com.shanebeestudios.skbee.api.reflection.ReflectionConstants;
import com.shanebeestudios.skbee.api.reflection.ReflectionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for {@link Entity Entities}
 */
public class EntityUtils {

    private static final Class<?> ENTITY_NMS_CLASS = ReflectionUtils.getNMSClass("Entity", "net.minecraft.world.entity");
    private static final Map<Class<? extends Entity>, EntityType> ENTITY_TYPE_MAP = new HashMap<>();

    static {
        // Map EntityClass/EntityType since Skript's EntityData doesn't return EntityType
        for (EntityType entityType : EntityType.values()) {
            ENTITY_TYPE_MAP.put(entityType.getEntityClass(), entityType);
        }
    }

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

    /**
     * Get an {@link EntityType} from an {@link Entity EntityClass}
     * <p>Skript uses EntityData for types, which doesn't have a link back to type</p>
     *
     * @param entityClass EntityClass to grab type from
     * @return EntityType from EntityClass
     */
    @Nullable
    public static EntityType getByClass(@NotNull Class<? extends Entity> entityClass) {
        return ENTITY_TYPE_MAP.get(entityClass);
    }

}
