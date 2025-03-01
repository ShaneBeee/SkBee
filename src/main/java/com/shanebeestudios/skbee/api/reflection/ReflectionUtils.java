package com.shanebeestudios.skbee.api.reflection;

import ch.njol.skript.Skript;
import com.shanebeestudios.skbee.SkBee;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility methods for reflection
 */
@SuppressWarnings({"unused", "CallToPrintStackTrace"})
public class ReflectionUtils {

    private static final String CRAFTBUKKIT_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();
    private static final boolean MAPPED_SERVER = Skript.classExists("net.minecraft.world.level.Level");

    /**
     * Get a CraftBukkit class
     *
     * @param obcClassString String of craftbukkit class (everything after "org.bukkit.craftbukkit")
     * @return CraftBukkit class
     */
    public static @Nullable Class<?> getOBCClass(String obcClassString) {
        String name = CRAFTBUKKIT_PACKAGE + "." + obcClassString;
        return getClass(name);
    }

    /**
     * Get a Minecraft class
     *
     * @param nmsClass Path of class to get (ex: "net.minecraft.world.entity.Entity")
     * @return Minecraft class
     */
    public static @Nullable Class<?> getNMSClass(String nmsClass) {
        return getClass(nmsClass);
    }

    /**
     * Get a Minecraft class with an optional Bukkit mapping alternative
     *
     * @param nmsClass      Path of class to get (ex: "net.minecraft.world.entity.Entity")
     * @param bukkitMapping Optional Bukkit mapping for NMS class (just the class, not the package)
     * @return Minecraft class
     */
    public static @Nullable Class<?> getNMSClass(String nmsClass, String bukkitMapping) {
        if (!MAPPED_SERVER) {
            String split = nmsClass.substring(0, nmsClass.lastIndexOf("."));
            nmsClass = split + "." + bukkitMapping;
        }
        return getClass(nmsClass);
    }

    private static @Nullable Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            if (SkBee.isDebug()) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Get a Minecraft entity from a Bukkit entity
     *
     * @param entity Bukkit entity to convert
     * @return Minecraft entity
     */
    public static @Nullable Object getNMSEntity(Entity entity) {
        try {
            Method getHandle = entity.getClass().getMethod("getHandle");
            return getHandle.invoke(entity);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            if (SkBee.isDebug()) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Get the value of a field from an object
     *
     * @param field  Name of field
     * @param clazz  Class with field
     * @param object Object which contains field
     * @return Object from field
     */
    public static @Nullable Object getField(String field, Class<?> clazz, Object object) {
        try {
            Field f = clazz.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(object);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            if (SkBee.isDebug()) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Set the value of a field in an object
     *
     * @param field  Name of field to set
     * @param clazz  Class with field
     * @param object Object which holds field
     * @param toSet  Object to set
     */
    public static void setField(String field, Class<?> clazz, Object object, Object toSet) {
        try {
            Field f = clazz.getDeclaredField(field);
            f.setAccessible(true);
            f.set(object, toSet);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException ex) {
            if (SkBee.isDebug()) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Set the value of a field in an object
     *
     * @param field  Name of field to set
     * @param object Object which holds field
     * @param toSet  Object to set
     */
    public static void setField(String field, Object object, Object toSet) {
        try {
            Field f = object.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(object, toSet);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException ex) {
            if (SkBee.isDebug()) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Check if a class and method exist
     *
     * @param className  Class to check
     * @param methodName Method to check
     * @return True if both class and method exist
     */
    public static boolean methodExists(String className, String methodName) {
        if (Skript.classExists(className)) {
            try {
                Class<?> clazz = Class.forName(className);
                if (Skript.methodExists(clazz, methodName)) {
                    return true;
                }
            } catch (ClassNotFoundException ignore) {
            }
        }
        return false;
    }

}
