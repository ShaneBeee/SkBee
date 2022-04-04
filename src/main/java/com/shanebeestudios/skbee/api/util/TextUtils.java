package com.shanebeestudios.skbee.api.util;

import net.kyori.adventure.translation.Translatable;
import org.bukkit.entity.Entity;

/**
 * Utility class to handle text components
 */
public class TextUtils {

    public static String getTranslationKey(Object object) {
        if (object instanceof Translatable) {
            return ((Translatable) object).translationKey();
        } else if (object instanceof Entity) {
            return ((Entity) object).getType().translationKey();
        }
        return null;
    }

}
