package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.Skript;
import ch.njol.skript.entity.EntityData;
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

    // Paper has this method
    public static final boolean HAS_TRANSIENT = Skript.methodExists(AttributeInstance.class, "addTransientModifier", AttributeModifier.class);

    public static Predicate<Entity> filter(@Nullable LivingEntity livingEntity, @Nullable Object[] filtered, boolean allow) {
        return filterEntity -> {
            if (livingEntity != null && filterEntity == livingEntity) return false;
            if (filtered != null) {
                for (Object object : filtered) {
                    if (object instanceof Entity entity) {
                        if (filterEntity == entity) return allow;
                    } else if (object instanceof EntityData<?> ed) {
                        if (ed.isInstance(filterEntity)) return allow;
                    }
                }
            }
            return !allow;
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
