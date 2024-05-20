package com.shanebeestudios.skbee.api.generator.event;

import org.bukkit.generator.ChunkGenerator;

public class ChunkGenEvent extends BaseGenEvent {

    private final int chunkX;
    private final int chunkZ;
    private final ChunkGenerator.ChunkData chunkData;

    public ChunkGenEvent(ChunkGenerator.ChunkData chunkData, int chunkX, int chunkZ) {
        this.chunkData = chunkData;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public ChunkGenerator.ChunkData getChunkData() {
        return this.chunkData;
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

}
