package com.shanebeestudios.skbee.api.generator.event;

import org.bukkit.generator.LimitedRegion;

public class BlockPopulateEvent extends BaseGenEvent {

    private final LimitedRegion limitedRegion;
    private final int chunkX, chunkZ;

    public BlockPopulateEvent(LimitedRegion limitedRegion, int chunkX, int chunkZ) {
        this.limitedRegion = limitedRegion;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
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

}
