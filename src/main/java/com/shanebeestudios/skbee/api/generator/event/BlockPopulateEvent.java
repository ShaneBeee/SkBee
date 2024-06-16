package com.shanebeestudios.skbee.api.generator.event;

import org.bukkit.generator.LimitedRegion;

import java.util.Random;

public class BlockPopulateEvent extends BaseGenEvent {

    private final LimitedRegion limitedRegion;
    private final int chunkX, chunkZ;
    private final Random random;

    public BlockPopulateEvent(LimitedRegion limitedRegion, int chunkX, int chunkZ, Random random) {
        this.limitedRegion = limitedRegion;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.random = random;
    }

    public LimitedRegion getLimitedRegion() {
        return this.limitedRegion;
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public Random getRandom() {
        return this.random;
    }
}
