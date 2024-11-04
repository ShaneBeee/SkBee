package com.shanebeestudios.skbee.elements.particle.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.function.SimpleJavaFunction;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.DefaultClasses;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.SkriptColor;
import ch.njol.skript.util.Timespan;
import com.shanebeestudios.skbee.api.particle.ParticleUtil;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustTransition;
import org.bukkit.Vibration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class Types {

    static {
        // Only register if no other addons have registered this class
        if (Classes.getExactClassInfo(Particle.class) == null) {
            Classes.registerClass(new ClassInfo<>(Particle.class, "particle")
                .user("particles?")
                .name("Particle")
                .description("Represents a particle which can be used in the 'Particle Spawn' effect.",
                    "Some particles require extra data, these are distinguished by their data type within the square brackets.",
                    "DustOption, DustTransition and Vibration each have their own functions to build the appropriate data for these particles.",
                    "NOTE: These are auto-generated and may differ between server versions.")
                .usage(ParticleUtil.getNamesAsString())
                .after("itemtype")
                .since("1.9.0")
                .parser(new Parser<>() {

                    @SuppressWarnings("NullableProblems")
                    @Nullable
                    @Override
                    public Particle parse(String s, ParseContext context) {
                        return ParticleUtil.parse(s.replace(" ", "_"));
                    }

                    @Override
                    public @NotNull String toString(Particle particle, int flags) {
                        return "" + ParticleUtil.getName(particle);
                    }

                    @Override
                    public @NotNull String toVariableNameString(Particle particle) {
                        return "particle:" + toString(particle, 0);
                    }
                }));
        } else {
            Util.logLoading("It looks like another addon registered 'particle' already.");
            Util.logLoading("You may have to use their particles in SkBee's 'particle spawn' effect.");
        }

        Classes.registerClass(new ClassInfo<>(Particle.DustOptions.class, "dustoption")
            .name(ClassInfo.NO_DOC).user("dust ?options?")
            .parser(new Parser<>() {
                @SuppressWarnings("NullableProblems")
                @Override
                public boolean canParse(ParseContext context) {
                    return false;
                }

                @Override
                public @NotNull String toString(Particle.DustOptions dustOption, int flags) {
                    org.bukkit.Color bukkitColor = dustOption.getColor();
                    int red = bukkitColor.getRed();
                    int green = bukkitColor.getGreen();
                    int blue = bukkitColor.getBlue();
                    SkriptColor skriptColor = SkriptColor.fromBukkitColor(bukkitColor);

                    String color;
                    //noinspection ConstantConditions
                    if (skriptColor != null) {
                        color = skriptColor.toString();
                    } else {
                        color = String.format("rgb(%s,%s,%s)", red, green, blue);
                    }
                    return "dustOption(color=" + color + ",size=" + dustOption.getSize() + ")";
                }

                @Override
                public @NotNull String toVariableNameString(Particle.DustOptions o) {
                    return toString(o, 0);
                }
            }));
        Classes.registerClass(new ClassInfo<>(DustTransition.class, "dusttransition")
            .name(ClassInfo.NO_DOC).user("dust ?transitions?")
            .parser(SkriptUtils.getDefaultParser()));
        Classes.registerClass(new ClassInfo<>(Vibration.class, "vibration")
            .name(ClassInfo.NO_DOC).user("vibrations?")
            .parser(SkriptUtils.getDefaultParser()));

        if (ParticleUtil.HAS_TARGET_COLOR && Classes.getExactClassInfo(Particle.TargetColor.class) == null) {
            Classes.registerClass(new ClassInfo<>(Particle.TargetColor.class, "targetcolor")
                .name(ClassInfo.NO_DOC)
                .user("target ?colors?")
                .parser(SkriptUtils.getDefaultParser()));
        }


        // == FUNCTIONS ==

        // Function to create DustOptions
        //noinspection ConstantConditions
        Functions.registerFunction(new SimpleJavaFunction<>("dustOption", new Parameter[]{
            new Parameter<>("color", DefaultClasses.COLOR, true, null),
            new Parameter<>("size", DefaultClasses.NUMBER, true, null)
        }, Classes.getExactClassInfo(Particle.DustOptions.class), true) {
            @SuppressWarnings("NullableProblems")
            @Override
            public Particle.DustOptions[] executeSimple(Object[][] params) {
                org.bukkit.Color color = ((Color) params[0][0]).asBukkitColor();
                float size = ((Number) params[1][0]).floatValue();
                return new Particle.DustOptions[]{new Particle.DustOptions(color, size)};
            }
        }.description("Creates a new dust option to be used with 'dust' particle. Color can either be a regular color or an RGB color using",
                "Skript's rgb() function. Size is the size the particle will be.")
            .examples("set {_c} to dustOption(red, 1.5)", "set {_c} to dustOption(rgb(1, 255, 1), 3)")
            .since("1.9.0"));


        // Function to create DustTransition
        //noinspection ConstantConditions
        Functions.registerFunction(new SimpleJavaFunction<>("dustTransition", new Parameter[]{
            new Parameter<>("fromColor", DefaultClasses.COLOR, true, null),
            new Parameter<>("toColor", DefaultClasses.COLOR, true, null),
            new Parameter<>("size", DefaultClasses.NUMBER, true, null)
        }, Classes.getExactClassInfo(DustTransition.class), true) {
            @SuppressWarnings("NullableProblems")
            @Override
            public DustTransition[] executeSimple(Object[][] params) {
                org.bukkit.Color fromColor = ((Color) params[0][0]).asBukkitColor();
                org.bukkit.Color toColor = ((Color) params[1][0]).asBukkitColor();
                float size = ((Number) params[2][0]).floatValue();
                return new DustTransition[]{new DustTransition(fromColor, toColor, size)};
            }
        }.description("Creates a new dust transition to be used with 'dust_color_transition' particle.",
                "Color can either be a regular color or an RGB color using Skript's rgb() function.",
                "Size is the size the particle will be. Requires MC 1.17+")
            .examples("set {_d} to dustTransition(red, green, 10)", "set {_d} to dustTransition(blue, rgb(1,1,1), 5)")
            .since("1.11.1"));

        // Function to create Vibration
        //noinspection ConstantConditions
        Functions.registerFunction(new SimpleJavaFunction<>("vibration", new Parameter[]{
            new Parameter<>("to", DefaultClasses.LOCATION, true, null),
            new Parameter<>("arrivalTime", DefaultClasses.TIMESPAN, true, null)
        }, Classes.getExactClassInfo(Vibration.class), true) {
            @SuppressWarnings({"NullableProblems", "removal"})
            @Override
            public Vibration[] executeSimple(Object[][] params) {
                if (params[0].length == 0 || params[1].length == 0) {
                    return null;
                }
                // Apparently original location makes no difference
                Location origin = new Location(null, 0, 0, 0);
                Location destination = (Location) params[0][0];
                int arrivalTime = (int) ((Timespan) params[1][0]).getTicks();
                Vibration vibration = new Vibration(origin, new Vibration.Destination.BlockDestination(destination), arrivalTime);
                return new Vibration[]{vibration};
            }
        }.description("Creates a new vibration to be used with 'vibration' particle.",
                "TO = the destination location the particle will travel to.",
                "ARRIVAL TIME = the time it will take to arrive at the destination location.",
                "Requires MC 1.17+")
            .examples("set {_v} to vibration({loc}, 10 seconds)")
            .since("1.11.1"));

        if (ParticleUtil.HAS_TARGET_COLOR) {
            //noinspection DataFlowIssue
            Functions.registerFunction(new SimpleJavaFunction<>("targetColor", new Parameter[]{
                    new Parameter<>("target", DefaultClasses.LOCATION, true, null),
                    new Parameter<>("color", DefaultClasses.COLOR, true, null)
                }, Classes.getExactClassInfo(Particle.TargetColor.class), true) {
                    @SuppressWarnings("NullableProblems")
                    @Override
                    public Particle.TargetColor[] executeSimple(Object[][] params) {
                        Location target = (Location) params[0][0];
                        org.bukkit.Color color = ((Color) params[1][0]).asBukkitColor();
                        return new Particle.TargetColor[]{new Particle.TargetColor(target, color)};
                    }
                }).description("Creates a new target color to be used with 'trail' particle.",
                    "Takes in a location for the target (where the trail heads to) and the color.",
                    "Requires Minecraft 1.21.2+")
                .examples("set {_target} to targetColor(location of target block, blue)",
                    "make 10 of trail using {_target} at location of player")
                .since("3.6.2");
        }
    }

}
