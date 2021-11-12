package com.shanebeestudios.skbee.api.structure;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class StructureBeeManager {

    private final Map<String,StructureBee> STRUCTURE_MAP = new HashMap<>();
    private final StructureManager STRUCTURE_MANAGER = Bukkit.getStructureManager();

    @NotNull
    public StructureBee getStructure(String name) {
        if (STRUCTURE_MAP.containsKey(name)) {
            return STRUCTURE_MAP.get(name);
        } else {
            NamespacedKey namespacedKey;
            if (name.contains(":")) {
                namespacedKey = NamespacedKey.fromString(name);
            } else {
                namespacedKey = NamespacedKey.minecraft(name);
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

}
