package com.shanebeestudios.skbee.api.nbt;

import com.shanebeestudios.skbee.SkBee;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface NBTCustom {

    NamespacedKey OLD_KEY = new NamespacedKey(SkBee.getPlugin(), "custom-nbt");
    String KEY = "skbee-custom";

    void deleteCustomNBT();

    @NotNull NBTCompound getCopy();

    default @NotNull NBTCompound getCustomNBT() {
        return getCustomNBT(true);
    }

    /**
     * Acquires the custom nbt compound for this object.
     * @param createTagIfMissing Whether to create an empty compound on this object if it's not already present.
     * @return A new compound. If {@code createTagIfMissing} is true, this is NotNull. If it is false, it is Nullable.
     */
    @Contract("true -> !null")
    NBTCompound getCustomNBT(boolean createTagIfMissing);

}
