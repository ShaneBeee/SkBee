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
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.MathUtil;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("NullableProblems")
@Name("BossBar - Properties")
@Description({"Represents the properties of a BossBar that can be changed.",
        "Progress of a bar is a number from 0-100.",
        "BossBar colors are not the same as Skript colors, see docs for BossBar color options."})
@Examples({"add all players to bar players of {_bar}",
        "remove player from bar players of {_bar}",
        "set bar color of {_bar} to bar blue",
        "set bar style of {_bar} to segmented 20",
        "set bar title of {_bar} to \"Le-Title\"",
        "reset bar title of {_bar}",
        "set bar progress of {_bar} to 100",
        "set bar flag darken sky of {_bar} to true"})
@Since("1.16.0")
public class ExprBossBarProperties extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprBossBarProperties.class, Object.class, ExpressionType.COMBINED,
                "[boss[ ]]bar players of %bossbar%",
                "[boss[ ]]bar color of %bossbar%",
                "[boss[ ]]bar style of %bossbar%",
                "[boss[ ]]bar title of %bossbar%",
                "[boss[ ]]bar progress of %bossbar%",
                "[boss[ ]]bar flag %bossbarflag% of %bossbar%",
                "[boss[ ]]bar visibility of %bossbar%");
    }

    private static final int PLAYERS = 0;
    private static final int BAR_COLOR = 1;
    private static final int BAR_STYLE = 2;
    private static final int BAR_TITLE = 3;
    private static final int BAR_PROGRESS = 4;
    private static final int BAR_FLAG = 5;
    private static final int BAR_VISIBILITY = 6;
    private int pattern;
    private Expression<BossBar> bossBar;
    private Expression<BarFlag> barFlag;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        pattern = matchedPattern;
        bossBar = (Expression<BossBar>) exprs[pattern == 5 ? 1 : 0];
        barFlag = pattern == 5 ? (Expression<BarFlag>) exprs[0] : null;
        return true;
    }

    @Override
    protected @Nullable Object[] get(Event event) {
        BossBar bossBar = this.bossBar.getSingle(event);
        if (bossBar == null) return null;

        switch (pattern) {
            case PLAYERS:
                return bossBar.getPlayers().toArray(new Player[0]);
            case BAR_COLOR:
                return new BarColor[]{bossBar.getColor()};
            case BAR_STYLE:
                return new BarStyle[]{bossBar.getStyle()};
            case BAR_TITLE:
                return new String[]{bossBar.getTitle()};
            case BAR_PROGRESS:
                return new Number[]{(bossBar.getProgress() * 100)};
            case BAR_FLAG:
                BarFlag barFlag = this.barFlag.getSingle(event);
                if (barFlag == null) return null;
                return new Boolean[]{bossBar.hasFlag(barFlag)};
            case BAR_VISIBILITY:
                return new Boolean[]{bossBar.isVisible()};
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (pattern == PLAYERS) {
            if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.SET || mode == ChangeMode.RESET) {
                return CollectionUtils.array(Player[].class);
            }
        } else if (pattern == BAR_COLOR && mode == ChangeMode.SET) {
            return CollectionUtils.array(BarColor.class);
        } else if (pattern == BAR_STYLE && mode == ChangeMode.SET) {
            return CollectionUtils.array(BarStyle.class);
        } else if (pattern == BAR_TITLE && (mode == ChangeMode.SET || mode == ChangeMode.RESET || mode == ChangeMode.DELETE)) {
            return CollectionUtils.array(String.class);
        } else if (pattern == BAR_PROGRESS) {
            if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.SET || mode == ChangeMode.RESET) {
                return CollectionUtils.array(Number.class);
            }
        } else if (pattern == BAR_FLAG) {
            if (mode == ChangeMode.SET || mode == ChangeMode.REMOVE) {
                return CollectionUtils.array(Boolean.class);
            }
        } else if (pattern == BAR_VISIBILITY) {
            if (mode == ChangeMode.SET) {
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

        if (pattern == PLAYERS) {
            if (delta instanceof Player[] players) {
                if (mode == ChangeMode.SET || mode == ChangeMode.RESET) {
                    bossBar.removeAll();
                }
                if (mode == ChangeMode.ADD || mode == ChangeMode.SET) {
                    for (Player player : players) {
                        bossBar.addPlayer(player);
                    }
                } else if (mode == ChangeMode.REMOVE) {
                    for (Player player : players) {
                        bossBar.removePlayer(player);
                    }
                }

            }
        } else if (pattern == BAR_COLOR && mode == ChangeMode.SET) {
            if (object instanceof BarColor barColor) {
                bossBar.setColor(barColor);
            }
        } else if (pattern == BAR_STYLE && mode == ChangeMode.SET) {
            if (object instanceof BarStyle barStyle) {
                bossBar.setStyle(barStyle);
            }
        } else if (pattern == BAR_TITLE) {
            if (mode == ChangeMode.SET && object instanceof String title) {
                bossBar.setTitle(title);
            } else if (mode == ChangeMode.RESET || mode == ChangeMode.DELETE) {
                bossBar.setTitle(null);
            }
        } else if (pattern == BAR_PROGRESS) {
            if (mode == ChangeMode.RESET) {
                bossBar.setProgress(1.0);
            } else if (object instanceof Number number) {
                double newProgress = 0;
                double oldProgress = bossBar.getProgress();
                float progress = number.floatValue() / 100;
                if (mode == ChangeMode.SET) {
                    newProgress = progress;
                } else if (mode == ChangeMode.ADD) {
                    newProgress = oldProgress + progress;
                } else if (mode == ChangeMode.REMOVE) {
                    newProgress = oldProgress - progress;
                }

                newProgress = MathUtil.clamp((float) newProgress, 0, 1);
                bossBar.setProgress(newProgress);
            }
        } else if (pattern == BAR_FLAG) {
            BarFlag barFlag = this.barFlag.getSingle(event);
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
        } else if (pattern == BAR_VISIBILITY && object instanceof Boolean isVisible) {
            bossBar.setVisible(isVisible);
        }
    }

    @Override
    public boolean isSingle() {
        return pattern != 0;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return switch (pattern) {
            case PLAYERS -> Player.class;
            case BAR_COLOR -> BarColor.class;
            case BAR_STYLE -> BarStyle.class;
            case BAR_TITLE -> String.class;
            case BAR_PROGRESS -> Number.class;
            case BAR_FLAG, BAR_VISIBILITY -> Boolean.class;
            default -> throw new IllegalStateException("Unexpected value: " + pattern);
        };
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String prop = switch (pattern) {
            case PLAYERS -> "bar players";
            case BAR_COLOR -> "bar color";
            case BAR_STYLE -> "bar style";
            case BAR_TITLE -> "bar title";
            case BAR_PROGRESS -> "bar progress";
            case BAR_FLAG -> "bar flag " + this.barFlag.toString(e, d);
            case BAR_VISIBILITY -> "bar visibility";
            default -> "null?!?!";
        };
        return prop + " of boss bar " + this.bossBar.toString(e, d);
    }

}
