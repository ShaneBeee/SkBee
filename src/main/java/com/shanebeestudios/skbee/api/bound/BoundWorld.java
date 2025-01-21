package com.shanebeestudios.skbee.api.bound;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a holder of {@link BoundRegion BoundRegions} for Worlds
 */
public class BoundWorld {

    private static final long SHIFT_VALUE = 8;

    private final World world;
    private final Map<Long, BoundRegion> regions = new HashMap<>();

    public BoundWorld(World world) {
        this.world = world;
    }

    /**
     * Get a {@link BoundRegion region} at a location
     *
     * @param location Location to check for region
     * @return BoundRegion if available, else null
     */
    public @Nullable BoundRegion getRegionAtLocation(Location location) {
        long id = getIdFromLocation(location);
        return this.regions.get(id);
    }

    /**
     * Get a {@link BoundRegion region} at a location
     * <br>Will create a region if one does not yet exist
     *
     * @param location Location to check for region
     * @return BoundRegion if available or a new one
     */
    public @NotNull BoundRegion getOrCreateRegionAtLocation(Location location) {
        long id = getIdFromLocation(location);
        BoundRegion boundRegion = this.regions.get(id);
        if (boundRegion == null) {
            boundRegion = new BoundRegion(id);
            this.regions.put(id, boundRegion);
        }
        return boundRegion;
    }

    private long getIdFromLocation(Location location) {
        long x = location.getBlockX() >> SHIFT_VALUE;
        long z = location.getBlockZ() >> SHIFT_VALUE;
        return x & 4294967295L | (z & 4294967295L) << 32;
    }

    /**
     * Add a bound to a region
     *
     * @param bound Bound to add to region
     */
    public void addBoundToRegion(Bound bound) {
        for (Location loc : getLocationList(bound)) {
            BoundRegion region = getOrCreateRegionAtLocation(loc);
            region.addBound(bound);
        }
    }

    /**
     * Remove a bound from a region
     * <br>If the region is empty after removal,
     * the region will be deleted
     *
     * @param bound Bound to remove
     */
    public void removeBoundFromRegion(Bound bound) {
        for (Location loc : getLocationList(bound)) {
            BoundRegion region = getRegionAtLocation(loc);
            if (region == null) continue;
            region.removeBound(bound);
            if (region.size() == 0) {
                this.regions.remove(region.getId());
            }
        }
    }

    /**
     * Get the points of a bound to regionalize
     * <br>This will be the 4 corners in most cases
     *
     * @param bound Bound to get corners from
     * @return List of locations representing the points of a bound to regionalize
     */
    private List<Location> getLocationList(Bound bound) {
        List<Location> locations = new ArrayList<>();

        BoundingBox box = bound.getBoundingBox();
        int minBlockX = box.getMin().getBlockX();
        int minBlockZ = box.getMin().getBlockZ();
        int maxBlockX = box.getMax().getBlockX();
        int maxBlockZ = box.getMax().getBlockZ();
        int shift = 1 << SHIFT_VALUE;
        for (int x = minBlockX; x <= (maxBlockX + shift); x += shift) {
            for (int z = minBlockZ; z <= (maxBlockZ + shift); z += shift) {
                locations.add(new Location(this.world, Math.min(x, maxBlockX), 0, Math.min(z, maxBlockZ)));
            }
        }

        return locations;
    }

    public List<Bound> getBoundsAtLocation(Location location) {
        BoundRegion region = getOrCreateRegionAtLocation(location);
        return region.getBounds();
    }

}
