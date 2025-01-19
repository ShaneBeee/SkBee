package com.shanebeestudios.skbee.elements.scoreboard.expressions;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Scoreboard - Objective Name")
@Description({"Represents the name/display name of an objective.",
    "- `name` = The name/id given to the objective (Cannot be changed).",
    "- `display name` = The name the players will see (Can be changed)."})
@Examples("set objective display name of {_objective} to \"le-objective\"")
@Since("2.6.0")
public class ExprObjName extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprObjName.class, String.class, ExpressionType.COMBINED,
            "objective (name|id) of %objective%",
            "objective display name of %objective%");
    }

    private Expression<Objective> objective;
    private boolean display;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.objective = (Expression<Objective>) exprs[0];
        this.display = matchedPattern == 1;
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected @Nullable String[] get(Event event) {
        Objective objective = this.objective.getSingle(event);
        if (objective == null) return null;
        return new String[]{this.display ? objective.getDisplayName() : objective.getName()};
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            if (!this.display) {
                Skript.error("Cannot change the name of an objective.");
                return null;
            }
            return CollectionUtils.array(String.class);
        }
        return null;
    }

    @SuppressWarnings("deprecation")
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
        String type = this.display ? "display name" : "name";
        return "objective " + type + " of " + this.objective.toString(e, d);
    }

}
