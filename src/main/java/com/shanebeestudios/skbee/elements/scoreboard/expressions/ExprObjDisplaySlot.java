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
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Scoreboard - Objective DisplaySlot")
@Description("Get/Set the display slot of an objective.")
@Examples("set display slot of {_objective} to player_list")
@Since("2.6.0")
public class ExprObjDisplaySlot extends SimpleExpression<DisplaySlot> {

    static {
        Skript.registerExpression(ExprObjDisplaySlot.class, DisplaySlot.class, ExpressionType.COMBINED,
            "[objective] display[ ]slot of %objective%");
    }

    private Expression<Objective> objective;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.objective = (Expression<Objective>) exprs[0];
        return true;
    }

    @Override
    protected @Nullable DisplaySlot[] get(Event event) {
        Objective objective = this.objective.getSingle(event);
        if (objective == null) {
            error("Objective is not set: " + this.objective.toString(event, true));
            return null;
        }
        return new DisplaySlot[]{objective.getDisplaySlot()};
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(DisplaySlot.class);
        else if (mode == ChangeMode.DELETE) return CollectionUtils.array();
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Objective objective = this.objective.getSingle(event);
        if (objective == null) {
            error("Objective is not set: " + this.objective.toString(event, true));
            return;
        }
        if (mode == ChangeMode.DELETE) {
            objective.setDisplaySlot(null);
        } else if (mode == ChangeMode.SET) {
            if (delta == null) {
                error("Change value is not set");
                return;
            }
            if (delta[0] instanceof DisplaySlot displaySlot) {
                objective.setDisplaySlot(displaySlot);
            } else {
                error("Invalid object for display slot: " + Classes.toString(delta[0]));
            }
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends DisplaySlot> getReturnType() {
        return DisplaySlot.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "objective display slot of " + this.objective.toString(e, d);
    }

}
