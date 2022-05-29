package com.shanebeestudios.skbee.elements.bossbar.types;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.EnumParser;
import com.shanebeestudios.skbee.api.util.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.eclipse.jdt.annotation.Nullable;

public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(BossBar.class, "bossbar")
                .user("boss ?bars?")
                .name("BossBar")
                .description("Represents a BossBar. Either from an entity or a custom one.",
                        "Custom BossBars can be deleted, BossBars of entities can't be deleted.",
                        "NOTE: Custom BossBars are not persistent and won't be saved across server restart/stop.")
                .examples("set {_bar} to boss bar named \"le-bar\"")
                .since("1.16.0")
                .parser(new Parser<>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(BossBar bossBar, int flags) {
                        if (bossBar instanceof KeyedBossBar keyedBossBar) {
                            return keyedBossBar.getKey().toString();
                        }
                        return "nokey:" + bossBar.getTitle();
                    }

                    @Override
                    public String toVariableNameString(BossBar o) {
                        return "";
                    }
                })
                .changer(new Changer<>() {
                    @Override
                    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
                        if (mode == ChangeMode.DELETE) return CollectionUtils.array();
                        return null;
                    }

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
                }));


        EnumUtils<BarColor> BAR_COLOR_ENUM = new EnumUtils<>(BarColor.class, "bar", "");
        Classes.registerClass(new ClassInfo<>(BarColor.class, "bossbarcolor")
                .user("boss ?bar ?colors?")
                .name("BossBar Color")
                .description("Represents the color options of a BossBar. ",
                        "Colors are prefixed with \"bar\" (such as `bar blue`) to differentiate from Skript colors")
                .usage(BAR_COLOR_ENUM.getAllNames())
                .examples("set bar color of {_bar} to bar blue")
                .since("1.16.0")
                .parser(BAR_COLOR_ENUM.getParser()));

        EnumUtils<BarStyle> BAR_STYLE_ENUM = new EnumUtils<>(BarStyle.class);
        Classes.registerClass(new ClassInfo<>(BarStyle.class, "bossbarstyle")
                .user("boss ?bar ?styles?")
                .name("BossBar Style")
                .description("Represents the style options of a BossBar.")
                .usage(BAR_STYLE_ENUM.getAllNames())
                .examples("set bar style of {_bar} to segmented 20")
                .since("1.16.0")
                .parser(BAR_STYLE_ENUM.getParser()));

        EnumUtils<BarFlag> BAR_FLAG_ENUM = new EnumUtils<>(BarFlag.class);
        Classes.registerClass(new ClassInfo<>(BarFlag.class, "bossbarflag")
                .user("boss ?bar ?flags?")
                .name("BossBar Flag")
                .description("Represents the flag options of a BossBar.")
                .usage(BAR_FLAG_ENUM.getAllNames())
                .examples("set bar flag darken sky of {_bar} to true")
                .since("1.16.0")
                .parser(BAR_FLAG_ENUM.getParser()));
    }
}
