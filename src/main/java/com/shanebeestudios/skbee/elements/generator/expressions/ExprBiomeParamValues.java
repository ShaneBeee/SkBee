package com.shanebeestudios.skbee.elements.generator.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import org.bukkit.generator.BiomeParameterPoint;
import org.jetbrains.annotations.Nullable;

import java.util.StringJoiner;

@Name("ChunkGenerator - Biome Parameter Point Values")
@Description({"Represents the different values of a Biome Parameter Point.",
    "This is used in the `biome gen` section of a chunk generator.",
    "See [**World Generation/Biomes**](https://minecraft.wiki/w/World_generation#Biomes) on McWiki for more details."})
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
@Since("INSERT VERSION")
public class ExprBiomeParamValues extends SimplePropertyExpression<BiomeParameterPoint, Double> {

    private enum ParamPoints {
        CONTINENTALNESS("continentalness") {
            @Override
            public double getPoint(int minMax, BiomeParameterPoint point) {
                return switch (minMax) {
                    case 1 -> point.getMinContinentalness();
                    case 2 -> point.getMaxContinentalness();
                    default -> point.getContinentalness();
                };
            }
        },
        DEPTH("depth") {
            @Override
            public double getPoint(int minMax, BiomeParameterPoint point) {
                return switch (minMax) {
                    case 1 -> point.getMinDepth();
                    case 2 -> point.getMaxDepth();
                    default -> point.getDepth();
                };
            }
        },
        EROSION("erosion") {
            @Override
            public double getPoint(int minMax, BiomeParameterPoint point) {
                return switch (minMax) {
                    case 1 -> point.getMinErosion();
                    case 2 -> point.getMaxErosion();
                    default -> point.getErosion();
                };
            }
        },
        HUMIDITY("humidity") {
            @Override
            public double getPoint(int minMax, BiomeParameterPoint point) {
                return switch (minMax) {
                    case 1 -> point.getMinHumidity();
                    case 2 -> point.getMaxHumidity();
                    default -> point.getHumidity();
                };
            }
        },
        TEMPERATURE("temperature", "temp[erature]") {
            @Override
            public double getPoint(int minMax, BiomeParameterPoint point) {
                return switch (minMax) {
                    case 1 -> point.getMinTemperature();
                    case 2 -> point.getMaxTemperature();
                    default -> point.getTemperature();
                };
            }
        },
        WEIRDNESS("weirdness") {
            @Override
            public double getPoint(int minMax, BiomeParameterPoint point) {
                return switch (minMax) {
                    case 1 -> point.getMinWeirdness();
                    case 2 -> point.getMaxWeirdness();
                    default -> point.getWeirdness();
                };
            }
        };

        private final String name;
        private final String pattern;

        ParamPoints(String name) {
            this.name = name;
            this.pattern = name;
        }

        ParamPoints(String name, String pattern) {
            this.name = name;
            this.pattern = pattern;
        }

        public String getName() {
            return name;
        }

        public String getPattern() {
            return pattern;
        }

        public abstract double getPoint(int minMax, BiomeParameterPoint point);
    }

    static {
        StringJoiner joiner = new StringJoiner("|");
        for (int i = 0; i < ParamPoints.values().length; i++) {
            joiner.add(i + ":" + ParamPoints.values()[i].getPattern());
        }
        register(ExprBiomeParamValues.class, Double.class,
            "[:min|:max] biome [parameter] (" + joiner + ")",
            "biomeparameterpoint");
    }

    private int minMax;
    private int paramPoint;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.minMax = parseResult.hasTag("min") ? 1 : parseResult.hasTag("max") ? 2 : 0;
        this.paramPoint = parseResult.mark;
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Double convert(BiomeParameterPoint from) {
        return ParamPoints.values()[this.paramPoint].getPoint(this.minMax, from);
    }

    @Override
    protected String getPropertyName() {
        String minMax = this.minMax == 1 ? "min " : this.minMax == 2 ? "max " : "";
        return minMax + ParamPoints.values()[this.paramPoint].getName();
    }

    @Override
    public Class<? extends Double> getReturnType() {
        return Double.class;
    }

}
