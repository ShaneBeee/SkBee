package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.bound.Bound;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Bound - Center Location")
@Description("Get the center location of a bound.")
@Examples("set {_center} to bound center of bound with id \"spawn-bound\"")
@Since("INSERT VERSION")
public class ExprBoundCenter extends SimplePropertyExpression<Bound, Location> {

    static {
        register(ExprBoundCenter.class, Location.class, "bound center", "bounds");
    }

    @Override
    public @Nullable Location convert(Bound bound) {
        return bound.getCenter();
    }

    @Override
    public @NotNull Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "bound center";
    }

}
