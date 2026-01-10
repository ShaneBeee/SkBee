package com.shanebeestudios.skbee.elements.bossbar.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.SkriptColor;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.BossBarUtils;
import com.shanebeestudios.skbee.api.util.MathUtil;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("BossBar - Properties")
@Description({"Represents the properties of a BossBar that can be changed.",
    "Progress of a bar is a number from 0-100."})
@Examples({"set {_players::*} to bar players of {_bar}",
    "set bar color of {_bar} to blue",
    "set bar style of {_bar} to notched_20",
    "set bar title of {_bar} to \"Le-Title\"",
    "reset bar title of {_bar}",
    "set bar progress of {_bar} to 100",
    "set bar flag darken_screen of {_bar} to true"})
@Since("2.14.1")
public class ExprBossBarProperties extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprBossBarProperties.class, Object.class, ExpressionType.COMBINED,
            "[boss[ ]]bar players of %bossbar%",
            "[boss[ ]]bar (color|colour) of %bossbar%",
            "[boss[ ]]bar style of %bossbar%",
            "[boss[ ]]bar title of %bossbar%",
            "[boss[ ]]bar progress of %bossbar%",
            "[boss[ ]]bar flag %bossbarflag% of %bossbar%");
    }

    private static final int PLAYERS = 0;
    private static final int BAR_COLOR = 1;
    private static final int BAR_STYLE = 2;
    private static final int BAR_TITLE = 3;
    private static final int BAR_PROGRESS = 4;
    private static final int BAR_FLAG = 5;
    private int pattern;
    private Expression<BossBar> bossBar;
    private Expression<BossBar.Flag> barFlag;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;
        this.bossBar = (Expression<BossBar>) exprs[this.pattern == 5 ? 1 : 0];
        this.barFlag = this.pattern == 5 ? (Expression<BossBar.Flag>) exprs[0] : null;
        return true;
    }

    @Override
    protected Object @Nullable [] get(Event event) {
        BossBar bossBar = this.bossBar.getSingle(event);
        if (bossBar == null) return null;

        switch (this.pattern) {
            case PLAYERS -> {
                List<Player> players = new ArrayList<>();
                bossBar.viewers().forEach(bossBarViewer -> {
                    if (bossBarViewer instanceof Player player) players.add(player);
                });
                return players.toArray(new Player[0]);
            }
            case BAR_COLOR -> {
                return new SkriptColor[]{BossBarUtils.getSkriptColor(bossBar.color())};
            }
            case BAR_STYLE -> {
                return new BossBar.Overlay[]{bossBar.overlay()};
            }
            case BAR_TITLE -> {
                return new ComponentWrapper[]{ComponentWrapper.fromComponent(bossBar.name())};
            }
            case BAR_PROGRESS -> {
                return new Number[]{(bossBar.progress() * 100)};
            }
            case BAR_FLAG -> {
                BossBar.Flag barFlag = this.barFlag.getSingle(event);
                if (barFlag == null) return null;
                return new Boolean[]{bossBar.hasFlag(barFlag)};
            }
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (this.pattern == PLAYERS) {
            if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.SET || mode == ChangeMode.RESET) {
                return CollectionUtils.array(Player[].class);
            }
        } else if (this.pattern == BAR_COLOR && mode == ChangeMode.SET) {
            return CollectionUtils.array(Color.class, BossBar.Color.class);
        } else if (this.pattern == BAR_STYLE && mode == ChangeMode.SET) {
            return CollectionUtils.array(BossBar.Overlay.class);
        } else if (this.pattern == BAR_TITLE && (mode == ChangeMode.SET || mode == ChangeMode.RESET || mode == ChangeMode.DELETE)) {
            return CollectionUtils.array(String.class, ComponentWrapper.class);
        } else if (this.pattern == BAR_PROGRESS) {
            if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.SET || mode == ChangeMode.RESET) {
                return CollectionUtils.array(Number.class);
            }
        } else if (this.pattern == BAR_FLAG) {
            if (mode == ChangeMode.SET || mode == ChangeMode.REMOVE) {
                return CollectionUtils.array(Boolean.class);
            }
        }
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Object object = delta != null ? delta[0] : null;
        BossBar bossBar = this.bossBar.getSingle(event);
        if (bossBar == null) return;

        if (this.pattern == PLAYERS) {
            if (delta instanceof Player[] players) {
                if (mode == ChangeMode.SET || mode == ChangeMode.RESET) {
                    bossBar.viewers().forEach(bossBarViewer -> {
                        if (bossBarViewer instanceof Audience audience) bossBar.removeViewer(audience);
                    });
                }
                if (mode == ChangeMode.ADD || mode == ChangeMode.SET) {
                    for (Player player : players) {
                        bossBar.addViewer(player);
                    }
                } else if (mode == ChangeMode.REMOVE) {
                    for (Player player : players) {
                        bossBar.removeViewer(player);
                    }
                }

            }
        } else if (this.pattern == BAR_COLOR && mode == ChangeMode.SET) {
            if (object instanceof SkriptColor skriptColor) {
                BossBar.Color bossBarColor = BossBarUtils.getBossBarColor(skriptColor);
                bossBar.color(bossBarColor);
            } else if (object instanceof BossBar.Color barColor) {
                bossBar.color(barColor);
            }
        } else if (this.pattern == BAR_STYLE && mode == ChangeMode.SET) {
            if (object instanceof BossBar.Overlay barStyle) {
                bossBar.overlay(barStyle);
            }
        } else if (this.pattern == BAR_TITLE) {
            if (mode == ChangeMode.SET) {
                if (object instanceof String s) bossBar.name(ComponentWrapper.fromText(s).getComponent());
                else if (object instanceof ComponentWrapper cw) bossBar.name(cw.getComponent());
            } else if (mode == ChangeMode.RESET || mode == ChangeMode.DELETE) {
                bossBar.name(Component.empty());
            }
        } else if (this.pattern == BAR_PROGRESS) {
            if (mode == ChangeMode.RESET) {
                bossBar.progress(1.0f);
            } else if (object instanceof Number number) {
                float newProgress = 0;
                float oldProgress = bossBar.progress();
                float progress = number.floatValue() / 100;
                if (mode == ChangeMode.SET) {
                    newProgress = progress;
                } else if (mode == ChangeMode.ADD) {
                    newProgress = oldProgress + progress;
                } else if (mode == ChangeMode.REMOVE) {
                    newProgress = oldProgress - progress;
                }

                newProgress = MathUtil.clamp(newProgress, 0, 1);
                if (Float.isNaN(newProgress)) newProgress = 0;
                bossBar.progress(newProgress);
            }
        } else if (this.pattern == BAR_FLAG) {
            BossBar.Flag barFlag = this.barFlag.getSingle(event);
            if (barFlag == null) return;

            if (mode == ChangeMode.SET && object instanceof Boolean setter) {
                if (setter) {
                    bossBar.addFlag(barFlag);
                } else {
                    bossBar.removeFlag(barFlag);
                }
            } else if (mode == ChangeMode.REMOVE) {
                bossBar.removeFlag(barFlag);
            }
        }
    }

    @Override
    public boolean isSingle() {
        return this.pattern != 0;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return switch (this.pattern) {
            case PLAYERS -> Player.class;
            case BAR_COLOR -> SkriptColor.class;
            case BAR_STYLE -> BossBar.Overlay.class;
            case BAR_TITLE -> ComponentWrapper.class;
            case BAR_PROGRESS -> Number.class;
            case BAR_FLAG ->  Boolean.class;
            default -> throw new IllegalStateException("Unexpected value: " + this.pattern);
        };
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String prop = switch (this.pattern) {
            case PLAYERS -> "bar players";
            case BAR_COLOR -> "bar color";
            case BAR_STYLE -> "bar style";
            case BAR_TITLE -> "bar title";
            case BAR_PROGRESS -> "bar progress";
            case BAR_FLAG -> "bar flag " + this.barFlag.toString(e, d);
            default -> "null?!?!";
        };
        return prop + " of boss bar " + this.bossBar.toString(e, d);
    }

}
