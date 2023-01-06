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

@Name("Bound - Map Marker Label")
@Description({"Represents the map marker label of a mapping plugin (currently only supports BlueMaps).",
        "If not set, will pull from the default value in the bound config."})
@Examples("set map marker label of bound with id \"test-bound\" to \"LeTestBound\"")
@Since("INSERT VERSION")
public class ExprBoundLabel extends SimplePropertyExpression<Bound, String> {

    static {
        register(ExprBoundLabel.class, String.class, "map marker label", "bounds");
    }

    @Override
    public @Nullable String convert(Bound bound) {
        return bound.getLabel();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(String.class);
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (mode != ChangeMode.SET) return;

        String label = ((String) delta[0]);
        for (Bound bound : getExpr().getArray(event)) {
            bound.setLabel(label);
        }
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "label";
    }

}
