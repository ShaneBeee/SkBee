package com.shanebeestudios.skbee.elements.objective.expressions;

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
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Scoreboard - Objective Name")
@Description("Get/Set the display name of an objective.")
@Examples("set objective display name of {_objective} to \"le-objective\"")
@Since("2.6.0")
public class ExprObjName extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprObjName.class, String.class, ExpressionType.COMBINED,
                "objective [display[ ]]name of %objective%");
    }

    private Expression<Objective> objective;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.objective = (Expression<Objective>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable String[] get(Event event) {
        Objective objective = this.objective.getSingle(event);
        if (objective == null) return null;
        return new String[]{objective.getName()};
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(String.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "deprecation"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Objective objective = this.objective.getSingle(event);
        if (delta[0] instanceof String name && mode == ChangeMode.SET && objective != null) {
            objective.setDisplayName(name);
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "objective display name of " + this.objective.toString(e, d);
    }

}
