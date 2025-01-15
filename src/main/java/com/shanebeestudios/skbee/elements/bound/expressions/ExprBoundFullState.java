package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.bound.Bound;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Bound - Full State")
@Description("Get/set whether this bound is a full bound (reaches from lowest to highest points of a world).")
@Examples("set bound full state of bound with id \"home\" to true")
@Since("3.8.0")
public class ExprBoundFullState extends SimplePropertyExpression<Bound, Boolean> {

    static {
        register(ExprBoundFullState.class, Boolean.class, "bound full state", "bounds");
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
