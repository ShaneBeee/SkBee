package com.shanebeestudios.skbee.api.util;

/**
 * Math utility class
 */
public class MathUtil {

    public static boolean isByte(Object object) {
        if (object instanceof Long || object instanceof Integer) {
            long l = ((Number) object).longValue();
            return l <= Byte.MAX_VALUE && l >= Byte.MIN_VALUE;
        }
        return false;
    }

    public static boolean isShort(Object object) {
        if (object instanceof Long || object instanceof Integer) {
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

}
