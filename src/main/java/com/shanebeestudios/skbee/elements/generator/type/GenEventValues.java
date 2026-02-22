package com.shanebeestudios.skbee.elements.generator.type;

import ch.njol.skript.registrations.EventValues;
import com.shanebeestudios.skbee.api.generator.event.BiomeGenEvent;
import com.shanebeestudios.skbee.api.generator.event.HeightGenEvent;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import org.bukkit.Location;
import org.bukkit.generator.BiomeParameterPoint;
import org.skriptlang.skript.common.function.DefaultFunction;

@SuppressWarnings("unused")
public class GenEventValues {

    public static void register(Registration reg) {
        EventValues.registerEventValue(BiomeGenEvent.class, Location.class, BiomeGenEvent::getLocation, EventValues.TIME_NOW);
        EventValues.registerEventValue(BiomeGenEvent.class, BiomeParameterPoint.class, BiomeGenEvent::getBiomeParameterPoint, EventValues.TIME_NOW);
        EventValues.registerEventValue(HeightGenEvent.class, Location.class, HeightGenEvent::getLocation, EventValues.TIME_NOW);

        reg.newType(BiomeParameterPoint.class, "biomeparameterpoint")
            .user("biome ?parameter ?points?")
            .name("ChunkGenerator - Biome Parameter Point")
            .description("The parameter points of a biome during chunk generation.",
                "See [**World Generation/Biomes**](https://minecraft.wiki/w/World_generation#Biomes) on McWiki for more details.")
            .since("3.9.0")
            .parser(SkriptUtils.getDefaultParser())
            .register();

        DefaultFunction<Number> peaksAndValleys = DefaultFunction.builder(reg.getAddon(), "peaksAndValleys", Number.class)
            .parameter("number", Number.class)
            .build(params -> {
                Number number =  params.get("number");
                float f = number.floatValue();
                return -(Math.abs(Math.abs(f) - 0.6666667F) - 0.33333334F) * 3.0F;
            });

        reg.newFunction(peaksAndValleys)
            .name("ChunkGenerator - Peaks and Valleys")
            .description("Peaks and Valleys in chunk generation.")
            .examples("set {_weird} to biome weirdness of biome parameter point",
                "set {_pv} to peaksAndValleys({_weird})",
                "if {_pv} < -0.85:",
                "\tset {_biome} to river")
            .since("3.9.0")
            .register();
    }

}
