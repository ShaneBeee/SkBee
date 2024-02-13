package com.shanebeestudios.skbee.api.util;

import ch.njol.skript.Skript;
import com.shanebeestudios.skbee.api.reflection.ReflectionConstants;
import com.shanebeestudios.skbee.api.reflection.ReflectionUtils;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Utility class for {@link Entity Entities}
 */
public class EntityUtils {

    private static final Class<?> ENTITY_NMS_CLASS = ReflectionUtils.getNMSClass("net.minecraft.world.entity.Entity");
    private static final boolean HAS_DAMAGE_SOURCE = Skript.classExists("org.bukkit.damage.DamageSource");
    private static final Constructor<EntityDamageEvent> DAMAGE_EVENT_CONSTRUCTOR;

    static {
        try {
            if (HAS_DAMAGE_SOURCE) {
                DAMAGE_EVENT_CONSTRUCTOR = null;
            } else {
                //noinspection JavaReflectionMemberAccess
                DAMAGE_EVENT_CONSTRUCTOR = EntityDamageEvent.class.getConstructor(Entity.class, EntityDamageEvent.DamageCause.class, double.class);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
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
     * Create an instance of EntityDamageEvent
     * <p>Bukkit went and added DamageSource to this event, no backwards compatability was kept</p>
     *
     * @param attacker     Who/what attacked
     * @param damageCause  Cause for damage
     * @param damageAmount Amount of damage applied
     * @return New instance of EntityDamageEvent
     */
    public static EntityDamageEvent createEntityDamageEvent(Entity attacker, EntityDamageEvent.DamageCause damageCause, double damageAmount) {
        if (HAS_DAMAGE_SOURCE) {
            DamageSource damageSource = DamageSource.builder(DamageType.GENERIC).withCausingEntity(attacker).build();
            return new EntityDamageEvent(attacker, damageCause, damageSource, damageAmount);
        } else {
            EntityDamageEvent entityDamageEvent;
            try {
                entityDamageEvent = DAMAGE_EVENT_CONSTRUCTOR.newInstance(attacker, damageCause, damageAmount);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            return entityDamageEvent;
        }
    }

}
