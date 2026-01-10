package com.shanebeestudios.skbee.elements.bossbar.types;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.BossBarUtils;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import com.shanebeestudios.skbee.api.wrapper.EnumWrapper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
                    "**NOTE**: BossBars cannot be saved in global variables.")
                .examples("set {_bar} to boss bar named \"le-bar\"",
                    "add all players to {_bar}")
                .since("1.16.0")
                .parser(new Parser<>() {
                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(BossBar bossBar, int flags) {
                        Key key = BossBarUtils.getKey(bossBar);
                        String name;
                        if (key == null) {
                            name = ComponentWrapper.fromComponent(bossBar.name()).toString();
                        } else {
                            name = key.toString();
                        }
                        return "BossBar[" + name + "]";
                    }

                    @Override
                    public @NotNull String toVariableNameString(BossBar bossBar) {
                        return toString(bossBar, 0);
                    }
                })
                .changer(new Changer<>() {
                    @Override
                    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
                        if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.REMOVE_ALL)
                            return CollectionUtils.array(Audience[].class);
                        if (mode == ChangeMode.DELETE) return CollectionUtils.array();
                        return null;
                    }

                    @SuppressWarnings("ConstantValue")
                    @Override
                    public void change(BossBar[] bossBars, @Nullable Object[] delta, ChangeMode mode) {
                        if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.REMOVE_ALL) {
                            if (delta == null) return;

                            for (BossBar bossBar : bossBars) {
                                for (Object object : delta) {
                                    if (object instanceof Audience audience) {
                                        if (mode == ChangeMode.ADD) bossBar.addViewer(audience);
                                        else bossBar.removeViewer(audience);
                                    }
                                }
                            }
                        }
                        if (mode == ChangeMode.DELETE) {
                            for (BossBar bossBar : bossBars) {
                                bossBar.viewers().forEach(bossBarViewer -> {
                                    if (bossBarViewer instanceof Audience audience) bossBar.removeViewer(audience);
                                });
                                BossBarUtils.removeBossBar(bossBar);
                            }
                        }
                    }
                }));
        } else {
            Util.logLoading("&eIt looks like another addon registered 'bossbar' already.");
            Util.logLoading("&eYou may have to use their BossBars in SkBee's BossBar elements.");
        }

        if (Classes.getExactClassInfo(BossBar.Overlay.class) == null && Classes.getClassInfoNoError("bossbarstyle") == null) {
            EnumWrapper<BossBar.Overlay> BAR_STYLE_ENUM = new EnumWrapper<>(BossBar.Overlay.class);
            // Prevent conflict with Skript's `is solid` condition
            BAR_STYLE_ENUM.replace("solid", "solid bar");
            Classes.registerClass(BAR_STYLE_ENUM.getClassInfo("bossbarstyle")
                .user("boss ?bar ?styles?")
                .name("BossBar Style")
                .description("Represents the style options of a BossBar.")
                .examples("set bar style of {_bar} to notched_20")
                .since("1.16.0"));
        } else {
            Util.logLoading("&eIt looks like another addon registered 'boss bar style' already.");
            Util.logLoading("&eYou may have to use their BossBar styles in SkBee's BossBar elements.");
        }

        if (Classes.getExactClassInfo(BossBar.Flag.class) == null && Classes.getClassInfoNoError("bossbarflag") == null) {
            EnumWrapper<BossBar.Flag> BAR_FLAG_ENUM = new EnumWrapper<>(BossBar.Flag.class);
            Classes.registerClass(BAR_FLAG_ENUM.getClassInfo("bossbarflag")
                .user("boss ?bar ?flags?")
                .name("BossBar Flag")
                .description("Represents the flag options of a BossBar.")
                .examples("set bar flag darken_screen of {_bar} to true")
                .since("1.16.0"));
        } else {
            Util.logLoading("&eIt looks like another addon registered 'boss bar flag' already.");
            Util.logLoading("&eYou may have to use their BossBar flags in SkBee's BossBar elements.");
        }
    }

}
