package com.shanebeestudios.skbee.api.region;

import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

/**
 * Utility methods for Folia's threaded regions
 */
public class RegionUtils {

    /**
     * Check if an object is owned by the curent region
     * <p>Currently accepts Block/Location/Entity.
     * If not running Folia this will just return true</p>
     *
     * @param object Object to check
     * @return Whether object is running in the current region
     */
    public static boolean isOwnedByCurrentRegion(Object object) {
        if (!Util.IS_RUNNING_FOLIA) return true;

        if (object instanceof Block block) return Bukkit.isOwnedByCurrentRegion(block);
        else if (object instanceof Entity entity) return Bukkit.isOwnedByCurrentRegion(entity);
        else if (object instanceof Location location) return Bukkit.isOwnedByCurrentRegion(location);
        return true;
    }

    /**
     * Check if a chunk location is owned by a current region
     * <p>If not running Folia this will just return true</p>
     *
     * @param world  World of chunk
     * @param chunkX Chunk X of chunk
     * @param chunkZ Chunk Z of chunk
     * @return Whether chunk is running in the current region
     */
    public static boolean isOwnedByCurrentRegion(World world, int chunkX, int chunkZ) {
        if (!Util.IS_RUNNING_FOLIA) return true;
        return Bukkit.isOwnedByCurrentRegion(world, chunkX, chunkZ);
    }

}
