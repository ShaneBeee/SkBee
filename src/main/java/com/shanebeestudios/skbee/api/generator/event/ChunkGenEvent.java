package com.shanebeestudios.skbee.api.generator.event;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.jetbrains.annotations.Nullable;

public class ChunkGenEvent extends BaseGenEvent {

    private final int chunkX;
    private final int chunkZ;
    private final ChunkData chunkData;
    private final ChunkGenerator chunkGenerator;

    public ChunkGenEvent(ChunkData chunkData, int chunkX, int chunkZ) {
        this.chunkData = chunkData;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.chunkGenerator = null;
    }

    public ChunkGenEvent(ChunkData chunkData, int chunkX, int chunkZ, ChunkGenerator chunkGenerator) {
        this.chunkData = chunkData;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.chunkGenerator = chunkGenerator;
    }

    public ChunkData getChunkData() {
        return this.chunkData;
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public @Nullable ChunkGenerator getChunkGenerator() {
        return this.chunkGenerator;
    }

}
