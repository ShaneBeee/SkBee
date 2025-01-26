package com.shanebeestudios.skbee.api.generator.event;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeParameterPoint;
import org.jetbrains.annotations.NotNull;

public class BiomeGenEvent extends BaseGenEvent {

    private final Location location;
    private final BiomeParameterPoint biomeParameterPoint;
    private Biome biome;

    public BiomeGenEvent(Location location, BiomeParameterPoint biomeParameterPoint) {
        this.location = location;
        this.biomeParameterPoint = biomeParameterPoint;
    }

    @NotNull
    public Location getLocation() {
        return this.location;
    }

    public BiomeParameterPoint getBiomeParameterPoint() {
        return this.biomeParameterPoint;
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }

    public Biome getBiome() {
        return this.biome;
    }

}
