package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprBoundFullState extends SimplePropertyExpression<Bound, Boolean> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprBoundFullState.class, Boolean.class, "bounds",
                "bound full state")
            .name("Bound - Full State")
            .description("Get/set whether this bound is a full bound (reaches from lowest to highest points of a world).")
            .examples("set bound full state of bound with id \"home\" to true")
            .since("3.8.0")
            .register();
    }

    @Override
    @Nullable
    public Boolean convert(Bound bound) {
        return bound.isFull();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    @Nullable
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Boolean.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (mode == ChangeMode.SET && delta != null && delta[0] instanceof Boolean full) {
            for (Bound bound : getExpr().getArray(event)) {
                bound.setFull(full);
            }
        }
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "full state";
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

}
