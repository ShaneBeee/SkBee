package com.shanebeestudios.skbee.elements.bossbar.types;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.yggdrasil.Fields;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
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
                            "Players can be added to/removed from BossBars.",
                            "Custom BossBars can be deleted, BossBars of entities cannot be deleted.",
                            "NOTE: BossBars from entities cannot be saved in global variables, as the entity may not be loaded",
                            "on the server when that variable is trying to load. Custom BossBars can be saved in variables.")
                    .examples("set {_bar} to boss bar named \"le-bar\"",
                            "add all players to {_bar}")
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
                        public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
                            if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.REMOVE_ALL)
                                return CollectionUtils.array(Player[].class);
                            if (mode == ChangeMode.DELETE) return CollectionUtils.array();
                            return null;
                        }

                        @SuppressWarnings({"NullableProblems", "ConstantValue"})
                        @Override
                        public void change(BossBar[] bossBars, @Nullable Object[] delta, ChangeMode mode) {
                            if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.REMOVE_ALL) {
                                if (delta == null) return;

                                for (BossBar bossBar : bossBars) {
                                    for (Object object : delta) {
                                        if (object instanceof Player player) {
                                            if (mode == ChangeMode.ADD) bossBar.addPlayer(player);
                                            else bossBar.removePlayer(player);
                                        }
                                    }
                                }
                            }
                            if (mode == ChangeMode.DELETE) {
                                for (BossBar bossBar : bossBars) {
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

        if (Classes.getExactClassInfo(BarStyle.class) == null && Classes.getClassInfoNoError("bossbarstyle") == null) {
            EnumWrapper<BarStyle> BAR_STYLE_ENUM = new EnumWrapper<>(BarStyle.class);
            // Prevent conflict with Skript's `is solid` condition
            BAR_STYLE_ENUM.replace("solid", "solid bar");
            Classes.registerClass(BAR_STYLE_ENUM.getClassInfo("bossbarstyle")
                    .user("boss ?bar ?styles?")
                    .name("BossBar Style")
                    .description("Represents the style options of a BossBar.")
                    .examples("set bar style of {_bar} to segmented 20")
                    .since("1.16.0"));
        } else {
            Util.logLoading("&eIt looks like another addon registered 'boss bar style' already.");
            Util.logLoading("&eYou may have to use their BossBar styles in SkBee's BossBar elements.");
        }

        if (Classes.getExactClassInfo(BarFlag.class) == null && Classes.getClassInfoNoError("bossbarflag") == null) {
            EnumWrapper<BarFlag> BAR_FLAG_ENUM = new EnumWrapper<>(BarFlag.class);
            Classes.registerClass(BAR_FLAG_ENUM.getClassInfo("bossbarflag")
                    .user("boss ?bar ?flags?")
                    .name("BossBar Flag")
                    .description("Represents the flag options of a BossBar.")
                    .examples("set bar flag darken sky of {_bar} to true")
                    .since("1.16.0"));
        } else {
            Util.logLoading("&eIt looks like another addon registered 'boss bar flag' already.");
            Util.logLoading("&eYou may have to use their BossBar flags in SkBee's BossBar elements.");
        }
    }

}
