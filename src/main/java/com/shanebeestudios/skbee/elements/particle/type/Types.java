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
                    @Nullable
                    @Override
                    public Particle parse(String s, ParseContext context) {
                        return ParticleUtil.parse(s.replace(" ", "_"));
                    }

                    @Override
                    public @NotNull String toString(Particle particle, int flags) {
                        return ParticleUtil.getName(particle);
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

        if (Classes.getExactClassInfo(Particle.DustOptions.class) == null) {
            Classes.registerClass(new ClassInfo<>(Particle.DustOptions.class, "dustoption")
                .name(ClassInfo.NO_DOC).user("dust ?options?")
                .parser(new Parser<>() {
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
        }

        if (Classes.getExactClassInfo(DustTransition.class) == null) {
            Classes.registerClass(new ClassInfo<>(DustTransition.class, "dusttransition")
                .name(ClassInfo.NO_DOC).user("dust ?transitions?")
                .parser(SkriptUtils.getDefaultParser()));
        }

        if (Classes.getExactClassInfo(Vibration.class) == null) {
            Classes.registerClass(new ClassInfo<>(Vibration.class, "vibration")
                .name(ClassInfo.NO_DOC).user("vibrations?")
                .parser(SkriptUtils.getDefaultParser()));
        }

        if (Classes.getExactClassInfo(Particle.Trail.class) == null) {
            Classes.registerClass(new ClassInfo<>(Particle.Trail.class, "trail")
                .name(ClassInfo.NO_DOC)
                .user("trails?")
                .parser(SkriptUtils.getDefaultParser()));
        }

        if (ParticleUtil.HAS_SPELL && Classes.getExactClassInfo(Particle.Spell.class) == null) {
            Classes.registerClass(new ClassInfo<>(Particle.Spell.class, "particlespell")
                .name(ClassInfo.NO_DOC)
                .user("particle ?spells?")
                .parser(SkriptUtils.getDefaultParser()));
        }

        // == FUNCTIONS ==

        // Function to create DustOptions
        //noinspection ConstantConditions
        Functions.registerFunction(new SimpleJavaFunction<>("dustOption", new Parameter[]{
            new Parameter<>("color", DefaultClasses.COLOR, true, null),
            new Parameter<>("size", DefaultClasses.NUMBER, true, null)
        }, Classes.getExactClassInfo(Particle.DustOptions.class), true) {
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
            @Override
            public Vibration[] executeSimple(Object[][] params) {
                if (params[0].length == 0 || params[1].length == 0) {
                    return null;
                }
                // Apparently original location makes no difference
                Location origin = new Location(null, 0, 0, 0);
                Location destination = (Location) params[0][0];
                int arrivalTime = (int) ((Timespan) params[1][0]).getAs(Timespan.TimePeriod.TICK);
                @SuppressWarnings("removal")
                Vibration vibration = new Vibration(origin, new Vibration.Destination.BlockDestination(destination), arrivalTime);
                return new Vibration[]{vibration};
            }
        }.description("Creates a new vibration to be used with 'vibration' particle.",
                "TO = the destination location the particle will travel to.",
                "ARRIVAL TIME = the time it will take to arrive at the destination location.",
                "Requires MC 1.17+")
            .examples("set {_v} to vibration({loc}, 10 seconds)")
            .since("1.11.1"));

        Functions.registerFunction(new SimpleJavaFunction<>("trail", new Parameter[]{
                new Parameter<>("target", DefaultClasses.LOCATION, true, null),
                new Parameter<>("color", DefaultClasses.COLOR, true, null),
                new Parameter<>("duration", DefaultClasses.TIMESPAN, true, null)
            }, Classes.getExactClassInfo(Particle.Trail.class), true) {
                @Override
                public Particle.Trail[] executeSimple(Object[][] params) {
                    Location target = (Location) params[0][0];
                    org.bukkit.Color color = ((Color) params[1][0]).asBukkitColor();
                    Timespan timespan = (Timespan) params[2][0];
                    return new Particle.Trail[]{new Particle.Trail(target, color, (int) timespan.getAs(Timespan.TimePeriod.TICK))};
                }
            }).description("Creates a new trail to be used with 'trail' particle.",
                "Takes in a location for the target (where the trail heads to), the color and duration.",
                "Requires Minecraft 1.21.4+")
            .examples("set {_trail} to trail(location of target block, blue, 1 second)",
                "make 10 of trail using {_trail} at location of player")
            .since("3.6.5");

        if (ParticleUtil.HAS_SPELL) {
            Functions.registerFunction(new SimpleJavaFunction<>("particleSpell", new Parameter[]{
                    new Parameter<>("color", DefaultClasses.COLOR, true, null),
                    new Parameter<>("power", DefaultClasses.NUMBER, true, null)
                }, Classes.getExactClassInfo(Particle.Spell.class), true) {
                    @Override
                    public Particle.Spell @Nullable [] executeSimple(Object[][] params) {
                        org.bukkit.Color color = ((Color) params[0][0]).asBukkitColor();
                        float power = ((Number) params[1][0]).floatValue();
                        return new Particle.Spell[]{new Particle.Spell(color, power)};
                    }
                })
                .description("Creates a new spell data to be used with the 'effect'/'instant_effect' particles.",
                    "Takes in a color and a number(float - which represents the power of the effect.)",
                    "Requires Minecraft 1.21.9+")
                .examples("set {_spell} to particleSpell(blue, 0.5)",
                    "make 10 of effect using {_spell} at location of player's head")
                .since("3.13.1");
        }
    }

}
