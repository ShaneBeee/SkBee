package com.shanebeestudios.skbee.elements.bossbar.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprBossBarByID extends SimpleExpression<BossBar> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprBossBarByID.class, BossBar.class,
                "boss[ ]bar (named|with id|from id) %string%")
            .name("BossBar - From ID")
            .description("Get an already created BossBar from ID (this will NOT create a new one).")
            .examples("delete boss bar with id \"le-bar\"",
                "set {_bar} to boss bar with id \"le-bar\"",
                "add all players to {_bar}")
            .since("2.14.1")
            .register();
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
    protected BossBar @Nullable [] get(Event event) {
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
    public @NotNull String toString(Event e, boolean d) {
        return "bossbar with id " + this.key.toString(e, d);
    }

}
