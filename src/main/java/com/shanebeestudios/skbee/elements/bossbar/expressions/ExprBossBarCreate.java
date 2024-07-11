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
import ch.njol.skript.util.SkriptColor;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.BossBarUtils;
import com.shanebeestudios.skbee.api.util.MathUtil;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("BossBar - Create")
@Description({"Create your own custom BossBar.",
    "**NOTE**: Progress is a number between 0-100.",
    "**NOTE**: This just creates a new custom bossbar. It will by default not be visible to anyone",
    "until you actually add players to it. See examples!!!",
    "**NOTE**: The ID is optional. If excluded, the BossBar will not save to the server."})
@Examples({"set {_bar} to boss bar with id \"le-bar\" with title \"My BossBar\"",
    "set {_bar} to boss bar with id \"le-bar\" with title \"Le Title\" with color pink with progress 50",
    "add all players to {_bar}"})
@Since("2.14.1")
public class ExprBossBarCreate extends SimpleExpression<BossBar> {

    static {
        Skript.registerExpression(ExprBossBarCreate.class, BossBar.class, ExpressionType.COMBINED,
            "[new] boss[ ]bar [(named|with id) %-string%] with title %string% [with (color|colour) %-color%] " +
                "[with style %-bossbarstyle%] [with progress %-number%]");
    }

    private Expression<String> key;
    private Expression<String> title;
    private Expression<SkriptColor> color;
    private Expression<BarStyle> barStyle;
    private Expression<Number> progress;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.key = (Expression<String>) exprs[0];
        this.title = (Expression<String>) exprs[1];
        this.color = (Expression<SkriptColor>) exprs[2];
        this.barStyle = (Expression<BarStyle>) exprs[3];
        this.progress = (Expression<Number>) exprs[4];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable BossBar[] get(Event event) {

        NamespacedKey key = null;
        if (this.key != null) {
            String keyString = this.key.getSingle(event);
            if (keyString != null) key = Util.getNamespacedKey(keyString, false);

            if (key != null) {
                KeyedBossBar bossBar = Bukkit.getBossBar(key);
                if (bossBar != null) return new BossBar[]{bossBar};
            }
        }

        String title = null;
        if (this.title != null && this.title.getSingle(event) != null) {
            title = this.title.getSingle(event);
        }

        BarColor barColor = null;
        if (this.color != null) {
            SkriptColor skriptColor = this.color.getSingle(event);
            if (skriptColor != null) {
                barColor = BossBarUtils.getBossBarColor(skriptColor);
            }
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
                progress = MathUtil.clamp(proNumber.floatValue() / 100, 0, 1);
            }
        }
        BossBar bossBar;
        if (key != null) {
            bossBar = Bukkit.createBossBar(key, title, barColor, barStyle);
        } else {
            bossBar = Bukkit.createBossBar(title, barColor, barStyle);
        }
        bossBar.setProgress(progress);

        return new BossBar[]{bossBar};
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
    public @NotNull String toString(Event e, boolean d) {
        String bar = "boss bar" + (this.key != null ? " " + this.key.toString(e, d) : "");
        String title = this.title != null ? " named " + this.title.toString(e, d) : "";
        String color = "";
        if (this.color != null) {
            color = " with color " + this.color.toString(e, d);
        }
        String style = this.barStyle != null ? " with style " + this.barStyle.toString(e, d) : "";
        String progress = this.progress != null ? " with progress " + this.progress.toString(e, d) : "";
        return bar + title + color + style + progress;
    }

}
