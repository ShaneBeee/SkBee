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
    "`fixed` = Will return the grouped ranged value of a param point, example for continentalness:",
    "- `-1.2~-1.05` = 0 (Mushroom fields)",
    "- `-1.05~-0.455` = 1 (Deep ocean)",
    "- `-0.455~-0.19` = 2 (Ocean)",
    "- `-0.19~-0.11` = 3 (Coast)",
    "- `-0.11~0.03` = 4 (Near-inland)",
    "- `0.03~0.3` = 5 (Mid-inland)",
    "- `0.3~1.0` = 6 (Far-inland)",
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
@Since("3.9.0")
public class ExprBiomeParamValues extends SimplePropertyExpression<BiomeParameterPoint, Number> {

    private enum ParamPoints {
        CONTINENTALNESS("continentalness", "(continentalness|continents)") {
            @Override
            public double getPoint(int minMax, BiomeParameterPoint point) {
                return switch (minMax) {
                    case 1 -> point.getMinContinentalness();
                    case 2 -> point.getMaxContinentalness();
                    default -> point.getContinentalness();
                };
            }

            @Override
            public int getFixedPoint(BiomeParameterPoint point) {
                double continentalness = point.getContinentalness();
                if (continentalness <= -1.05) return 0; // Mushroom Fields
                else if (continentalness <= -0.455) return 1; // Deep ocean
                else if (continentalness <= -0.19) return 2; // Ocean
                else if (continentalness <= -0.11) return 3; // Coast
                else if (continentalness <= 0.03) return 4; // Near-Inland
                else if (continentalness <= 0.3) return 5; // Mid-Inland
                else if (continentalness <= 1.0) return 6; // Far-Inland
                else return 7; // This shouldn't happen, but safety measure
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

            @Override
            public int getFixedPoint(BiomeParameterPoint point) {
                // Depth doesn't have a fixed point
                // So let's just do some math
                return (int) point.getDepth() * 128;
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

            @Override
            public int getFixedPoint(BiomeParameterPoint point) {
                double erosion = point.getErosion();
                if (erosion <= -0.78) return 0;
                else if (erosion <= -0.375) return 1;
                else if (erosion <= -0.2225) return 2;
                else if (erosion <= 0.05) return 3;
                else if (erosion <= 0.45) return 4;
                else if (erosion <= 0.55) return 5;
                else return 6; // This shouldn't happen, but safety measure
            }
        },
        HUMIDITY("humidity", "(humidity|vegetation)") {
            @Override
            public double getPoint(int minMax, BiomeParameterPoint point) {
                return switch (minMax) {
                    case 1 -> point.getMinHumidity();
                    case 2 -> point.getMaxHumidity();
                    default -> point.getHumidity();
                };
            }

            @Override
            public int getFixedPoint(BiomeParameterPoint point) {
                double humidity = point.getHumidity();
                if (humidity <= -0.35) return 0;
                else if (humidity <= -0.1) return 1;
                else if (humidity <= 0.1) return 2;
                else if (humidity <= 0.3) return 3;
                else if (humidity <= 1.0) return 4;
                else return 5; // This shouldn't happen, but safety measure
            }
        },
        PEAKS_AND_VALLEYS("peaks and valleys", "(peaks and valleys|pv)") {
            @Override
            public double getPoint(int minMax, BiomeParameterPoint point) {
                return -(Math.abs(Math.abs(point.getWeirdness()) - 0.6666667F) - 0.33333334F) * 3.0F;
            }

            @Override
            public int getFixedPoint(BiomeParameterPoint point) {
                double peaksAndValleys = getPoint(0, point);
                if (peaksAndValleys <= -0.85) return 0; // Valleys
                else if (peaksAndValleys <= -0.6) return 1; // Low
                else if (peaksAndValleys <= 0.2) return 2; // Mid
                else if (peaksAndValleys <= 0.7) return 3; // High
                else if (peaksAndValleys <= 1.0) return 4; // Peaks
                else return 5; // This shouldn't happen, but safety measure
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

            @Override
            public int getFixedPoint(BiomeParameterPoint point) {
                double temperature = point.getTemperature();
                if (temperature <= -0.45) return 0;
                else if (temperature <= -0.15) return 1;
                else if (temperature <= 0.2) return 2;
                else if (temperature <= 0.55) return 3;
                else if (temperature <= 1.0) return 4;
                else return 5; // This shouldn't happen, but safety measure
            }
        },
        WEIRDNESS("weirdness", "(weirdness|ridges)") {
            @Override
            public double getPoint(int minMax, BiomeParameterPoint point) {
                return switch (minMax) {
                    case 1 -> point.getMinWeirdness();
                    case 2 -> point.getMaxWeirdness();
                    default -> point.getWeirdness();
                };
            }

            @Override
            public int getFixedPoint(BiomeParameterPoint point) {
                // I couldn't find great data on this one
                // This is all I could find
                return point.getWeirdness() <= 0 ? 0 : 1;
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

        public abstract int getFixedPoint(BiomeParameterPoint point);
    }

    static {
        StringJoiner joiner = new StringJoiner("|");
        for (int i = 0; i < ParamPoints.values().length; i++) {
            joiner.add(i + ":" + ParamPoints.values()[i].getPattern());
        }
        register(ExprBiomeParamValues.class, Number.class,
            "[:min|:max|:fixed] biome [parameter] (" + joiner + ")",
            "biomeparameterpoint");
    }

    private int minMax;
    private boolean fixed;
    private int paramPoint;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.minMax = parseResult.hasTag("min") ? 1 : parseResult.hasTag("max") ? 2 : 0;
        this.fixed = parseResult.hasTag("fixed");
        this.paramPoint = parseResult.mark;
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Number convert(BiomeParameterPoint from) {
        ParamPoints paramPoint = ParamPoints.values()[this.paramPoint];
        if (this.fixed) return paramPoint.getFixedPoint(from);
        return paramPoint.getPoint(this.minMax, from);
    }

    @Override
    protected String getPropertyName() {
        String prefix = this.fixed ? "fixed " : this.minMax == 1 ? "min " : this.minMax == 2 ? "max " : "";
        return prefix + ParamPoints.values()[this.paramPoint].getName();
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return this.fixed ? Integer.class : Double.class;
    }

}
