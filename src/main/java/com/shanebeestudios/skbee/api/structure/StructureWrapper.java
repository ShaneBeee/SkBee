package com.shanebeestudios.skbee.api.structure;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.MathUtil;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.PDCWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Wrapper class for {@link Structure} that includes more information used for saving/placing
 */
@SuppressWarnings({"unused", "CallToPrintStackTrace"})
public class StructureWrapper {

    private static final StructureManager STRUCTURE_MANAGER = Bukkit.getStructureManager();
    private static final String ROTATION_KEY = "rotation";
    private static final String MIRROR_KEY = "mirror";
    private static final String INTEGRITY_KEY = "integrity";
    private static final String INCLUDE_ENTITIES_KEY = "includeEntities";
    private static final String LAST_SAVED_LOCATION_KEY = "lastSavedLocation";

    /**
     * Wrap a structure
     *
     * @param structure Structure to wrap
     * @param key       Key of structure to wrap
     * @return Wrapped structure
     */
    public static StructureWrapper wrap(Structure structure, NamespacedKey key) {
        return new StructureWrapper(structure, key);
    }

    private final Structure structure;
    private final NamespacedKey key;

    private StructureRotation rotation;
    private Mirror mirror;
    private float integrity = 1f;
    private boolean includeEntities = true;
    private final PDCWrapper pdcWrapper;
    private Location lastPlacedLocation;

    private StructureWrapper(Structure structure, NamespacedKey key) {
        this.key = key;
        this.structure = structure;
        this.pdcWrapper = PDCWrapper.wrap(structure);
        this.rotation = StructureRotation.values()[this.pdcWrapper.getByte(ROTATION_KEY)];
        this.mirror = Mirror.values()[this.pdcWrapper.getByte(MIRROR_KEY)];
        if (this.pdcWrapper.hasKey(INTEGRITY_KEY)) {
            this.integrity = MathUtil.clamp(this.pdcWrapper.getFloat(INTEGRITY_KEY), 0f, 1f);
        }
        if (this.pdcWrapper.hasKey(INCLUDE_ENTITIES_KEY)) {
            this.includeEntities = this.pdcWrapper.getBoolean(INCLUDE_ENTITIES_KEY);
        }
        this.lastPlacedLocation = this.pdcWrapper.getLocation(LAST_SAVED_LOCATION_KEY);
    }

    /**
     * Fill a structure between 2 locations
     *
     * @param location    Starting location
     * @param blockVector Vector for opposing corner
     */
    public void fill(Location location, BlockVector blockVector) {
        structure.fill(location, blockVector, true);
    }

    /**
     * Place the structure at a location
     *
     * @param location Location to player
     * @param palette  Which palette to place (-1 will be random)
     */
    public void place(Location location, int palette) {
        this.lastPlacedLocation = location;
        this.pdcWrapper.setLocation(LAST_SAVED_LOCATION_KEY, location);
        palette = MathUtil.clamp(palette, -1, this.structure.getPaletteCount() - 1);
        structure.place(location, includeEntities, rotation, mirror, palette, integrity, new Random());
    }

    /**
     * Place the structure at a location
     * <p>This will place with a random palette, see {@link #place(Location, int)}</p>
     *
     * @param location Location to player
     */
    public void place(Location location) {
        place(location, -1);
    }

