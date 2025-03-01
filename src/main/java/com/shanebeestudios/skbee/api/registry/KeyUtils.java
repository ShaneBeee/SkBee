package com.shanebeestudios.skbee.api.registry;

import ch.njol.skript.Skript;
import com.shanebeestudios.skbee.SkBee;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

/**
 * Utility methods for {@link Key}
 */
public class KeyUtils {

    /**
     * Get a {@link Key Minecraft Key} from string
     * <p>If a namespace is not provided, it will default to "minecraft:" namespace.
     * If an invalid key is provided, an error will send to console if debug is enabled</p>
     *
     * @param key Key for new Minecraft Key
     * @return new Minecraft NamespacedKey
     */
    public static Key getKey(@Nullable String key) {
        return getKey(key, SkBee.isDebug());
    }

    /**
     * Get a {@link Key Minecraft Key} from string
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

        key = key.toLowerCase();
        if (key.contains(" ")) {
            key = key.replace(" ", "_");
        }

        if (Key.parseable(key)) {
            return Key.key(key);
        } else {
            if (error)
                Skript.error("An invalid key was provided, that didn't follow [a-z0-9/._-:]. key: " + key);
            return null;
        }
    }

}
