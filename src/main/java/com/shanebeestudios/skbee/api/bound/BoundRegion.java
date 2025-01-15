package com.shanebeestudios.skbee.api.bound;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a region that holds {@link Bound bounds}.
 * <br>A bound region is 16x16 chunks in size.
 */
public class BoundRegion {

    private final List<Bound> bounds = new ArrayList<>();
    private final long id;

    public BoundRegion(long id) {
        this.id = id;
    }

    /**
     * Get a list of all bounds in this region
     *
     * @return List of bounds in region
     */
    public List<Bound> getBounds() {
        return this.bounds;
    }

    /**
     * Add a bound to this region
     *
     * @param bound Bound to add
     */
    public void addBound(Bound bound) {
        if (this.bounds.contains(bound)) return;
        this.bounds.add(bound);
    }

    /**
     * Remove a bound from this region
     *
     * @param bound Bound to remove
     */
    public void removeBound(Bound bound) {
        this.bounds.remove(bound);
    }

    /**
     * Get the size of all bounds in this region
     *
     * @return Size of bounds in this region
     */
    public int size() {
        return this.bounds.size();
    }

    /**
     * Get the ID of this region
     *
     * @return ID of region
     */
    public long getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "BoundRegion{id=" + id + ", size=" + bounds.size() + '}';
    }
}
