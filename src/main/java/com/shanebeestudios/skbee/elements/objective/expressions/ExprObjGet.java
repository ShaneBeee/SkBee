package com.shanebeestudios.skbee.elements.objective.expressions;

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
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Objective;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Scoreboard - Objective Get")
@Description("Get an already registered objective.")
@Examples("set {_obj} to objective with id \"le-objective\"")
@Since("2.6.0")
public class ExprObjGet extends SimpleExpression<Objective> {

    static {
        Skript.registerExpression(ExprObjGet.class, Objective.class, ExpressionType.COMBINED,
                "objective (with id|from [id]) %string%");
    }

    private Expression<String> id;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.id = (Expression<String>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Objective[] get(Event event) {
        String id = this.id.getSingle(event);
        if (id != null) {
            return new Objective[]{Bukkit.getScoreboardManager().getMainScoreboard().getObjective(id)};
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
        return "objective from id " + this.id.toString(e,d);
    }

}
