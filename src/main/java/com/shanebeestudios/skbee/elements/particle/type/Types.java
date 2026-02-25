package com.shanebeestudios.skbee.elements.particle.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.SkriptColor;
import ch.njol.skript.util.Timespan;
import com.shanebeestudios.skbee.api.particle.ParticleUtil;
import com.shanebeestudios.skbee.api.particle.ParticleWrapper;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Particle.DustTransition;
import org.bukkit.Particle.Trail;
import org.bukkit.Vibration;
import org.bukkit.Vibration.Destination;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.common.function.DefaultFunction;

public class Types {

    public static void register(Registration reg) {
        registerTypes(reg);
        registerFunctions(reg);
    }

    private static void registerTypes(Registration reg) {
        reg.newType(ParticleWrapper.class, "minecraftparticle")
            .user("minecraft ?particles?")
            .name("Particle")
            .description("Represents a particle which can be used in the 'Particle Spawn' effect.",
                "Some particles require extra data, these are distinguished by their data type within the square brackets.",
                "DustOption, DustTransition and Vibration each have their own functions to build the appropriate data for these particles.",
                Util.AUTO_GEN_NOTE)
            .parser(ParticleWrapper.getParser())
            .usage(ParticleWrapper.getNamesAsString())
            .after("itemtype")
            .since("1.9.0")
            .register();

        if (Classes.getExactClassInfo(DustOptions.class) == null) {
            Classes.registerClass(new ClassInfo<>(DustOptions.class, "dustoption")
                .name(ClassInfo.NO_DOC).user("dust ?options?")
                .parser(new Parser<>() {
                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(DustOptions dustOption, int flags) {
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
                    public @NotNull String toVariableNameString(DustOptions o) {
                        return toString(o, 0);
                    }
                }));
        } else {
            Util.logLoading("It looks like another addon registered 'dustoption' already.");
            Util.logLoading("You may have to use their DustOption in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(DustTransition.class) == null) {
            Classes.registerClass(new ClassInfo<>(DustTransition.class, "dusttransition")
                .name(ClassInfo.NO_DOC).user("dust ?transitions?")
                .parser(SkriptUtils.getDefaultParser()));
        } else {
            Util.logLoading("It looks like another addon registered 'dusttransition' already.");
            Util.logLoading("You may have to use their DustTransition in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(Vibration.class) == null) {
            Classes.registerClass(new ClassInfo<>(Vibration.class, "vibration")
                .name(ClassInfo.NO_DOC).user("vibrations?")
                .parser(SkriptUtils.getDefaultParser()));
        } else {
            Util.logLoading("It looks like another addon registered 'vibration' already.");
            Util.logLoading("You may have to use their Vibration in SkBee's syntaxes.");
        }

        if (Classes.getExactClassInfo(Trail.class) == null) {
            Classes.registerClass(new ClassInfo<>(Trail.class, "trail")
                .name(ClassInfo.NO_DOC)
                .user("trails?")
                .parser(SkriptUtils.getDefaultParser()));
        } else {
            Util.logLoading("It looks like another addon registered 'trail' already.");
            Util.logLoading("You may have to use their Trail in SkBee's syntaxes.");
        }

        if (ParticleUtil.HAS_SPELL) {
            if (Classes.getExactClassInfo(Particle.Spell.class) == null) {
                Classes.registerClass(new ClassInfo<>(Particle.Spell.class, "particlespell")
                    .name(ClassInfo.NO_DOC)
                    .user("particle ?spells?")
                    .parser(SkriptUtils.getDefaultParser()));
            } else {
                Util.logLoading("It looks like another addon registered 'particlespell' already.");
                Util.logLoading("You may have to use their Particle Spell in SkBee's syntaxes.");
            }
        }
    }

    private static void registerFunctions(Registration reg) {
        // == FUNCTIONS ==

        // Function to create DustOptions
        DefaultFunction<DustOptions> dustFunc = DefaultFunction.builder(reg.getAddon(),
                "dustOption", DustOptions.class)
            .parameter("color", Color.class)
            .parameter("size", Number.class)
            .build(args -> {
                Object color = args.get("color");
                Number size = args.get("size");
                if (color instanceof Color) {
                    return new DustOptions(((Color) color).asBukkitColor(), size.floatValue());
                }
                return null;
            });

        reg.newFunction(dustFunc)
            .description("Creates a new dust option to be used with 'dust' particle. Color can either be a regular color or an RGB color using",
                "Skript's rgb() function. Size is the size the particle will be.")
            .examples("set {_c} to dustOption(red, 1.5)", "set {_c} to dustOption(rgb(1, 255, 1), 3)")
            .since("1.9.0")
            .register();


        // Function to create DustTransition
        DefaultFunction<DustTransition> transitionFunc = DefaultFunction.builder(reg.getAddon(), "dustTransition", DustTransition.class)
            .parameter("fromColor", Color.class)
            .parameter("toColor", Color.class)
            .parameter("size", Number.class)
            .build(params -> {
                Object fromColor = params.get("fromColor");
                Object toColor = params.get("toColor");
                Number size = params.get("size");
                if (fromColor instanceof Color && toColor instanceof Color) {
                    return new DustTransition(((Color) fromColor).asBukkitColor(), ((Color) toColor).asBukkitColor(), size.floatValue());
                }
                return null;
            });

        reg.newFunction(transitionFunc)
            .description("Creates a new dust transition to be used with 'dust_color_transition' particle.",
                "Color can either be a regular color or an RGB color using Skript's rgb() function.",
                "Size is the size the particle will be.")
            .examples("set {_d} to dustTransition(red, green, 10)",
                "set {_d} to dustTransition(blue, rgb(1,1,1), 5)")
            .since("1.11.1")
            .register();

        // Function to create Vibration
        DefaultFunction<Vibration> vibeFunc = DefaultFunction.builder(reg.getAddon(), "vibration", Vibration.class)
            .parameter("to", Location.class)
            .parameter("arrivalTime", Timespan.class)
            .build(params -> {
                Location destination = params.get("to");
                int arrivalTime = (int) ((Timespan) params.get("arrivalTime")).getAs(Timespan.TimePeriod.TICK);
                return new Vibration(new Destination.BlockDestination(destination), arrivalTime);
            });

        reg.newFunction(vibeFunc)
            .description("Creates a new vibration to be used with 'vibration' particle.",
                "`to` = The destination location the particle will travel to.",
                "`arrivalTime` = The time it will take to arrive at the destination location.")
            .examples("set {_v} to vibration({loc}, 10 seconds)")
            .since("1.11.1")
            .register();

        DefaultFunction<Trail> trailFunc = DefaultFunction.builder(reg.getAddon(), "trail", Trail.class)
            .parameter("target", Location.class)
            .parameter("color", Color.class)
            .parameter("duration", Timespan.class)
            .build(params -> {
                Location target = params.get("target");
                org.bukkit.Color color = ((Color) params.get("color")).asBukkitColor();
                Timespan timespan = params.get("duration");
                return new Trail(target, color, (int) timespan.getAs(Timespan.TimePeriod.TICK));
            });

        reg.newFunction(trailFunc)
            .description("Creates a new trail to be used with 'trail' particle.",
                "Takes in a location for the target (where the trail heads to), the color and duration.",
                "Requires Minecraft 1.21.4+")
            .examples("set {_trail} to trail(location of target block, blue, 1 second)",
                "make 10 of trail using {_trail} at location of player")
            .since("3.6.5")
            .register();

        if (ParticleUtil.HAS_SPELL) {
            DefaultFunction<Particle.Spell> spellFunc = DefaultFunction.builder(reg.getAddon(), "particleSpell", Particle.Spell.class)
                .parameter("color", Color.class)
                .parameter("power", Number.class)
                .build(params -> {
                    Object color = params.get("color");
                    Number power = params.get("power");
                    if (color instanceof Color) {
                        return new Particle.Spell(((Color) color).asBukkitColor(), power.floatValue());
                    }
                    return null;
                });

            reg.newFunction(spellFunc)
                .description("Creates a new spell data to be used with the 'effect'/'instant_effect' particles.",
                    "Takes in a color and a number(float - which represents the power of the effect.)",
                    "Requires Minecraft 1.21.9+")
                .examples("set {_spell} to particleSpell(blue, 0.5)",
                    "make 10 of effect using {_spell} at location of player's head")
                .since("3.13.1")
                .register();
        }
    }

}
