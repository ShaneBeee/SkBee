package com.shanebeestudios.skbee.api.worldgen;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager for custom chunk generators.
 */
public class ChunkGenManager {

    private static final Map<String, CustomChunkGenerator> GENERATORS = new HashMap<>();

    public static @NotNull CustomChunkGenerator registerOrGetGenerator(String id, boolean hasBiomeProvider) {
        if (GENERATORS.containsKey(id)) {
            return GENERATORS.get(id);
        }
        CustomChunkGenerator chunkGen = new CustomChunkGenerator(id, hasBiomeProvider);
        GENERATORS.put(id, chunkGen);
        return chunkGen;
    }

    @Nullable
    public static CustomChunkGenerator getByID(String name) {
        if (GENERATORS.containsKey(name)) {
            return GENERATORS.get(name);
        }
        return null;
    }

}
