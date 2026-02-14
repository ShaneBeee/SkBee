package com.shanebeestudios.skbee.elements.scoreboard.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprObjName extends SimpleExpression<Object> {

    private static final Class<?>[] RETURN_CLASSES = CollectionUtils.array(String.class, ComponentWrapper.class);

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprObjName.class, Object.class,
                "objective (name|id) of %objective%",
                "objective [:component] display name of %objective%")
            .name("Scoreboard - Objective Name")
            .description("Represents the name/display name of an objective.",
                "- `name` = The name/id given to the objective (Cannot be changed).",
                "- `display name` = The name the players will see [as a string] (Can be changed).",
                "- `component display name` = The name the players will see [as a text component] (Can be changed).")
            .examples("set objective display name of {_objective} to \"le-objective\"")
            .since("2.6.0")
            .register();
    }

    private Expression<Objective> objective;
    private boolean display;
    private boolean component;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.objective = (Expression<Objective>) exprs[0];
        this.display = matchedPattern == 1;
        this.component = parseResult.hasTag("component");
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected @Nullable Object[] get(Event event) {
        Objective objective = this.objective.getSingle(event);
        if (objective == null) {
            return null;
        }
        if (this.display) {
            if (this.component) return new ComponentWrapper[]{ComponentWrapper.fromComponent(objective.displayName())};
            return new String[]{objective.getDisplayName()};
        }
        return new Object[]{objective.getName()};
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            if (!this.display) {
                Skript.error("Cannot change the name of an objective.");
                return null;
            }
            return RETURN_CLASSES;
        }
        return null;
    }

    @SuppressWarnings({"deprecation", "DataFlowIssue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Objective objective = this.objective.getSingle(event);
        if (delta[0] instanceof String name && mode == ChangeMode.SET && objective != null) {
            objective.setDisplayName(name);
        }

        if (delta != null) {
            if (delta[0] instanceof String string) {
                objective.setDisplayName(string);
            } else if (delta[0] instanceof ComponentWrapper cw) {
                objective.displayName(cw.getComponent());
            }
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return this.component ? ComponentWrapper.class : String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String type = this.display ? "display name" : "name";
        return "objective " + type + " of " + this.objective.toString(e, d);
    }

}
