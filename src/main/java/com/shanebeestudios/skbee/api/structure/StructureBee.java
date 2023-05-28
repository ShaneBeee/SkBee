package com.shanebeestudios.skbee.api.structure;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
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
public class StructureBee {

    private static final StructureManager STRUCTURE_MANAGER = Bukkit.getStructureManager();

    private final Structure structure;
    private final NamespacedKey key;

    private StructureRotation rotation = StructureRotation.NONE;
    private Mirror mirror = Mirror.NONE;
    private float integrity = 1f;
    private boolean includeEntities = true;

    public StructureBee(Structure structure, NamespacedKey key) {
        this.key = key;
        this.structure = structure;
    }

    public void fill(Location location, BlockVector blockVector) {
        structure.fill(location, blockVector, true);
    }

    public void place(Location location) {
        structure.place(location, includeEntities, rotation, mirror, -1, integrity, new Random());
    }

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
     * Get a list of available {@link BlockStateBee BlockStates} of this {@link Structure}
     * <br>
     * Will return an empty list of no blocks have been filled
     *
     * @return List of available BlockStates
     */
    public List<BlockStateBee> getBlockStates() {
        List<BlockStateBee> blocks = new ArrayList<>();
        if (structure.getPaletteCount() > 0) {
            Palette palette = structure.getPalettes().get(0);
            try {
                palette.getBlocks().forEach(blockState -> blocks.add(new BlockStateBee(blockState)));
            } catch (IllegalStateException ignore) {
                Util.log("Illegal block in palette of structure &r'&b" + this.key + "&r'");
            }
        }
        return blocks;
    }

    public Structure getBukkitStructure() {
        return structure;
    }

    public String getName() {
        return key.getKey();
    }

    public NamespacedKey getKey() {
        return key;
    }

    public StructureRotation getRotation() {
        return rotation;
    }

    public void setRotation(StructureRotation rotation) {
        this.rotation = rotation;
    }

    public Mirror getMirror() {
        return mirror;
    }

    public void setMirror(Mirror mirror) {
        this.mirror = mirror;
    }

    public float getIntegrity() {
        return integrity;
    }

    public void setIntegrity(float integrity) {
        this.integrity = integrity;
    }

    public boolean isIncludeEntities() {
        return includeEntities;
    }

    public void setIncludeEntities(boolean includeEntities) {
        this.includeEntities = includeEntities;
    }

    public BlockVector getSize() {
        return this.structure.getSize();
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
