package com.shanebeestudios.skbee.api.bound;

import com.google.common.base.Preconditions;
import com.shanebeestudios.skbee.api.wrapper.LazyLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Reprents a bounding box object
 */
@SuppressWarnings("unused")
@SerializableAs("Bound")
public class Bound implements ConfigurationSerializable {

    @Deprecated(forRemoval = true)
    private String worldName;

    private NamespacedKey worldKey;
    private String id;
    private final boolean temporary;
    private boolean full;
    private List<UUID> owners = new ArrayList<>();
    private List<UUID> members = new ArrayList<>();
    private Map<String, Object> values = new HashMap<>();
    private BoundingBox boundingBox;
    private BoundingBox fullBoundBoxCache;

    /**
     * @hidden only used to deserialize
     */
    @Deprecated(forRemoval = true)
    public Bound(String world, String id, BoundingBox boundingBox, boolean temporary) {
        this.worldName = world;
        this.id = id;
        this.boundingBox = boundingBox;
        this.temporary = temporary;
    }

    /**
     * @hidden only used to deserialize
     */
    public Bound(NamespacedKey worldKey, String id, BoundingBox boundingBox, boolean temporary) {
        this.worldKey = worldKey;
        this.id = id;
        this.boundingBox = boundingBox;
        this.temporary = temporary;
    }

    /**
     * Create a new bound between 2 locations (must be in same world)
     *
     * @param location  Location 1
     * @param location2 Location 2
     * @param id        ID of this bound
     * @param temporary Whether this bound is temporary
     */
    public Bound(Location location, Location location2, String id, boolean temporary, boolean usingBlocks) {
        Preconditions.checkArgument(location.getWorld() == location2.getWorld(), "Worlds have to match");
        this.worldKey = location.getWorld().getKey();
        this.id = id;
        if (usingBlocks) {
            Block block1 = location.getBlock();
            Block block2 = location2.getBlock();
            this.boundingBox = BoundingBox.of(block1, block2);
        } else {
            this.boundingBox = BoundingBox.of(location, location2);
        }
        this.temporary = temporary;
    }

    /**
     * Check if a location is within the region of this bound
     *
     * @param loc Location to check
     * @return True if location is within this bound
     */
    public boolean isInRegion(@NotNull Location loc) {
        World w = loc.getWorld();
        if (w != null && w.getName().equals(worldName)) {
            return getCachedBoundingBox().contains(loc.toVector());
        }
        return false;
    }

    /**
     * Check if this bound overlaps another bound
     *
     * @param bound Bound to check for overlapping
     * @return True if bound overlaps
     */
    public boolean overlaps(Bound bound) {
        if (bound.worldName.equals(worldName)) {
            return getCachedBoundingBox().overlaps(bound.getCachedBoundingBox());
        }
        return false;
    }

    /**
     * Check if this bound overlaps another potential bound within 2 locations
     *
     * @param l1 Location 1 of potential bound
     * @param l2 Location 2 of potential bound
     * @return True if bound overlaps
     */
    public boolean overlaps(Location l1, Location l2) {
        if (l1.getWorld() != null && l1.getWorld() == l2.getWorld()) {
            return getCachedBoundingBox().overlaps(l1.toVector(), l2.toVector());
        }
        return false;
    }

    /**
     * Get the entities within this Bound
     * <p>Note: If the chunk is unloaded, the entities will also be unloaded</p>
     *
     * @param type Type of entity to get
     * @return List of loaded entities in bound
     */
    public List<Entity> getEntities(Class<? extends Entity> type) {
        List<Entity> entities = new ArrayList<>();
        World world = getWorld();
        if (world != null) {
            BoundingBox box = getCachedBoundingBox();
            Collection<Entity> nearbyEntities = world.getNearbyEntities(box, entity ->
                type.isAssignableFrom(entity.getClass()));
            entities.addAll(nearbyEntities);
        }
        return entities;
    }

    /**
     * Get a list of blocks within a bound
     *
     * @return List of blocks within bound
     */
    public @NotNull List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<>();
        World w = getWorld();
        if (w == null) return blocks;

        Location min = getLesserCorner();
        Location max = getGreaterCorner();

