package com.shanebeestudios.skbee.api.bound;

import com.google.common.base.Preconditions;
import com.shanebeestudios.skbee.api.util.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Reprents a bounding box object
 */
@SuppressWarnings("unused")
@SerializableAs("Bound")
public class Bound implements ConfigurationSerializable {

    private final String world;
    private String id;
    private final boolean temporary;
    private List<UUID> owners = new ArrayList<>();
    private List<UUID> members = new ArrayList<>();
    private Map<String, Object> values = new HashMap<>();
    private BoundingBox boundingBox;

    /**
     * Create a new global bound in a {@link World} with ID using a {@link BoundingBox}
     *
     * @param world       World this bound is in
     * @param id          ID of this bound
     * @param boundingBox BoundingBox of this bound
     */
    public Bound(String world, String id, BoundingBox boundingBox) {
        this(world, id, boundingBox, false);
    }

    /**
     * Create a new bound in a {@link World} with ID using a {@link BoundingBox}
     *
     * @param world       World this bound is in
     * @param id          ID of this bound
     * @param boundingBox BoundingBox of this bound
     * @param temporary   Whether this bound is temporary
     */
    public Bound(String world, String id, BoundingBox boundingBox, boolean temporary) {
        this.world = world;
        this.id = id;
        this.boundingBox = boundingBox;
        this.temporary = temporary;
    }

    /**
     * Create a new global bound between 2 locations (must be in same world)
     *
     * @param location  Location 1
     * @param location2 Location 2
     * @param id        ID of this bound
     */
    public Bound(Location location, Location location2, String id) {
        this(location, location2, id, false);
    }

