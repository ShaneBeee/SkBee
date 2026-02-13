package com.shanebeestudios.skbee.elements.scoreboard.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprObjDisplaySlot extends SimpleExpression<DisplaySlot> {

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprObjDisplaySlot.class, DisplaySlot.class,
                "[objective] display[ ]slot of %objective%")
            .name("Scoreboard - Objective DisplaySlot")
            .description("Get/Set the display slot of an objective.")
            .examples("set display slot of {_objective} to player_list")
            .since("2.6.0")
            .register();
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
            return;
        }
        if (mode == ChangeMode.DELETE) {
            objective.setDisplaySlot(null);
        } else if (mode == ChangeMode.SET) {
            if (delta == null) {
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
