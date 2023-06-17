package com.shanebeestudios.skbee.api.wrapper;

import com.shanebeestudios.skbee.SkBee;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for Bukkit {@link PersistentDataContainer PDC}
 * <p>Contains simplified methods for getting/setting values in PDCs</p>
 */
@SuppressWarnings("unused")
public class PDCWrapper {

    private static final PersistentDataType<String, String> STRING = PersistentDataType.STRING;
    private static final PersistentDataType<Integer, Integer> INT = PersistentDataType.INTEGER;
    private static final PersistentDataType<Byte, Byte> BYTE = PersistentDataType.BYTE;
    private static final PersistentDataType<int[], int[]> INT_ARRAY = PersistentDataType.INTEGER_ARRAY;

    /**
     * Create a wrapper from a persistent data holder
     *
     * @param holder Holder of data to wrap
     * @return New wrapper for PDC of holder
     */
    public static PDCWrapper wrap(PersistentDataHolder holder) {
        return new PDCWrapper(holder);
    }

    private final PersistentDataContainer container;

    private PDCWrapper(PersistentDataHolder holder) {
        this.container = holder.getPersistentDataContainer();
    }

    private NamespacedKey getKey(String key) {
        return new NamespacedKey(SkBee.getPlugin(), key);
    }

    private <T> void setValue(String key, PersistentDataType<T, T> type, T value) {
        NamespacedKey namespacedKey = getKey(key);
        this.container.set(namespacedKey, type, value);
    }

    private <T> T getValue(String key, PersistentDataType<T, T> type) {
        NamespacedKey namespacedKey = getKey(key);
        if (this.container.has(namespacedKey)) return this.container.get(namespacedKey, type);
        return null;
    }

    /**
     * Delete a value from the PDC
     *
     * @param key Key to remove
     */
    public void deleteKey(String key) {
        this.container.remove(getKey(key));
    }

    /**
     * Set a string
     *
     * @param key   Key to set
     * @param value String to be set
     */
    public void setString(String key, String value) {
        setValue(key, STRING, value);
    }

    /**
     * Get a string
     *
     * @param key Key of string to get
     * @return String from PDC
     */
    @Nullable
    public String getString(String key) {
        return getValue(key, STRING);
    }

    /**
     * Set an integer
     *
     * @param key   Key of integer to set
     * @param value Integer to set
     */
    public void setInt(String key, int value) {
        setValue(key, INT, value);
    }

    /**
     * Get an integer
     *
     * @param key Key of integer to get
     * @return Integer from PDC
     */
    public int getInt(String key) {
        Integer intValue = getValue(key, INT);
        if (intValue == null) return 0;
        return intValue;
    }

    /**
     * Set a byte
     *
     * @param key   Key of byte to set
     * @param value Byte to set
     */
    public void setByte(String key, byte value) {
        setValue(key, BYTE, value);
    }

    /**
     * Get a byte
     *
     * @param key Key of byte to get
     * @return Byte from PDC
     */
    public byte getByte(String key) {
        Byte byteValue = getValue(key, BYTE);
        if (byteValue == null) return 0;
        return byteValue;
    }

    /**
     * Set a location
     *
     * @param key      Key to set
     * @param location Location to set
     */
    public void setLocation(String key, @NotNull Location location) {
        int[] coords = new int[]{location.getBlockX(), location.getBlockY(), location.getBlockZ()};
        String world = location.getWorld().getName();
        setValue(key + ".coords", INT_ARRAY, coords);
        setValue(key + ".world", STRING, world);
    }

    /**
     * Get a location
     *
     * @param key Key of location
     * @return Location from PDC
     */
    @Nullable
    public Location getLocation(String key) {
        String worldName = getValue(key + ".world", STRING);
        if (worldName == null) return null;

        int[] ints = getValue(key + ".coords", INT_ARRAY);
        if (ints == null || ints.length != 3) return null;

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        return new Location(world, ints[0], ints[1], ints[2]);
    }

}
