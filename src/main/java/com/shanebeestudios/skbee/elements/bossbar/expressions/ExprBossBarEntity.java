package com.shanebeestudios.skbee.elements.bossbar.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExprBossBarEntity extends SimpleExpression<BossBar> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprBossBarEntity.class, BossBar.class,
                "boss[ ]bar of %entity%",
                "boss[ ]bars of %players%")
            .name("BossBar - Entity/Player")
            .description("Get a BossBar from an entity (such as a wither) or all BossBars of players.",
                "NOTE: BossBars from entities cannot be saved in global variables, as the entity may not be loaded on the",
                "server when that variable is trying to load. Custom BossBars and BossBars from players can be saved in variables.")
            .examples("set {_bar} to boss bar of target entity",
                "set {_bars::*} to boss bars of player")
            .since("2.14.1")
            .register();
    }

    private int pattern;
    private Expression<Entity> entity;
    private Expression<Player> players;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        pattern = matchedPattern;
        this.entity = pattern == 0 ? (Expression<Entity>) exprs[0] : null;
        this.players = pattern == 1 ? (Expression<Player>) exprs[0] : null;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable BossBar[] get(Event event) {
        if (pattern == 0 && this.entity != null) {
            Entity entity = this.entity.getSingle(event);
            if (entity instanceof Boss boss) {
                return new BossBar[]{boss.getBossBar()};
            }
        } else if (pattern == 1 && this.players != null) {
            List<BossBar> bars = new ArrayList<>();
            Player[] players = this.players.getArray(event);
            Bukkit.getBossBars().forEachRemaining(bossBar -> {
                for (Player player : players) {
                    if (bossBar.getPlayers().contains(player) && !bars.contains(bossBar)) {
                        bars.add(bossBar);
                    }
                }
            });
            return bars.toArray(new BossBar[0]);
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return pattern == 0;
    }

    @Override
    public @NotNull Class<? extends BossBar> getReturnType() {
        return BossBar.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        if (pattern == 0) return "boss bar of entity " + this.entity.toString(e, d);
        return "boss bars of players " + this.players.toString(e, d);
    }

}
