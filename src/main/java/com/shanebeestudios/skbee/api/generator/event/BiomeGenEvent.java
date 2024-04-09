package com.shanebeestudios.skbee.api.generator.event;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

public class BiomeGenEvent extends BaseGenEvent {

    private final Location location;
    private Biome biome;

    public BiomeGenEvent(Location location) {
        this.location = location;
    }

    @NotNull
    public Location getLocation() {
        return this.location;
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }

    public Biome getBiome() {
        return this.biome;
    }

}
