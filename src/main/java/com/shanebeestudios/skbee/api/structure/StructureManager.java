package com.shanebeestudios.skbee.api.structure;

import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.structure.Structure;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager class for structures
 */
public class StructureManager {

    private final Map<String, StructureWrapper> STRUCTURE_MAP = new HashMap<>();
    private final org.bukkit.structure.StructureManager BUKKIT_STRUCTURE_MANAGER = Bukkit.getStructureManager();

    public StructureWrapper getStructure(String name) {
        if (STRUCTURE_MAP.containsKey(name)) {
            StructureWrapper structureWrapper = STRUCTURE_MAP.get(name);
            // if attempting to load again, reset rotation/mirror
            structureWrapper.reset();
            return structureWrapper;
        } else {
            NamespacedKey namespacedKey = Util.getNamespacedKey(name, true);
            if (namespacedKey == null) {
                return null;
            }
            Structure structure = BUKKIT_STRUCTURE_MANAGER.loadStructure(namespacedKey, true);
            StructureWrapper structureWrapper;
            if (structure == null) {
                structure = BUKKIT_STRUCTURE_MANAGER.createStructure();
                BUKKIT_STRUCTURE_MANAGER.registerStructure(namespacedKey, structure);
            }
            structureWrapper = StructureWrapper.wrap(structure, namespacedKey);
            STRUCTURE_MAP.put(name, structureWrapper);
            return structureWrapper;
        }
    }

    public boolean structureExists(String name) {
        NamespacedKey namespacedKey = Util.getNamespacedKey(name, true);
        if (namespacedKey == null) {
            return false;
        }
        return BUKKIT_STRUCTURE_MANAGER.loadStructure(namespacedKey, false) != null;
    }

}
