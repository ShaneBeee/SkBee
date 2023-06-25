package com.shanebeestudios.skbee.elements.bossbar.types;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.yggdrasil.Fields;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;

@SuppressWarnings("unused")
public class Types {

    static {
        if (Classes.getExactClassInfo(BossBar.class) == null) {
            Classes.registerClass(new ClassInfo<>(BossBar.class, "bossbar")
                    .user("boss ?bars?")
                    .name("BossBar")
                    .description("Represents a BossBar. Either from an entity or a custom one.",
                            "Custom BossBars can be deleted, BossBars of entities can't be deleted.",
                            "NOTE: BossBars from entities cannot be saved in global variables, as the entity may not be loaded",
                            "on the server when that variable is trying to load. Custom BossBars can be saved in variables.")
                    .examples("set {_bar} to boss bar named \"le-bar\"")
                    .since("1.16.0")
                    .parser(new Parser<>() {

                        @SuppressWarnings("NullableProblems")
                        @Override
                        public boolean canParse(ParseContext context) {
                            return false;
                        }

                        @Override
                        public @NotNull String toString(BossBar bossBar, int flags) {
                            String bar;
                            if (bossBar instanceof KeyedBossBar keyedBossBar) {
                                bar = keyedBossBar.getKey().toString();
                            } else {
                                bar = "nokey:" + bossBar.getTitle();
                            }
                            return "BossBar[" + bar + "]";
                        }

                        @Override
                        public @NotNull String toVariableNameString(BossBar bossBar) {
                            return toString(bossBar, 0);
                        }
                    })
                    .changer(new Changer<>() {
                        @SuppressWarnings("NullableProblems")
                        @Override
                        public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
                            if (mode == ChangeMode.DELETE) return CollectionUtils.array();
                            return null;
                        }

                        @SuppressWarnings("NullableProblems")
                        @Override
                        public void change(BossBar[] what, @Nullable Object[] delta, ChangeMode mode) {
                            if (mode == ChangeMode.DELETE) {
                                for (BossBar bossBar : what) {
                                    bossBar.removeAll();
                                    if (bossBar instanceof KeyedBossBar keyedBossBar) {
                                        Bukkit.removeBossBar(keyedBossBar.getKey());
                                    }
                                }
                            }
                        }
                    })
                    .serializer(new Serializer<>() {

                        @SuppressWarnings("NullableProblems")
                        @Override
                        public Fields serialize(BossBar bossBar) {
                            Fields fields = new Fields();
                            if (bossBar instanceof KeyedBossBar keyedBossBar) {
                                NamespacedKey key = keyedBossBar.getKey();
                                fields.putObject("namespace", key.getNamespace());
                                fields.putObject("key", key.getKey());
                            }
                            return fields;
                        }

                        @SuppressWarnings("NullableProblems")
                        @Override
                        public void deserialize(BossBar o, Fields f) {
                        }

                        @SuppressWarnings("NullableProblems")
                        @Override
                        protected BossBar deserialize(Fields fields) throws StreamCorruptedException {
                            String name = fields.getObject("namespace", String.class);
                            String key = fields.getObject("key", String.class);

                            assert name != null;
                            assert key != null;
                            NamespacedKey namespacedKey = new NamespacedKey(name, key);
                            KeyedBossBar bossBar = Bukkit.getBossBar(namespacedKey);
                            if (bossBar == null) {
                                throw new StreamCorruptedException("Missing Bossbar: [" + name + ":" + key + "]");
                            }
                            return bossBar;
                        }

                        @Override
                        public boolean mustSyncDeserialization() {
                            return false;
                        }

                        @Override
                        protected boolean canBeInstantiated() {
                            return false;
                        }
                    }));
        } else {
            Util.logLoading("&eIt looks like another addon registered 'bossbar' already.");
            Util.logLoading("&eYou may have to use their BossBars in SkBee's BossBar elements.");
        }


        // TODO (dec 11/2022)
        // Remove in future
        if (Classes.getExactClassInfo(BarColor.class) == null && Classes.getClassInfoNoError("bossbarcolor") == null) {
            EnumWrapper<BarColor> BAR_COLOR_ENUM = new EnumWrapper<>(BarColor.class, "bar", "");
            Classes.registerClass(new ClassInfo<>(BarColor.class, "bossbarcolor")
                    .user("boss ?bar ?colors?")
                    .name("BossBar Color")
                    .description("Represents the color options of a BossBar. ",
                            "Colors are prefixed with \"bar\" (such as `bar blue`) to differentiate from Skript colors.",
                            "\nDEPRECATED: Will be removed in the future")
                    .usage(BAR_COLOR_ENUM.getAllNames())
                    .examples("set bar color of {_bar} to bar blue")
                    .since("1.16.0")
                    .parser(new Parser<>() {

                        @SuppressWarnings("NullableProblems")
                        @Override
                        public boolean canParse(ParseContext context) {
                            return true;
                        }

                        @SuppressWarnings("NullableProblems")
                        @Override
                        public @Nullable BarColor parse(String string, ParseContext context) {
                            Skript.warning("Please use SkriptColors instead of BarColors, BarColors will be removed in the future.");
                            return BAR_COLOR_ENUM.parse(string);
                        }

                        @SuppressWarnings("NullableProblems")
                        @Override
                        public String toString(BarColor o, int flags) {
                            return BAR_COLOR_ENUM.toString(o, flags);
                        }

                        @SuppressWarnings("NullableProblems")
                        @Override
                        public String toVariableNameString(BarColor o) {
                            return BAR_COLOR_ENUM.toString(o, 0);
                        }
                    }));
        }

        if (Classes.getExactClassInfo(BarStyle.class) == null && Classes.getClassInfoNoError("bossbarstyle") == null) {
            EnumWrapper<BarStyle> BAR_STYLE_ENUM = new EnumWrapper<>(BarStyle.class);
            // Prevent conflict with Skript's `is solid` condition
            BAR_STYLE_ENUM.replace("solid", "solid bar");
            Classes.registerClass(new ClassInfo<>(BarStyle.class, "bossbarstyle")
                    .user("boss ?bar ?styles?")
                    .name("BossBar Style")
                    .description("Represents the style options of a BossBar.")
                    .usage(BAR_STYLE_ENUM.getAllNames())
                    .examples("set bar style of {_bar} to segmented 20")
                    .since("1.16.0")
                    .parser(BAR_STYLE_ENUM.getParser()));
        } else {
            Util.logLoading("&eIt looks like another addon registered 'boss bar style' already.");
            Util.logLoading("&eYou may have to use their BossBar styles in SkBee's BossBar elements.");
        }

        if (Classes.getExactClassInfo(BarFlag.class) == null && Classes.getClassInfoNoError("bossbarflag") == null) {
            EnumWrapper<BarFlag> BAR_FLAG_ENUM = new EnumWrapper<>(BarFlag.class);
            Classes.registerClass(new ClassInfo<>(BarFlag.class, "bossbarflag")
                    .user("boss ?bar ?flags?")
                    .name("BossBar Flag")
                    .description("Represents the flag options of a BossBar.")
                    .usage(BAR_FLAG_ENUM.getAllNames())
                    .examples("set bar flag darken sky of {_bar} to true")
                    .since("1.16.0")
                    .parser(BAR_FLAG_ENUM.getParser()));
        } else {
            Util.logLoading("&eIt looks like another addon registered 'boss bar flag' already.");
            Util.logLoading("&eYou may have to use their BossBar flags in SkBee's BossBar elements.");
        }
    }

}
