package com.shanebeestudios.skbee.api.structure;

import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;

import java.util.HashMap;
import java.util.Map;

public class StructureBeeManager {

    private final Map<String, StructureBee> STRUCTURE_MAP = new HashMap<>();
    private final StructureManager STRUCTURE_MANAGER = Bukkit.getStructureManager();

    public StructureBee getStructure(String name) {
        if (STRUCTURE_MAP.containsKey(name)) {
            StructureBee structureBee = STRUCTURE_MAP.get(name);
            // if attempting to load again, reset rotation/mirror
            structureBee.reset();
            return structureBee;
        } else {
            NamespacedKey namespacedKey = Util.getNamespacedKey(name, true);
            if (namespacedKey == null) {
                return null;
            }
            Structure structure = STRUCTURE_MANAGER.loadStructure(namespacedKey, true);
            StructureBee structureBee;
            if (structure == null) {
                structure = STRUCTURE_MANAGER.createStructure();
                STRUCTURE_MANAGER.registerStructure(namespacedKey, structure);
            }
            structureBee = new StructureBee(structure, namespacedKey);
            STRUCTURE_MAP.put(name, structureBee);
            return structureBee;
        }
    }

    public boolean structureExists(String name) {
        NamespacedKey namespacedKey = Util.getNamespacedKey(name, true);
        if (namespacedKey == null) {
            return false;
        }
        return STRUCTURE_MANAGER.loadStructure(namespacedKey, false) != null;
    }

}
