package com.shanebeestudios.skbee.api.util;

import org.bukkit.Location;

/**
 * Math utility class
 */
public class MathUtil {

    public static boolean isByte(Object object) {
        if (object instanceof Byte) {
            return true;
        } else if (object instanceof Long || object instanceof Integer) {
            long l = ((Number) object).longValue();
            return l <= Byte.MAX_VALUE && l >= Byte.MIN_VALUE;
        }
        return false;
    }

    public static boolean isShort(Object object) {
        if (object instanceof Short) {
            return true;
        } else if (object instanceof Long || object instanceof Integer) {
            long l = ((Number) object).longValue();
            return l <= Short.MAX_VALUE && l >= Short.MIN_VALUE;
        }
        return false;
    }

    public static boolean isInt(Object object) {
        if (object instanceof Long || object instanceof Integer) {
            long l = ((Number) object).longValue();
            return l <= Integer.MAX_VALUE && l >= Integer.MIN_VALUE;
        }
        return false;
    }

    public static boolean isFloat(Object object) {
        if (object instanceof Float) {
            return true;
        }
        if (object instanceof Double) {
            double d = ((Number) object).doubleValue();
            return d <= Float.MAX_VALUE && d >= Float.MIN_VALUE;
        }
        return false;
    }

    /**
     * Check if a String is a Number
     *
     * @param string String to check
     * @return true if Number else false
     */
    public static boolean isNumber(String string) {
        return string.matches("\\d+");
    }

    /**
     * Check if a String is a boolean
     *
     * @param string String to check
     * @return true if Boolean else false
     */
    public static boolean isBoolean(String string) {
        return string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false");
    }

    public static boolean isWithin(Location loc, Location one, Location two) {
        double xLow = Math.min(one.getX(), two.getX());
        double yLow = Math.min(one.getY(), two.getY());
        double zLow = Math.min(one.getZ(), two.getZ());
        double xHigh = Math.max(one.getX(), two.getX());
        double yHigh = Math.max(one.getY(), two.getY());
        double zHigh = Math.max(one.getZ(), two.getZ());

        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        return x >= xLow && x <= xHigh && y >= yLow && y <= yHigh && z >= zLow && z <= zHigh;
    }

    public static float clamp(float num, float min, float max) {
        return Math.min(max, Math.max(num, min));
    }

    public static int clamp(int num, int min, int max) {
        return Math.min(max, Math.max(num, min));
    }

}