    /**
     * Create a new bound between 2 locations (must be in same world)
     *
     * @param location  Location 1
     * @param location2 Location 2
     * @param id        ID of this bound
     * @param temporary Whether this bound is temporary
     */
    public Bound(Location location, Location location2, String id, boolean temporary) {
        Preconditions.checkArgument(location.getWorld() == location2.getWorld(), "Worlds have to match");
        this.world = location.getWorld().getName();
        this.id = id;
        Block block1 = location.getBlock();
        Block block2 = location2.getBlock();
        this.boundingBox = BoundingBox.of(block1, block2);
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
        if (w != null && w.getName().equals(world)) {
            return this.boundingBox.contains(loc.toVector());
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
        if (bound.world.equals(world)) {
            return boundingBox.overlaps(bound.boundingBox);
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
            return boundingBox.overlaps(l1.toVector(), l2.toVector());
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
        World world = getWorld();
        if (world == null) return null;
        Collection<Entity> nearbyEntities = world.getNearbyEntities(this.boundingBox, entity ->
                type.isAssignableFrom(entity.getClass()));
        return new ArrayList<>(nearbyEntities);
    }

    /**
     * Get a list of blocks within a bound
     *
     * @return List of blocks within bound
     */
    public List<Block> getBlocks() {
        World w = getWorld();
        if (w == null) return null;
        List<Block> array = new ArrayList<>();
        int minX = (int) boundingBox.getMinX();
        int minY = (int) boundingBox.getMinY();
        int minZ = (int) boundingBox.getMinZ();
        int maxX = (int) boundingBox.getMaxX();
        int maxY = (int) boundingBox.getMaxY();
        int maxZ = (int) boundingBox.getMaxZ();
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    Block b = w.getBlockAt(x, y, z);
                    array.add(b);
                }
            }
        }
        return array;
    }

    /**
     * Get the world of this bound
     *
     * @return World of this bound
     */
    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    /**
     * Get the greater corner of this bound
     *
     * @return Location of greater corner
     */
    public Location getGreaterCorner() {
        World world = getWorld();
        if (world == null) return null;
        return boundingBox.getMax().toLocation(world);
    }

    /**
     * Get the lesser corner of this bound
     *
     * @return Location of lesser corner
     */
    public Location getLesserCorner() {
        World world = getWorld();
        if (world == null) return null;
        return boundingBox.getMin().toLocation(world);
    }

    /**
     * Get the center location of this bound
     *
     * @return The center location
     */
    public Location getCenter() {
        World world = getWorld();
        if (world == null) return null;
        return boundingBox.getCenter().toLocation(world);
    }

    public int getLesserX() {
        return ((int) boundingBox.getMinX());
    }

    public void setLesserX(int x) {
        Vector min = this.boundingBox.getMin();
        min.setX(x);
        resize(min, this.boundingBox.getMax());
    }

    public int getLesserY() {
        return ((int) boundingBox.getMinY());
    }

    public void setLesserY(int y) {
        Vector min = this.boundingBox.getMin();
        min.setY(y);
        resize(min, this.boundingBox.getMax());
    }

    public int getLesserZ() {
        return ((int) boundingBox.getMinZ());
    }

    public void setLesserZ(int z) {
        Vector min = this.boundingBox.getMin();
        min.setZ(z);
        resize(min, this.boundingBox.getMax());
    }

    public int getGreaterX() {
        return ((int) boundingBox.getMaxX());
    }

    public void setGreaterX(int x2) {
        Vector max = this.boundingBox.getMax();
        max.setX(x2);
        resize(this.boundingBox.getMin(), max);
    }

    public int getGreaterY() {
        return ((int) boundingBox.getMaxY());
    }

    public void setGreaterY(int y2) {
        Vector max = this.boundingBox.getMax();
        max.setY(y2);
        resize(this.boundingBox.getMin(), max);
    }

    public int getGreaterZ() {
        return ((int) boundingBox.getMaxZ());
    }

    public void setGreaterZ(int z2) {
        Vector max = this.boundingBox.getMax();
        max.setZ(z2);
        resize(this.boundingBox.getMin(), max);
    }

    public void resize(Vector v1, Vector v2) {
        this.boundingBox = this.boundingBox.resize(v1.getX(), v1.getY(), v1.getZ(), v2.getX(), v2.getY(), v2.getZ());
    }

    public void resize(Location loc1, Location loc2) {
        Preconditions.checkArgument(loc1.getWorld() == loc2.getWorld(), "Worlds have to match");
        Preconditions.checkArgument(loc1.getWorld().getName().equalsIgnoreCase(this.world), "World cannot be changed!");
        Block block1 = loc1.getBlock();
        Block block2 = loc2.getBlock();
        this.boundingBox = BoundingBox.of(block1, block2);
    }

    /**
     * Make this bound a full bound
     * <p>Stretch from bottom to top of world</p>
     */
    public void makeFull() {
        World world = getWorld();
        Vector min = this.boundingBox.getMin().clone();
        Vector max = this.boundingBox.getMax().clone();
        min.setY(WorldUtils.getMinHeight(world));
        max.setY(WorldUtils.getMaxHeight(world));
        this.boundingBox = BoundingBox.of(min, max);
    }

    public void change(Axis axis, Corner corner, int amount) {
        if (axis == Axis.X) {
            if (corner == Corner.GREATER) {
                setGreaterX(getGreaterX() + amount);
            } else {
                setLesserX(getLesserX() + amount);
            }
        } else if (axis == Axis.Y) {
            if (corner == Corner.GREATER) {
                setGreaterY(getGreaterY() + amount);
            } else {
                setLesserY(getLesserY() + amount);
            }
        } else {
            if (corner == Corner.GREATER) {
                setGreaterZ(getGreaterZ() + amount);
            } else {
                setLesserZ(getLesserZ() + amount);
            }
        }
    }

    public enum Axis {
        X, Y, Z
    }

    public enum Corner {
        GREATER, LESSER
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
        this.owners = owners;
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
        this.members = members;
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
        this.values.put(key, value);
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
        return this.values.get(key);
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

    /**
     * Check if this bound is temporary
     *
     * @return True if this bound is temporary
     */
    public boolean isTemporary() {
        return temporary;
    }

    public String toString() {
        return this.id;
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

        result.put("id", id);
        result.put("world", world);
        result.put("boundingbox", boundingBox);

        List<String> owners = new ArrayList<>();
        this.owners.forEach(uuid -> owners.add(uuid.toString()));
        List<String> members = new ArrayList<>();
        this.members.forEach(uuid -> members.add(uuid.toString()));
        result.put("owners", owners);
        result.put("members", members);
        result.put("values", values);

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
        String world = ((String) args.get("world"));
        String id = String.valueOf(args.get("id"));
        Bound bound;
        if (args.containsKey("boundingbox")) {
            BoundingBox box = ((BoundingBox) args.get("boundingbox"));
            bound = new Bound(world, id, box);
        } else {
            int x = ((Number) args.get("x1")).intValue();
            int y = ((Number) args.get("y1")).intValue();
            int z = ((Number) args.get("z1")).intValue();
            int x2 = ((Number) args.get("x2")).intValue();
            int y2 = ((Number) args.get("y2")).intValue();
            int z2 = ((Number) args.get("z2")).intValue();
            BoundingBox box = new BoundingBox(x, y, z, x2, y2, z2);
            bound = new Bound(world, id, box);
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
