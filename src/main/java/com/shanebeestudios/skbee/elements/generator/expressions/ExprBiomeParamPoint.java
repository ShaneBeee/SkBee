package com.shanebeestudios.skbee.elements.generator.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.EventValueExpression;
import org.bukkit.generator.BiomeParameterPoint;

@Name("ChunkGenerator - Biome Parameter Point")
@Description({"Represents the biome noise parameters which may be passed to a world generator.",
    "This is used in the `biome gen` section of a chunk generator."})
@Examples({"register chunk generator with id \"test\":",
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
    "\t\t\t\tset chunkdata biome to snowy taiga"})
@Since("3.9.0")
public class ExprBiomeParamPoint extends EventValueExpression<BiomeParameterPoint> {

    static {
        register(ExprBiomeParamPoint.class, BiomeParameterPoint.class, "biome parameter point");
    }

    public ExprBiomeParamPoint() {
        super(BiomeParameterPoint.class);
    }

}
