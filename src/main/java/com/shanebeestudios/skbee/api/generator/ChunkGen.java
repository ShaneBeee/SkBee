package com.shanebeestudios.skbee.api.generator;

import org.jetbrains.annotations.Nullable;

public class ChunkGen {

    private final String id;
    private final ChunkGenerator chunkGenerator;
    private final BiomeGenerator biomeGenerator;

    public ChunkGen(String id, boolean chunk, boolean biome) {
        this.id = id;
        this.chunkGenerator = chunk ? new ChunkGenerator() : null;
        this.biomeGenerator = biome ? new BiomeGenerator() : null;
    }

    public String getId() {
        return this.id;
    }

    @Nullable
    public ChunkGenerator getChunkGenerator() {
        return this.chunkGenerator;
    }

    @Nullable
    public BiomeGenerator getBiomeGenerator() {
        return this.biomeGenerator;
    }

}
