package com.shanebeestudios.skbee.api.wrapper;

import com.shanebeestudios.skbee.SkBee;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

@SuppressWarnings("unused")
public class PDCWrapper {

    private static final PersistentDataType<String, String> STRING = PersistentDataType.STRING;
    private static final PersistentDataType<Integer, Integer> INT = PersistentDataType.INTEGER;
    private static final PersistentDataType<int[], int[]> INT_ARRAY = PersistentDataType.INTEGER_ARRAY;

    public static PDCWrapper wrap(PersistentDataHolder holder) {
        return new PDCWrapper(holder);
    }

    private final PersistentDataContainer container;

    public PDCWrapper(PersistentDataHolder holder) {
        this.container = holder.getPersistentDataContainer();
    }

    private NamespacedKey getKey(String key) {
        return new NamespacedKey(SkBee.getPlugin(), key);
    }

    public void setString(String key, String value) {
        container.set(getKey(key), STRING, value);
    }

    public String getString(String key) {
        if (container.has(getKey(key), STRING)) {
            return container.get(getKey(key), STRING);
        }
        return null;
    }

    public void setInt(String key, int value) {
        container.set(getKey(key), INT, value);
    }

    public int getInt(String key) {
        if (container.has(getKey(key), INT)) {
            Integer integer = container.get(getKey(key), INT);
            if (integer != null) {
                return integer;
            }
        }
        return 0;
    }

    public void setLocation(String key, Location location) {
        int[] coords = new int[]{location.getBlockX(), location.getBlockY(), location.getBlockY()};
        String world = location.getWorld().getName();
        container.set(getKey(key + ".coords"), INT_ARRAY, coords);
        container.set(getKey(key + ".world"), STRING, world);
    }

    public Location getLocation(String key) {
        NamespacedKey worldKey = getKey(key + ".world");
        if (container.has(worldKey, STRING)) {
            String worldName = container.get(worldKey, STRING);
            assert worldName != null;
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                int[] ints = container.get(getKey(key + ".coords"), INT_ARRAY);
                if (ints != null && ints.length == 3) {
                    return new Location(world, ints[0], ints[1], ints[2]);
                }
            }
        }
        return null;
    }

}
