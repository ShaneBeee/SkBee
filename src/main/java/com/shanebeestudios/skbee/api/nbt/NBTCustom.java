package com.shanebeestudios.skbee.api.nbt;

import com.shanebeestudios.skbee.SkBee;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public interface NBTCustom {

    NamespacedKey OLD_KEY = new NamespacedKey(SkBee.getPlugin(), "custom-nbt");
    String KEY = "skbee-custom";

    void deleteCustomNBT();

    @NotNull NBTCompound getCopy();

    @NotNull NBTCompound getCustomNBT();

}
