package com.shanebeestudios.skbee.elements.generator.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.function.SimpleJavaFunction;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.DefaultClasses;
import ch.njol.skript.registrations.EventValues;
import com.shanebeestudios.skbee.api.generator.event.BiomeGenEvent;
import com.shanebeestudios.skbee.api.generator.event.HeightGenEvent;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import org.bukkit.Location;
import org.bukkit.generator.BiomeParameterPoint;
import org.jetbrains.annotations.Nullable;

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
            .since("3.9.0")
            .parser(SkriptUtils.getDefaultParser()));

        Functions.registerFunction(new SimpleJavaFunction<>("peaksAndValleys", new Parameter[]{
            new Parameter<>("number", DefaultClasses.NUMBER, true, null)
        }, DefaultClasses.NUMBER, true) {
            @Override
            public Number @Nullable [] executeSimple(Object[][] params) {
                Number number = (Number) params[0][0];
                float f = number.floatValue();
                return new Number[]{-(Math.abs(Math.abs(f) - 0.6666667F) - 0.33333334F) * 3.0F};
            }
        }).description("Peaks and Valleys in chunk generation.")
            .examples("set {_weird} to biome weirdness of biome parameter point",
                "set {_pv} to peaksAndValleys({_weird})",
                "if {_pv} < -0.85:",
                "\tset {_biome} to river")
            .since("3.9.0");
    }

}
