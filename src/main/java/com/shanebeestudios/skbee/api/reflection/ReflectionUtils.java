package com.shanebeestudios.skbee.api.reflection;

import ch.njol.skript.Skript;
import com.shanebeestudios.skbee.SkBee;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("unused")
public class ReflectionUtils {

    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
    private static final boolean NEW_NMS = Skript.isRunningMinecraft(1, 17);
    private static final boolean DEBUG = SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG;

    public static Class<?> getOBCClass(String obcClassString) {
        String name = "org.bukkit.craftbukkit." + VERSION + obcClassString;
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static Class<?> getNMSClass(String nmsClass, String nmsPackage) {
        try {
            if (NEW_NMS) {
                return Class.forName(nmsPackage + "." + nmsClass);
            } else {
                return Class.forName("net.minecraft.server." + VERSION + nmsClass);
            }
        } catch (ClassNotFoundException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static Object getNMSEntity(Entity entity) {
        try {
            Method getHandle = entity.getClass().getMethod("getHandle");
            return getHandle.invoke(entity);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static Object getField(String field, Class<?> clazz, Object object) {
        try {
            Field f = clazz.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(object);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    public static void setField(String field, Class<?> clazz, Object object, Object toSet) {
        try {
            Field f = clazz.getDeclaredField(field);
            f.setAccessible(true);
            f.set(object, toSet);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
        }
    }

    public static void setField(String field, Object object, Object toSet) {
        try {
            Field f = object.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(object, toSet);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException ex) {
            if (DEBUG) {
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