    /**
     * Save structure to file
     */
    public void save() {
        try {
            STRUCTURE_MANAGER.saveStructure(key, structure);
        } catch (IOException e) {
            Util.skriptError("Could not save structure '%s', enable debug in SkBee config for more info.", getName());
            if (SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Delete structure
     */
    public void delete() {
        try {
            STRUCTURE_MANAGER.deleteStructure(key, true);
        } catch (IOException e) {
            Util.skriptError("Could not delete structure '%s', enable debug in SkBee config for more info.", getName());
            if (SkBee.getPlugin().getPluginConfig().SETTINGS_DEBUG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get a list of available {@link BlockState BlockStates} of this {@link Structure}
     * <br>
     * Will return an empty list of no blocks have been filled
     *
     * @return List of available BlockStates
     */
    public @Nullable List<BlockState> getBlockStates() {
        if (this.structure.getPaletteCount() > 0) {
            return this.structure.getPalettes().get(0).getBlocks();
        }
        return null;
    }

    /**
     * Get the instance of the {@link Structure Bukkit Structure}
     *
     * @return Bukkit structure
     */
    public Structure getBukkitStructure() {
        return structure;
    }

    /**
     * Get the name of this structure
     *
     * @return Name of structure
     */
    public String getName() {
        return key.getKey();
    }

    /**
     * Get the {@link NamespacedKey key} of this structure
     *
     * @return Key of this structure
     */
    public NamespacedKey getKey() {
        return key;
    }

    /**
     * Get the rotation of this structure
     *
     * @return Rotation of this structure
     */
    public StructureRotation getRotation() {
        return rotation;
    }

    /**
     * Set the rotation of this structure
     *
     * @param rotation Rotation of this structure
     */
    public void setRotation(StructureRotation rotation) {
        this.rotation = rotation;
        this.pdcWrapper.setByte(ROTATION_KEY, (byte) rotation.ordinal());
    }

    /**
     * Get the mirror of this structure
     *
     * @return Mirror of this structure
     */
    public Mirror getMirror() {
        return mirror;
    }

    /**
     * Set the mirror of this structure
     * <p>Represents how a Structure can be mirrored upon being loaded.</p>
     *
     * @param mirror mirror of this structure
     */
    public void setMirror(Mirror mirror) {
        this.mirror = mirror;
        this.pdcWrapper.setByte(MIRROR_KEY, ((byte) mirror.ordinal()));
    }

    /**
     * Get the integrity of this structure
     *
     * @return Integrity of this structure
     * @see #setIntegrity(float) for description of integrity
     */
    public float getIntegrity() {
        return integrity;
    }

    /**
     * Set the integrity of this structure
     * <p>
     * Determines how damaged the building should look by randomly skipping blocks to place.
     * This value can range from 0 to 1.
     * With 0 removing all blocks and 1 spawning the structure in pristine condition.
     * </p>
     *
     * @param integrity Integrity of this structure
     */
    public void setIntegrity(float integrity) {
        this.integrity = MathUtil.clamp(integrity, 0, 1);
        this.pdcWrapper.setFloat(INTEGRITY_KEY, this.integrity);
    }

    /**
     * Whether or not this structure is including entities
     *
     * @return Whether or not this structure is including entities
     */
    public boolean isIncludeEntities() {
        return includeEntities;
    }

    /**
     * Set whether this structure should include entities
     *
     * @param includeEntities Whether to include entities
     */
    public void setIncludeEntities(boolean includeEntities) {
        this.includeEntities = includeEntities;
        this.pdcWrapper.setBoolean(INCLUDE_ENTITIES_KEY, includeEntities);
    }

    /**
     * Get the size of this structure represented as a {@link BlockVector vector}
     *
     * @return Size of structure
     */
    public BlockVector getSize() {
        return this.structure.getSize();
    }

    /**
     * Get the last location this structure was placed at.
     * <p>This is persistent, and will only work with SkBee placing.</p>
     *
     * @return Last location the structure was placed at
     */
    public Location getLastPlacedLocation() {
        return this.lastPlacedLocation;
    }

    /**
     * Reset variables in this structure
     * (including mirror, rotation, integrity and inclusion of entities)
     */
    public void reset() {
        setMirror(Mirror.NONE);
        setRotation(StructureRotation.NONE);
        setIntegrity(1.0f);
        setIncludeEntities(true);
    }

    @Override
    public String toString() {
        return String.format("Structure{key=\"%s\", rotation=%s, mirror=%s, integrity=%s, includeEntities=%s (%s), size=[%s]}",
                key, rotation, mirror, integrity, includeEntities, structure.getEntityCount(), structure.getSize());
    }

}
