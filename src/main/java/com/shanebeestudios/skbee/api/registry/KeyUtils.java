package com.shanebeestudios.skbee.api.registry;

import com.shanebeestudios.skbee.api.util.Util;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

/**
 * Utility methods for {@link Key}
 */
public class KeyUtils {

    /**
     * Gets a {@link Key Minecraft Key} from string
     * <p>If a namespace is not provided, it will default to "minecraft:" namespace.
     * Will not send an error to console.</p>
     *
     * @param key Key for new Minecraft Key
     * @return new Minecraft NamespacedKey
     */
    public static Key getKey(@Nullable String key) {
        return getKey(key, false);
    }

    /**
     * Gets a {@link Key Minecraft Key} from string
     * <p>If a namespace is not provided, it will default to "minecraft:" namespace</p>
     *
     * @param key   Key for new Minecraft Key
     * @param error Whether to send a Skript/console error if one occurs
     * @return new Minecraft NamespacedKey
     */
    @SuppressWarnings("PatternValidation")
    @Nullable
    public static Key getKey(@Nullable String key, boolean error) {
        if (key == null) return null;
        if (!key.contains(":")) key = "minecraft:" + key;
        if (key.length() > 255) {
            if (error)
                Util.skriptError("An invalid key was provided, key must be less than 256 characters: %s", key);
            return null;
        }
        key = key.toLowerCase();
        if (key.contains(" ")) {
            key = key.replace(" ", "_");
        }

        try {
            return Key.key(key);
        } catch (InvalidKeyException ignore) {
            if (error)
                Util.skriptError("An invalid key was provided, that didn't follow [a-z0-9/._-:]. key: %s", key);
            return null;
        }
    }

}
