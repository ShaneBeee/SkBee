package com.shanebeestudios.skbee.elements.bossbar.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExprBossBarAll extends SimpleExpression<BossBar> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprBossBarAll.class, BossBar.class,
                "all boss[ ]bars")
            .name("BossBar - All")
            .description("Get a list of all BossBars.")
            .examples("set {_bars::*} to all bossbars")
            .since("2.14.1")
            .register();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable BossBar[] get(Event event) {
        List<BossBar> bars = new ArrayList<>();
        Bukkit.getBossBars().forEachRemaining(bars::add);
        return bars.toArray(new BossBar[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends BossBar> getReturnType() {
        return BossBar.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "all boss bars";
    }

}
