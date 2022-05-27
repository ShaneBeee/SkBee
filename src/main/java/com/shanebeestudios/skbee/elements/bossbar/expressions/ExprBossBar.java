package com.shanebeestudios.skbee.elements.bossbar.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("BossBar")
@Description({"Get a BossBar from an entity (such as a wither) or create your own custom BossBar.",
        "Progress is a number between 0-100",
        "NOTE: Custom BossBars are not persistent and will not be saved on server stop/restart."})
@Examples({"set {_bar} to boss bar named \"le-bar\"",
        "set {_bar} to boss bar named \"le-bar\" with title \"Le Title\" with color bar blue with progress 50",
        "delete boss bar named \"le-bar\"",
        "set {_bar} to boss bar of target entity"})
@Since("INSERT VERSION")
public class ExprBossBar extends SimpleExpression<BossBar> {


    static {
        Skript.registerExpression(ExprBossBar.class, BossBar.class, ExpressionType.COMBINED,
                "boss[ ]bar of %entity%",
                "[new] boss[ ]bar named %string% [with title %-string%] [with color %-bossbarcolor%] " +
                        "[with style %-bossbarstyle%] [with progress %-number%]");
    }

    private int pattern;
    private Expression<Entity> entity;
    private Expression<String> key;
    private Expression<String> title;
    private Expression<BarColor> barColor;
    private Expression<BarStyle> barStyle;
    private Expression<Number> progress;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        pattern = matchedPattern;
        this.entity = pattern == 0 ? (Expression<Entity>) exprs[0] : null;
        this.key = pattern == 1 ? (Expression<String>) exprs[0] : null;
        this.title = pattern == 1 ? (Expression<String>) exprs[1] : null;
        this.barColor = pattern == 1 ? (Expression<BarColor>) exprs[2] : null;
        this.barStyle = pattern == 1 ? (Expression<BarStyle>) exprs[3] : null;
        this.progress = pattern == 1 ? (Expression<Number>) exprs[4] : null;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable BossBar[] get(Event event) {
        if (this.entity != null) {
            Entity entity = this.entity.getSingle(event);
            if (entity instanceof Boss boss) {
                return new BossBar[]{boss.getBossBar()};
            }
        } else if (this.key != null) {
            String name = this.key.getSingle(event);
            NamespacedKey key = getKey(name);
            if (key != null) {
                KeyedBossBar bossBar = Bukkit.getBossBar(key);
                if (bossBar == null) {
                    String title = null;
                    if (this.title != null && this.title.getSingle(event) != null) {
                        title = this.title.getSingle(event);
                    }

                    BarColor barColor = null;
                    if (this.barColor != null) {
                        barColor = this.barColor.getSingle(event);
                    }
                    if (barColor == null) {
                        barColor = BarColor.PURPLE;
                    }

                    BarStyle barStyle = null;
                    if (this.barStyle != null) {
                        barStyle = this.barStyle.getSingle(event);
                    }
                    if (barStyle == null) {
                        barStyle = BarStyle.SEGMENTED_20;
                    }

                    float progress = 1;
                    if (this.progress != null) {
                        Number proNumber = this.progress.getSingle(event);
                        if (proNumber != null) {
                            progress = (float) (proNumber.intValue() / 100);
                            if (progress > 1) progress = 1;
                            if (progress < 0) progress = 0;
                        }
                    }

                    bossBar = Bukkit.createBossBar(key, title, barColor, barStyle);
                    bossBar.setProgress(progress);
                }
                return new BossBar[]{bossBar};
            }

        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends BossBar> getReturnType() {
        return BossBar.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        if (pattern == 0) {
            return "boss bar of entity " + this.entity.toString(e, d);
        } else {
            String name = "boss bar named " + this.key.toString(e, d);
            String title = this.title != null ? " named " + this.title.toString(e, d) : "";
            String color = this.barColor != null ? " with color " + this.barColor.toString(e, d) : "";
            String style = this.barStyle != null ? " with style " + this.barStyle.toString(e, d) : "";
            String progress = this.progress != null ? " with progress " + this.progress.toString(e, d) : "";
            return name + title + color + style + progress;
        }
    }

    private NamespacedKey getKey(@Nullable String key) {
        if (key == null) return null;

        String[] keys = key.split(":");
        if (keys.length == 2) {
            try {
                return new NamespacedKey(keys[0], keys[1]);
            } catch (IllegalArgumentException ignore) {
            }

        } else {
            try {
                return new NamespacedKey(SkBee.getPlugin(), keys[0]);
            } catch (IllegalArgumentException ignore) {
            }
        }
        return null;
    }

}
