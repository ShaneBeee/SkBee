package com.shanebeestudios.skbee.api.generator;

import ch.njol.skript.lang.Trigger;
import com.shanebeestudios.skbee.api.generator.event.BiomeGenEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BiomeGenerator extends BiomeProvider {

    // SKRIPT STUFF
    private Trigger trigger;

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    // CLASS STUFF
    @Override
    public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
        Location location = new Location((World) worldInfo, x, y, z);
        BiomeGenEvent biomeGenEvent = new BiomeGenEvent(location);
        this.trigger.execute(biomeGenEvent);
        return biomeGenEvent.getBiome();
    }

    @Override
    public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
        List<Biome> biomes = new ArrayList<>();
        for (Biome biome : Biome.values()) {
            if (biome != Biome.CUSTOM) biomes.add(biome);
        }
        return biomes;
    }

}
