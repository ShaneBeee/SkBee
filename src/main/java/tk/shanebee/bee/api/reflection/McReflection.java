package tk.shanebee.bee.api.reflection;

import org.bukkit.entity.Entity;

public class McReflection {

    public static void setClip(Entity entity, boolean clip) {
        if (entity == null) return;
        Object nmsEntity = ReflectionUtils.getNMSHandle(entity);
        if (nmsEntity == null) return;
        Class<?> nmsClass = ReflectionUtils.getNMSClass("Entity");
        if (nmsClass == null) return;
        ReflectionUtils.setField("noclip", nmsClass, nmsEntity, !clip);
    }

}
