package com.shanebeestudios.skbee.elements.bossbar.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("BossBar - From ID")
@Description("Get a BossBar from ID.")
@Examples({"delete boss bar with id \"le-bar\"",
        "set {_bar} to boss bar with id \"le-bar\"",
        "add all players to {_bar}"})
@Since("INSERT VERSION")
public class ExprBossBarByID extends SimpleExpression<BossBar> {

    static {
        Skript.registerExpression(ExprBossBarByID.class, BossBar.class, ExpressionType.COMBINED,
                "boss[ ]bar (named|with id|from id) %string%");
    }

    private Expression<String> key;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.key = (Expression<String>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable BossBar[] get(Event event) {
        String name = this.key.getSingle(event);
        if (name == null) return null;
        NamespacedKey key = Util.getNamespacedKey(name, true);
        if (key != null) {
            KeyedBossBar bossBar = Bukkit.getBossBar(key);
            if (bossBar != null) return new BossBar[]{bossBar};
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
        return "bossbar with id " + this.key.toString(e, d);
    }

}
