package com.shanebeestudios.skbee.elements.scoreboard.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprObjGet extends SimpleExpression<Objective> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprObjGet.class, Objective.class,
                "objective (with id|from [id]) %string% [(from|of) %scoreboard%]")
            .name("Scoreboard - Objective Get")
            .description("Get an already registered objective.",
                "Optionally can input a scoreboard (will default to main scoreboard).")
            .examples("set {_obj} to objective with id \"le-objective\"",
                "set {_obj} to objective with id \"my_objective\" from player's scoreboard")
            .since("2.6.0")
            .register();
    }

    private Expression<String> id;
    private Expression<Scoreboard> scoreboard;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.id = (Expression<String>) exprs[0];
        this.scoreboard = (Expression<Scoreboard>) exprs[1];
        return true;
    }

    @Override
    protected @Nullable Objective[] get(Event event) {
        Scoreboard scoreboard = this.scoreboard.getSingle(event);
        if (scoreboard == null) {
            return null;
        }
        String id = this.id.getSingle(event);
        if (id != null) {
            return new Objective[]{scoreboard.getObjective(id)};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Objective> getReturnType() {
        return Objective.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String board = this.scoreboard != null ? " from scoreboard " + this.scoreboard.toString(e, true) : "";
        return "objective from id " + this.id.toString(e, d) + board;
    }

}
