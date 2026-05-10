package com.shanebeestudios.skbee.api.nbt;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Base interface for classes that override NBT-API classes.
 */
public interface NBTCustom {

    /**
     * Represents the custom NBT key for storage.
     */
    String KEY = "skbee-custom";

    /**
     * Delete custom NBT data from this object.
     */
    void deleteCustomNBT();

    /**
     * Get a copy of the NBT of this object.
     * <p>
     * Modifying this will have no effect on the original NBT.
     * </p>
     *
     * @return Copy of NBT.
     */
    @NotNull NBTCompound getCopy();

    /**
     * Acquires the custom NBT compound for this object.
     *
     * @return Custom NBT compound.
     */
    default @NotNull NBTCompound getCustomNBT() {
        return getCustomNBT(true);
    }

    /**
     * Acquires the custom NBT compound for this object.
     *
     * @param createTagIfMissing Whether to create an empty compound on this object if it's not already present.
     * @return A new compound. If {@code createTagIfMissing} is true, this is NotNull. If it is false, it is Nullable.
     */
    @Contract("true -> !null")
    NBTCompound getCustomNBT(boolean createTagIfMissing);

}
