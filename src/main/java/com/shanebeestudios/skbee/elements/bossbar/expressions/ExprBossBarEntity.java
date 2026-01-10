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
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("BossBar - Entity/Player")
@Description({"Get a BossBar from an entity (such as a wither) or all BossBars of players.",
    "NOTE: BossBars from entities cannot be saved in global variables, as the entity may not be loaded on the",
    "server when that variable is trying to load. Custom BossBars and BossBars from players can be saved in variables."})
@Examples({"set {_bar} to boss bar of target entity",
    "set {_bars::*} to boss bars of player"})
@Since("2.14.1")
public class ExprBossBarEntity extends SimpleExpression<BossBar> {

    static {
        Skript.registerExpression(ExprBossBarEntity.class, BossBar.class, ExpressionType.COMBINED,
            "boss[ ]bar of %entity%",
            "boss[ ]bars of %players%");
    }

    private int pattern;
    private Expression<Entity> entity;
    private Expression<Player> players;

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.pattern = matchedPattern;
        if (this.pattern == 0) {
            Skript.error("Getting the bossbar of an entity is currently broken!");
            return false;
        }
        this.entity = this.pattern == 0 ? (Expression<Entity>) exprs[0] : null;
        this.players = this.pattern == 1 ? (Expression<Player>) exprs[0] : null;
        return true;
    }

    @Override
    protected @Nullable BossBar[] get(Event event) {
        if (this.pattern == 0 && this.entity != null) {
            Entity entity = this.entity.getSingle(event);
            if (entity instanceof Boss boss) {
                //TODO what to do?!?! (Boss doesnt have a method to get an Adventure bossbar)
                //return new BossBar[]{boss.getBossBar()};
            }
        } else if (this.pattern == 1 && this.players != null) {
            List<BossBar> bars = new ArrayList<>();
            for (Player player : this.players.getArray(event)) {
                player.activeBossBars().forEach(bars::add);

            }
            return bars.toArray(new BossBar[0]);
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return this.pattern == 0;
    }

    @Override
    public @NotNull Class<? extends BossBar> getReturnType() {
        return BossBar.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        if (this.pattern == 0) return "boss bar of entity " + this.entity.toString(e, d);
        return "boss bars of players " + this.players.toString(e, d);
    }

}
