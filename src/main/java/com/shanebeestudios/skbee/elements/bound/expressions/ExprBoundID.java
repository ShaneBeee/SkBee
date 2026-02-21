package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.bound.BoundConfig;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprBoundID extends SimplePropertyExpression<Bound, String> {

    private static final BoundConfig BOUND_CONFIG = SkBee.getPlugin().getBoundConfig();

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprBoundID.class, String.class,
                "[bound] id", "bounds")
            .name("Bound - ID")
            .description("Get/set the id of a bound. When setting the ID of a bound, if another bound has that ID, this will fail with an error in console.",
                "You cannot set the IDs of multiple bounds at once.")
            .examples("set {_id} to id of first element of bounds at player",
                "loop all bounds at player:",
                "\tset id of loop-bound to \"%player%-%id of loop-bound%\"",
                "set {_id} to id of event-bound",
                "send \"You entered bound '%id of loop-bound%'\"")
            .since("1.15.0")
            .register();
    }

    @Nullable
    @Override
    public String convert(Bound bound) {
        return bound.getId();
    }

    @SuppressWarnings("NullableProblems")
    @Nullable
    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        if (!getExpr().isSingle()) {
            Skript.error("Can't set the id of multiple bounds at once!");
            return null;
        }
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(String.class);
        }
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta == null || !getExpr().isSingle()) return;

        String id = ((String) delta[0]);
        Bound bound = getExpr().getSingle(event);
        if (bound == null) return;

        if (BOUND_CONFIG.boundExists(id)) {
            // We don't want to rename a bound if the name already exists
            Util.skriptError("Bound with ID '%s' already exists, you can not rename bound with id '%s' to that.", id, bound.getId());
            return;
        }
        BOUND_CONFIG.removeBound(bound);
        bound.setId(id);
        BOUND_CONFIG.saveBound(bound, false);

    }

    @Override
    protected @NotNull String getPropertyName() {
        return "bound id";
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

}
