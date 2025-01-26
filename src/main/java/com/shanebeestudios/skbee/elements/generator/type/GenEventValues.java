package com.shanebeestudios.skbee.elements.generator.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import com.shanebeestudios.skbee.api.generator.event.BiomeGenEvent;
import com.shanebeestudios.skbee.api.generator.event.HeightGenEvent;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import org.bukkit.Location;
import org.bukkit.generator.BiomeParameterPoint;

@SuppressWarnings("unused")
public class GenEventValues {

    static {
        EventValues.registerEventValue(BiomeGenEvent.class, Location.class, BiomeGenEvent::getLocation, EventValues.TIME_NOW);
        EventValues.registerEventValue(BiomeGenEvent.class, BiomeParameterPoint.class, BiomeGenEvent::getBiomeParameterPoint, EventValues.TIME_NOW);
        EventValues.registerEventValue(HeightGenEvent.class, Location.class, HeightGenEvent::getLocation, EventValues.TIME_NOW);

        Classes.registerClass(new ClassInfo<>(BiomeParameterPoint.class, "biomeparameterpoint")
            .user("biome ?parameter ?points?")
            .name("ChunkGenerator - Biome Parameter Point")
            .description("The parameter points of a biome during chunk generation.",
                "See [**World Generation/Biomes**](https://minecraft.wiki/w/World_generation#Biomes) on McWiki for more details.")
            .since("INSERT VERSION")
            .parser(SkriptUtils.getDefaultParser()));
    }

}
