package com.shanebeestudios.skbee.api.registry;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class RegistryUtils {

    private static final RegistryAccess REGISTRY_ACCESS = RegistryAccess.registryAccess();

    /**
     * Get a {@link Registry} from a {@link RegistryKey}
     *
     * @param registryKey Key of registry to get
     * @param <T>         Type of registry
     * @return Registry from key
     */
    @SuppressWarnings("NullableProblems")
    @NotNull
    public static <T extends Keyed> Registry<T> getRegistry(RegistryKey<T> registryKey) {
        return getRegistryAccess().getRegistry(registryKey);
    }

    /**
     * Quick method to get {@link RegistryAccess}
     *
     * @return Instance of RegistryAccess
     */
    public static RegistryAccess getRegistryAccess() {
        return REGISTRY_ACCESS;
    }

    @SuppressWarnings("NullableProblems")
    public static @Nullable <T extends Keyed> RegistryKeySet<T> getKeySet(Tag<?> bukkitTag, RegistryKey<T> registryKey) {
        Registry<T> registry = getRegistry(registryKey);
        TagKey<T> tagKey = TagKey.create(registryKey, bukkitTag.key());
        if (registry.hasTag(tagKey)) {
            return registry.getTag(tagKey);
        }
        return null;
    }

}
