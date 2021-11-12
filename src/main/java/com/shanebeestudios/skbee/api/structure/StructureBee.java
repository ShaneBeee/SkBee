package com.shanebeestudios.skbee.api.structure;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.BlockVector;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;

import java.io.IOException;
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

    @Override
    public String toString() {
        return String.format("Structure{key=\"%s\", rotation=%s, mirror=%s, integrity=%s, includeEntities=%s (%s), size=[%s]}",
                key, rotation, mirror, integrity, includeEntities, structure.getEntityCount(), structure.getSize());
    }
}
