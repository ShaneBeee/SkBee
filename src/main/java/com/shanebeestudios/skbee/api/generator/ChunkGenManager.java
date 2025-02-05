package com.shanebeestudios.skbee.api.generator;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ChunkGenManager {

    private static final Map<String,ChunkGen> generators = new HashMap<>();

    public static ChunkGen registerOrGetGenerator(String id, boolean biome) {
        if (generators.containsKey(id)) return generators.get(id);
        ChunkGen chunkGen = new ChunkGen(id, biome);
        generators.put(id, chunkGen);
        return chunkGen;
    }

    @Nullable
    public static ChunkGen getByID(String name) {
        if (generators.containsKey(name)) return generators.get(name);
        return null;
    }

}
