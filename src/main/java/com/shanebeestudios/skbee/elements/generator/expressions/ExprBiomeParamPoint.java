package com.shanebeestudios.skbee.elements.generator.expressions;

import ch.njol.skript.expressions.base.EventValueExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.generator.BiomeParameterPoint;

public class ExprBiomeParamPoint extends EventValueExpression<BiomeParameterPoint> {

    public static void register(Registration reg) {
        reg.newEventExpression(ExprBiomeParamPoint.class, BiomeParameterPoint.class,
                "biome parameter point")
            .name("ChunkGenerator - Biome Parameter Point")
            .description("Represents the biome noise parameters which may be passed to a world generator.",
                "This is used in the `biome gen` section of a chunk generator.")
            .examples("register chunk generator with id \"test\":",
                "\tbiome gen:",
                "\t\tif biome continentalness of biome parameter point <= -0.19:",
                "\t\t\tset chunkdata biome to ocean",
                "\t\telse:",
                "\t\t\tset {_temp} to biome temp of biome parameter point",
                "\t\t\tif {_temp} > 0.55:",
                "\t\t\t\tset chunkdata biome to badlands",
                "\t\t\telse if {_temp} > 0.2:",
                "\t\t\t\tset chunkdata biome to desert",
                "\t\t\telse if {_temp} > -0.15:",
                "\t\t\t\tset chunkdata biome to jungle",
                "\t\t\telse if {_temp} < -0.45:",
                "\t\t\t\tset chunkdata biome to plains",
                "\t\t\telse:",
                "\t\t\t\tset chunkdata biome to snowy taiga")
            .since("3.9.0")
            .register();
    }

    public ExprBiomeParamPoint() {
        super(BiomeParameterPoint.class);
    }

}
