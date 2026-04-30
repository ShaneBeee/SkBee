package com.shanebeestudios.skbee.elements.worldgen.types;

import ch.njol.skript.registrations.Classes;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.worldgen.event.BiomeGenEvent;
import com.shanebeestudios.skbee.api.worldgen.event.HeightGenEvent;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.worldgen.BeeWorldCreator;
import org.bukkit.Location;
import org.bukkit.WorldType;
import org.bukkit.generator.BiomeParameterPoint;
import org.bukkit.generator.ChunkGenerator;
import org.skriptlang.skript.common.function.DefaultFunction;

public class Types {

    public static void register(Registration reg) {
        eventValues(reg);
        types(reg);
    }

    private static void types(Registration reg) {
        reg.newType(ChunkGenerator.class, "chunkgenerator")
            .name("Chunk Generator")
            .description("A custom chunk generator for a world.")
            .since("INSERT VERSION")
            .parser(SkriptUtils.getDefaultParser())
            .register();

        reg.newType(BeeWorldCreator.class, "worldcreator")
            .user("world ?creators?")
            .name("World Creator")
            .description("Used to create new worlds.")
            .examples("set {_creator} to new world creator named \"my-world\"")
            .since("1.8.0")
            .parser(SkriptUtils.getDefaultParser())
            .register();

        if (Classes.getExactClassInfo(WorldType.class) == null) {
            reg.newEnumType(WorldType.class, "worldtype")
                .user("world ?types?")
                .name("World Type")
                .description("The type of a world")
                .examples("set world type of {_creator} to flat")
                .since("1.8.0")
                .register();
        } else {
            Util.log("It looks like another addon registered 'world type' already. ");
            Util.log("You may have to use their world type options in SkBee's 'world creator' system.");
        }
    }

    private static void eventValues(Registration reg) {
        reg.newEventValue(BiomeGenEvent.class, Location.class)
            .converter(BiomeGenEvent::getLocation)
            .register();
        reg.newEventValue(BiomeGenEvent.class, BiomeParameterPoint.class)
            .converter(BiomeGenEvent::getBiomeParameterPoint)
            .register();
        reg.newEventValue(HeightGenEvent.class, Location.class)
            .converter(HeightGenEvent::getLocation)
            .register();

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
                Number number = params.get("number");
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
