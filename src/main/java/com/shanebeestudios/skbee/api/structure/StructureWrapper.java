package com.shanebeestudios.skbee.api.structure;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.BlockStateWrapper;
import com.shanebeestudios.skbee.api.wrapper.PDCWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.structure.Palette;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.BlockVector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Wrapper class for {@link Structure} that includes more information used for saving/placing
 */
@SuppressWarnings("unused")
public class StructureWrapper {

    private static final StructureManager STRUCTURE_MANAGER = Bukkit.getStructureManager();
    private static final String PDC_KEY = "lastSavedLocation";

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

    private StructureRotation rotation = StructureRotation.NONE;
    private Mirror mirror = Mirror.NONE;
    private float integrity = 1f;
    private boolean includeEntities = true;
    private final PDCWrapper pdcWrapper;
    private Location lastPlacedLocation;

    private StructureWrapper(Structure structure, NamespacedKey key) {
        this.key = key;
        this.structure = structure;
        this.pdcWrapper = PDCWrapper.wrap(structure);
        this.lastPlacedLocation = this.pdcWrapper.getLocation(PDC_KEY);
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
     * @param location Location to playaer
     */
    public void place(Location location) {
        this.lastPlacedLocation = location;
        this.pdcWrapper.setLocation(PDC_KEY, location);
        structure.place(location, includeEntities, rotation, mirror, -1, integrity, new Random());
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
     * Get a list of available {@link BlockStateWrapper BlockStates} of this {@link Structure}
     * <br>
     * Will return an empty list of no blocks have been filled
     *
     * @return List of available BlockStates
     */
    public List<BlockStateWrapper> getBlockStates() {
        List<BlockStateWrapper> blocks = new ArrayList<>();
        if (structure.getPaletteCount() > 0) {
            Palette palette = structure.getPalettes().get(0);
            try {
                palette.getBlocks().forEach(blockState -> blocks.add(new BlockStateWrapper(blockState, true)));
            } catch (IllegalStateException ignore) {
                Util.log("Illegal block in palette of structure &r'&b" + this.key + "&r'");
            }
        }
        return blocks;
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
        this.integrity = integrity;
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
     * Set whether or not this structure should include entities
     *
     * @param includeEntities Whether to include entities
     */
    public void setIncludeEntities(boolean includeEntities) {
        this.includeEntities = includeEntities;
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
        this.mirror = Mirror.NONE;
        this.rotation = StructureRotation.NONE;
        this.integrity = 1.0f;
        this.includeEntities = true;
    }

    @Override
    public String toString() {
        return String.format("Structure{key=\"%s\", rotation=%s, mirror=%s, integrity=%s, includeEntities=%s (%s), size=[%s]}",
                key, rotation, mirror, integrity, includeEntities, structure.getEntityCount(), structure.getSize());
    }

}
