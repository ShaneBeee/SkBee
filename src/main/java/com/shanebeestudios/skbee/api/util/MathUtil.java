package com.shanebeestudios.skbee.api.util;

/**
 * Utility class for math operations
 */
public class MathUtil {

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

}
