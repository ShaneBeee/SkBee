package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.Skript;
import ch.njol.skript.entity.EntityData;
import com.shanebeestudios.skbee.api.reflection.ReflectionConstants;
import com.shanebeestudios.skbee.api.reflection.ReflectionUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Utility class for {@link Entity Entities}
 */
public class EntityUtils {

    public static final boolean HAS_TRANSIENT = Skript.methodExists(AttributeInstance.class, "addTransientModifier", AttributeModifier.class);
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

    public static Predicate<Entity> filter(@Nullable LivingEntity livingEntity, @Nullable Object[] ignored) {
        return filterEntity -> {
            if (filterEntity == livingEntity) return false;
            if (ignored != null) {
                for (Object object : ignored) {
                    if (object instanceof Entity entity) {
                        if (filterEntity == entity) return false;
                    } else if (object instanceof EntityData<?> ed) {
                        if (ed.isInstance(filterEntity)) return false;
                    }
                }
            }
            return true;
        };
    }

    /**
     * Check if an entity already has an instance of an attribute modifier
     *
     * @param entity    Entity to check for modifier
     * @param attribute Attribute to check for modifier
     * @param modifier  Modifier to compare
     * @return True if modifier with same id/name already exists else false
     */
    public static boolean hasAttributeModifier(LivingEntity entity, Attribute attribute, AttributeModifier modifier) {
        AttributeInstance attributeInstance = entity.getAttribute(attribute);
        if (attributeInstance == null) return false;

        for (AttributeModifier attributeInstanceModifier : attributeInstance.getModifiers()) {
            if (ItemUtils.HAS_KEY && attributeInstanceModifier.getKey().equals(modifier.getKey())) {
                return true;
            } else if (attributeInstanceModifier.getName().equalsIgnoreCase(modifier.getName())) {
                return true;
            }
        }
        return false;
    }

}
