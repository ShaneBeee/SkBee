package com.shanebeestudios.skbee.elements.other.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.function.SimpleJavaFunction;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.DefaultClasses;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.Timespan;
import ch.njol.util.StringUtils;
import com.shanebeestudios.skbee.api.particle.ParticleUtil;
import com.shanebeestudios.skbee.api.util.EnumUtils;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Particle.DustTransition;
import org.bukkit.Vibration;
import org.bukkit.Vibration.Destination.BlockDestination;
import org.bukkit.entity.Spellcaster;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.event.entity.EntityTransformEvent.TransformReason;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerQuitEvent.QuitReason;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Types {

    private static String getItemFlagNames() {
        List<String> flags = new ArrayList<>();
        for (ItemFlag flag : ItemFlag.values()) {
            String hide = flag.name().replace("HIDE_", "").toLowerCase(Locale.ROOT);
            hide += "_flag";
            flags.add(hide);
        }
        Collections.sort(flags);
        return StringUtils.join(flags, ", ");
    }

    static {
        // == TYPES ==

        // Only register if no other addons have registered this class
        if (Classes.getExactClassInfo(State.class) == null) {
            EnumUtils<State> FISH_STATE_ENUM = new EnumUtils<>(State.class);
            Classes.registerClass(new ClassInfo<>(State.class, "fishingstate")
                    .user("fish(ing)? ?states?")
                    .name("Fish Event State")
                    .usage(FISH_STATE_ENUM.getAllNames())
                    .since("1.15.2")
                    .parser(FISH_STATE_ENUM.getParser()));
        } else {
            Util.logLoading("It looks like another addon registered 'fishingstate' already.");
            Util.logLoading("You may have to use their fishing states in SkBee's 'Fish Event State' expression.");
        }

        if (Classes.getExactClassInfo(ItemFlag.class) == null) {
            Classes.registerClass(new ClassInfo<>(ItemFlag.class, "itemflag")
                    .user("item ?flags?")
                    .name("Item Flag")
                    .description("Represents the different Item Flags that can be applied to an item.",
                            "NOTE: Underscores aren't required, you CAN use spaces.")
                    .user(getItemFlagNames())
                    .since("2.1.0")
                    .parser(new Parser<>() {

                        @Override
                        public boolean canParse(@NotNull ParseContext context) {
                            return true;
                        }

                        @SuppressWarnings("NullableProblems")
                        @Override
                        public @Nullable ItemFlag parse(String string, ParseContext context) {
                            String flag = string.replace(" ", "_").toUpperCase(Locale.ROOT);
                            flag = "HIDE_" + flag.replace("_FLAG", "");
                            try {
                                return ItemFlag.valueOf(flag);
                            } catch (IllegalArgumentException ignore) {
                                return null;
                            }
                        }

                        @Override
                        public @NotNull String toString(ItemFlag itemFlag, int flags) {
                            String flag = itemFlag.name().replace("HIDE_", "") + "_FLAG";
                            return flag.toLowerCase(Locale.ROOT);
                        }

                        @Override
                        public @NotNull String toVariableNameString(ItemFlag itemFlag) {
                            return toString(itemFlag, 0);
                        }
                    }));
        } else {
            Util.logLoading("It looks like another addon registered 'itemflag' already.");
            Util.logLoading("You may have to use their Item Flags in SkBee's 'Hidden Item Flags' expression.");
        }

        // Only register if no other addons have registered this class
        if (Classes.getExactClassInfo(Spellcaster.Spell.class) == null) {
            EnumUtils<Spellcaster.Spell> SPELL_ENUM = new EnumUtils<>(Spellcaster.Spell.class);
            Classes.registerClass(new ClassInfo<>(Spellcaster.Spell.class, "spell")
                    .user("spells?")
                    .name("Spellcaster Spell")
                    .description("Represents the different spells of a spellcaster.")
                    .usage(SPELL_ENUM.getAllNames())
                    .since("1.17.0")
                    .parser(SPELL_ENUM.getParser()));
        } else {
            Util.logLoading("It looks like another addon registered 'spell' already.");
            Util.logLoading("You may have to use their spells in SkBee's 'Spell-caster Spell' expression.");
        }

        // Only register if no other addons have registered this class
        // EntityPotionEffectEvent.Cause
        if (Classes.getExactClassInfo(Cause.class) == null) {
            EnumUtils<Cause> POTION_EFFECT_EVENT_CAUSE = new EnumUtils<>(Cause.class, "", "effect");
            Classes.registerClass(new ClassInfo<>(Cause.class, "potioneffectcause")
                    .user("potion ?effect ?causes?")
                    .name("Potion Effect Cause")
                    .description("Represents the different causes of an entity potion effect event.")
                    .usage(POTION_EFFECT_EVENT_CAUSE.getAllNames())
                    .since("1.17.0")
                    .parser(POTION_EFFECT_EVENT_CAUSE.getParser()));
        } else {
            Util.logLoading("It looks like another addon registered 'potioneffectcause' already.");
            Util.logLoading("You may have to use their potion effect causes in SkBee's 'Entity Potion Effect' event.");
        }

        if (Classes.getExactClassInfo(TransformReason.class) == null) {
            EnumUtils<TransformReason> TRANSOFORM_REASON = new EnumUtils<>(TransformReason.class);
            Classes.registerClass(new ClassInfo<>(TransformReason.class, "transformreason")
                    .user("transform ?reasons?")
                    .name("Transform Reason")
                    .description("Represents the different reasons for transforming in the entity transform event.")
                    .usage(TRANSOFORM_REASON.getAllNames())
                    .since("2.5.3")
                    .parser(TRANSOFORM_REASON.getParser()));
        }

        if (Classes.getExactClassInfo(QuitReason.class) == null) {
            EnumUtils<QuitReason> QUIT_REASON = new EnumUtils<>(QuitReason.class);
            Classes.registerClass(new ClassInfo<>(QuitReason.class, "quitreason")
                    .user("quit ?reasons?")
                    .name("Quit Reason")
                    .description("Represents the different reasons for calling the player quit event.")
                    .usage(QUIT_REASON.getAllNames())
                    .since("INSERT VERSION")
                    .parser(QUIT_REASON.getParser()));
        }

        // Only register if no other addons have registered this class
        if (Classes.getExactClassInfo(Particle.class) == null) {
            Classes.registerClass(new ClassInfo<>(Particle.class, "particle")
                    .user("particles?")
                    .name("Particle")
                    .description("Represents a particle which can be used in the 'Particle Spawn' effect.",
                            "Some particles require extra data, these are distinguished by their data type within the square brackets.",
                            "DustOption, DustTransition and Vibration each have their own functions to build the appropriate data for these particles.")
                    .usage(ParticleUtil.getNamesAsString())
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

        Classes.registerClass(new ClassInfo<>(DustOptions.class, "dustoption")
                .name(ClassInfo.NO_DOC).user("dust ?options?"));
        Classes.registerClass(new ClassInfo<>(DustTransition.class, "dusttransition")
                .name(ClassInfo.NO_DOC).user("dust ?transitions?"));
        Classes.registerClass(new ClassInfo<>(Vibration.class, "vibration")
                .name(ClassInfo.NO_DOC).user("vibrations?"));


        // == FUNCTIONS ==

        // Function to create DustOptions
        //noinspection ConstantConditions
        Functions.registerFunction(new SimpleJavaFunction<>("dustOption", new Parameter[]{
                new Parameter<>("color", DefaultClasses.COLOR, true, null),
                new Parameter<>("size", DefaultClasses.NUMBER, true, null)
        }, Classes.getExactClassInfo(DustOptions.class), true) {
            @SuppressWarnings("NullableProblems")
            @Override
            public DustOptions[] executeSimple(Object[][] params) {
                org.bukkit.Color color = ((Color) params[0][0]).asBukkitColor();
                float size = ((Number) params[1][0]).floatValue();
                return new DustOptions[]{new DustOptions(color, size)};
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
                return new DustTransition[]{
                        new DustTransition(fromColor, toColor, size)
                };
            }
        }.description("Creates a new dust transition to be used with 'dust_color_transition' particle.",
                        "Color can either be a regular color or an RGB color using Skript's rgb() function.",
                        "Size is the size the particle will be. Requires MC 1.17+")
                .examples("set {_d} to dustTransition(red, green, 10)", "set {_d} to dustTransition(blue, rgb(1,1,1), 5)")
                .since("1.11.1"));

        // Function to create Vibration
        //noinspection ConstantConditions
        Functions.registerFunction(new SimpleJavaFunction<>("vibration", new Parameter[]{
                new Parameter<>("from", DefaultClasses.LOCATION, true, null),
                new Parameter<>("to", DefaultClasses.LOCATION, true, null),
                new Parameter<>("arrivalTime", DefaultClasses.TIMESPAN, true, null)
        }, Classes.getExactClassInfo(Vibration.class), true) {
            @SuppressWarnings("NullableProblems")
            @Override
            public Vibration[] executeSimple(Object[][] params) {
                if (params[0].length == 0 || params[1].length == 0) {
                    return null;
                }
                Location origin = (Location) params[0][0];
                Location destination = (Location) params[1][0];
                int arrivalTime = (int) ((Timespan) params[2][0]).getTicks_i();
                Vibration vibration = new Vibration(origin, new BlockDestination(destination), arrivalTime);
                return new Vibration[]{vibration};
            }
        }.description("Creates a new vibration to be used with 'vibration' particle.",
                        "FROM = the origin location the particle will start at.",
                        "TO = the destination location the particle will travel to.",
                        "ARRIVAL TIME = the time it will take to arrive at the destination location. Requires MC 1.17+")
                .examples("set {_v} to vibration({loc1}, {loc2}, 10 seconds)")
                .since("1.11.1"));

    }

}