        for (int x = min.getBlockX(); x < max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y < max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
                    blocks.add(w.getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    /**
     * Get the world of this bound
     *
     * @return World of this bound
     */
    @Nullable
    public World getWorld() {
        if (this.worldKey != null) {
            return Bukkit.getWorld(this.worldKey);
        } else {
            return Bukkit.getWorld(this.worldName);
        }
    }

    public NamespacedKey getWorldKey() {
        return this.worldKey;
    }

    @Deprecated(forRemoval = true)
    public @Nullable String getWorldName() {
        return this.worldName;
    }

    @Deprecated(forRemoval = true)
    public void updateKey() {
        if (this.worldKey != null) return;
        World world = getWorld();
        NamespacedKey key = world.getKey();
        this.worldKey = key;
    }

    /**
     * Get the greater corner of this bound
     *
     * @return Location of greater corner
     */
    public Location getGreaterCorner() {
        Vector max = getCachedBoundingBox().getMax();
        return new Location(getWorld(), max.getX(), max.getY(), max.getZ());
    }

    /**
     * Get the lesser corner of this bound
     *
     * @return Location of lesser corner
     */
    public Location getLesserCorner() {
        Vector min = getCachedBoundingBox().getMin();
        return new Location(getWorld(), min.getX(), min.getY(), min.getZ());
    }

    /**
     * Get the center location of this bound
     *
     * @return The center location
     */
    public Location getCenter() {
        Vector center = getCachedBoundingBox().getCenter();
        return new Location(getWorld(), center.getX(), center.getY(), center.getZ());
    }

    public void resize(Location loc1, Location loc2) {
        resize(loc1, loc2, false);
    }

    public void resize(Location loc1, Location loc2, boolean usingBlocks) {
        Preconditions.checkArgument(loc1.getWorld() == loc2.getWorld(), "Worlds have to match");
        Preconditions.checkArgument(loc1.getWorld().getName().equalsIgnoreCase(this.worldName), "World cannot be changed!");
        if (usingBlocks) {
            this.boundingBox = BoundingBox.of(loc1.getBlock(), loc2.getBlock());
        } else {
            this.boundingBox = BoundingBox.of(loc1, loc2);
        }
        // Reset full bound cache
        this.fullBoundBoxCache = null;
    }

    /**
     * Create a copy of a bound
     *
     * @param newId ID of new bound
     * @return New cloned bound
     */
    public Bound copy(String newId) {
        Location lesserCorner = this.getLesserCorner().clone();
        Location greaterCorner = this.getGreaterCorner().clone();
        Bound newBound = new Bound(this.worldKey, newId, this.boundingBox.clone(), this.temporary);
        newBound.setOwners(this.getOwners());
        newBound.setMembers(this.getMembers());
        newBound.values = this.values;
        return newBound;
    }

    /**
     * Get the ID of this bound
     *
     * @return ID of this bound
     */
    public String getId() {
        return id;
    }

    /**
     * Se the ID of this bound
     *
     * @param id New ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get a list of owners of this bound
     *
     * @return List of owners of this bound
     */
    public List<UUID> getOwners() {
        return owners;
    }

    /**
     * Set the owners of this bound
     *
     * @param owners Owners to set
     */
    public void setOwners(List<UUID> owners) {
        this.owners = new ArrayList<>(owners);
    }

    /**
     * Clear the owners of this bound
     */
    public void clearOwners() {
        this.owners.clear();
    }

    /**
     * Add an owner to this obund
     *
     * @param owner Owner to add
     */
    public void addOwner(UUID owner) {
        if (!this.owners.contains(owner)) {
            this.owners.add(owner);
        }
    }

    /**
     * Remove an owner from this bound
     *
     * @param owner Owner to remove
     */
    public void removeOwner(UUID owner) {
        this.owners.remove(owner);
    }

    /**
     * Get a list of members of this bound
     *
     * @return List of members
     */
    public List<UUID> getMembers() {
        return members;
    }

    /**
     * Set the members of this bound
     *
     * @param members Members to set
     */
    public void setMembers(List<UUID> members) {
        this.members = new ArrayList<>(members);
    }

    /**
     * Clear the members of this bound
     */
    public void clearMembers() {
        this.members.clear();
    }

    /**
     * Add a member to this bound
     *
     * @param member Member to add
     */
    public void addMember(UUID member) {
        if (!this.members.contains(member)) {
            this.members.add(member);
        }
    }

    /**
     * Remove a member from this bound
     *
     * @param member Member to remove
     */
    public void removeMember(UUID member) {
        this.members.remove(member);
    }

    /**
     * Set a custom value of this bound
     *
     * @param key   Key of value to set
     * @param value Value to set
     */
    public void setValue(String key, Object value) {
        if (value instanceof Location location) {
            this.values.put(key, new LazyLocation(location));
        } else {
            this.values.put(key, value);
        }
    }

    /**
     * Delete a custom value of this bound
     *
     * @param key Key of value to delete
     */
    public void deleteValue(String key) {
        this.values.remove(key);
    }

    /**
     * Clear all custom values of this bound
     */
    public void clearValues() {
        this.values = new HashMap<>();
    }

    /**
     * Get a custom value of this bound
     *
     * @param key Key of the value to get
     * @return Value from bound
     */
    public Object getValue(String key) {
        Object o = this.values.get(key);
        if (o instanceof LazyLocation lazyLocation) return lazyLocation.getLocation();
        return o;
    }

    /**
     * Get a map of all custom values of this bound
     *
     * @return Map of all custom values of this bound
     */
    public Map<String, Object> getValues() {
        return values;
    }

    /**
     * Get the instance of the Bukkit {@link BoundingBox}
     *
     * @return Bukkit BoundingBox
     */
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    private BoundingBox getCachedBoundingBox() {
        if (this.isFull()) {
            if (this.fullBoundBoxCache != null) return this.fullBoundBoxCache;
            BoundingBox box = this.boundingBox.clone();
            World world = getWorld();
            int minY = world != null ? world.getMinHeight() : 0;
            int maxY = world != null ? world.getMaxHeight() - 1 : 255;
            this.fullBoundBoxCache = box.resize(box.getMinX(), minY, box.getMinZ(), box.getMaxX(), maxY, box.getMaxZ());
            return this.fullBoundBoxCache;
        }
        return this.boundingBox;
    }

    /**
     * Check if this bound is temporary
     *
     * @return True if this bound is temporary
     */
    public boolean isTemporary() {
        return temporary;
    }

    /**
     * Check if this bound is full
     *
     * @return Whether bound is full
     */
    public boolean isFull() {
        return this.full;
    }

    /**
     * Set if this bound is full
     *
     * @param full Whether the bound is full
     */
    public void setFull(boolean full) {
        this.full = full;
    }

    public String toString() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Bound other)) return false;
        return this.worldName.equals(other.worldName) && this.id.equals(other.id);
    }

    /**
     * Serialize this bound into yaml configuration
     *
     * @return Yaml configuration serialization of this bound
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("id", this.id);
        if (this.worldKey != null) {
            result.put("world_key", this.worldKey.toString());
        } else {
            // TODO remove world name stuff in the future (feb 18/2026)
            result.put("world_name", this.worldName);
        }
        result.put("boundingbox", this.boundingBox);
        result.put("full", this.full);

        List<String> owners = new ArrayList<>();
        this.owners.forEach(uuid -> owners.add(uuid.toString()));
        List<String> members = new ArrayList<>();
        this.members.forEach(uuid -> members.add(uuid.toString()));
        result.put("owners", owners);
        result.put("members", members);
        this.values.values().removeIf(Objects::isNull);
        result.put("values", this.values);

        return result;
    }

    /**
     * Deserialize this bound from yaml configuration
     *
     * @param args Args from yaml
     * @return New bound from serialized yaml configuration
     */
    @SuppressWarnings("unchecked")
    public static Bound deserialize(Map<String, Object> args) {

        String id = String.valueOf(args.get("id"));
        BoundingBox box = ((BoundingBox) args.get("boundingbox"));
        Bound bound;

        // TODO remove world name stuff in the future (feb 18/2026)
        if (args.containsKey("world")) {
            String world = ((String) args.get("world"));
            bound = new Bound(world, id, box, false);
        } else if (args.containsKey("world_name")) {
            String world = ((String) args.get("world_name"));
            bound = new Bound(world, id, box, false);
        } else if (args.containsKey("world_key")) {
            String world = ((String) args.get("world_key"));
            NamespacedKey key = NamespacedKey.fromString(world);
            bound = new Bound(key, id, box, false);
        } else {
            throw new IllegalStateException("Bound with id '" + id + "' is missing a world.");
        }


        if (args.containsKey("full")) {
            bound.setFull((Boolean) args.get("full"));
        }

        if (args.containsKey("owners")) {
            List<String> owners = (List<String>) args.get("owners");
            List<UUID> ownerUUIDs = new ArrayList<>();
            owners.forEach(owner -> {
                UUID uuid = UUID.fromString(owner);
                ownerUUIDs.add(uuid);
            });
            bound.setOwners(ownerUUIDs);
        }

        if (args.containsKey("members")) {
            List<String> members = (List<String>) args.get("members");
            List<UUID> memberUUIDs = new ArrayList<>();
            members.forEach(member -> {
                UUID uuid = UUID.fromString(member);
                memberUUIDs.add(uuid);
            });
            bound.setMembers(memberUUIDs);
        }

        if (args.containsKey("values")) {
            bound.values = (Map<String, Object>) args.get("values");
        }

        return bound;
    }

}
