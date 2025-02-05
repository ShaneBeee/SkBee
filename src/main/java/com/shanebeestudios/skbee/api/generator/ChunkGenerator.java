package com.shanebeestudios.skbee.api.generator;

import ch.njol.skript.lang.Trigger;
import com.shanebeestudios.skbee.api.generator.event.BlockPopulateEvent;
import com.shanebeestudios.skbee.api.generator.event.ChunkGenEvent;
import com.shanebeestudios.skbee.api.generator.event.HeightGenEvent;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChunkGenerator extends org.bukkit.generator.ChunkGenerator {

    // SKRIPT STUFF
    private Trigger noiseGenTrigger;
    private Trigger surfaceGenTrigger;
    private Trigger chunkGenTrigger;
    private Trigger heightGenTrigger;
    private Trigger blockPopTrigger;

    public void setNoiseGenTrigger(Trigger noiseGenTrigger) {
        this.noiseGenTrigger = noiseGenTrigger;
    }

    public void setSurfaceGenTrigger(Trigger surfaceGenTrigger) {
        this.surfaceGenTrigger = surfaceGenTrigger;
    }

    public void setChunkGenTrigger(Trigger chunkGenTrigger) {
        this.chunkGenTrigger = chunkGenTrigger;
    }

    public void setHeightGenTrigger(Trigger heightGenTrigger) {
        this.heightGenTrigger = heightGenTrigger;
    }

    public void setBlockPopTrigger(Trigger blockPopTrigger) {
        this.blockPopTrigger = blockPopTrigger;
    }

    // OTHER STUFF
    private boolean vanillaDecor = false;
    private boolean vanillaCaves = false;
    private boolean vanillaStructures = false;
    private boolean vanillaMobs = false;
    private Location fixedSpawnLocation = null;

    public void setVanillaDecor(boolean vanillaDecor) {
        this.vanillaDecor = vanillaDecor;
    }

    public void setVanillaCaves(boolean vanillaCaves) {
        this.vanillaCaves = vanillaCaves;
    }

    public void setVanillaStructures(boolean vanillaStructures) {
        this.vanillaStructures = vanillaStructures;
    }

    public void setVanillaMobs(boolean vanillaMobs) {
        this.vanillaMobs = vanillaMobs;
    }

    public void setFixedSpawnLocation(Location fixedSpawnLocation) {
        this.fixedSpawnLocation = fixedSpawnLocation;
    }

    // GENERATOR

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        if (this.noiseGenTrigger != null) {
            ChunkGenEvent chunkGenEvent = new ChunkGenEvent(chunkData, chunkX, chunkZ);
            this.noiseGenTrigger.execute(chunkGenEvent);
        }
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        if (this.surfaceGenTrigger != null) {
            ChunkGenEvent chunkGenEvent = new ChunkGenEvent(chunkData, chunkX, chunkZ, this);
            this.surfaceGenTrigger.execute(chunkGenEvent);
        } else if (this.chunkGenTrigger != null) {
            ChunkGenEvent chunkGenEvent = new ChunkGenEvent(chunkData, chunkX, chunkZ);
            this.chunkGenTrigger.execute(chunkGenEvent);
        }
    }

    @Override
    public int getBaseHeight(@NotNull WorldInfo worldInfo, @NotNull Random random, int x, int z, @NotNull HeightMap heightMap) {
        if (this.heightGenTrigger != null) {
            HeightGenEvent heightGenEvent = new HeightGenEvent(new Location(null, x, 0, z));
            this.heightGenTrigger.execute(heightGenEvent);
            return heightGenEvent.getHeight();
        }
        return super.getBaseHeight(worldInfo, random, x, z, heightMap);
    }

    @Override
    public boolean shouldGenerateNoise() {
        return this.chunkGenTrigger == null && this.noiseGenTrigger == null;
    }

    @Override
    public boolean shouldGenerateSurface() {
        return this.chunkGenTrigger == null && this.surfaceGenTrigger == null;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return this.vanillaCaves;
    }

    @Override
    public boolean shouldGenerateDecorations() {
        return this.vanillaDecor;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return this.vanillaStructures;
    }

    @Override
    public boolean shouldGenerateMobs() {
        return this.vanillaMobs;
    }

    @Override
    public @Nullable Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        if (this.fixedSpawnLocation != null) {
            Location clone = this.fixedSpawnLocation.clone();
            clone.setWorld(world);
            return clone;
        }
        return null;
    }

    @Override
    public @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
        List<BlockPopulator> populators = new ArrayList<>();
        populators.add(new BlockPopulator() {
            @Override
            public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
                if (ChunkGenerator.this.blockPopTrigger != null) {
                    BlockPopulateEvent populateEvent = new BlockPopulateEvent(limitedRegion, chunkX, chunkZ, random);
                    ChunkGenerator.this.blockPopTrigger.execute(populateEvent);
                }
            }
        });
        return populators;
    }

}
