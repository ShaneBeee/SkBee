package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.bound.Bound;
import org.bukkit.Location;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Bound - Locations")
@Description("Get the greater/lesser locations of this bound.")
@Examples("set {_loc} to greater location of bound with id \"le-bound\"")
@Since("INSERT VERSION")
public class ExprBoundLocations extends SimplePropertyExpression<Bound, Location> {

    static {
        register(ExprBoundLocations.class, Location.class, "(:lesser|greater) location", "bounds");
    }

    private boolean lesser;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.lesser = parseResult.hasTag("lesser");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Location convert(Bound bound) {
        return this.lesser ? bound.getLesserCorner() : bound.getGreaterCorner();
    }

    @Override
    public @NotNull Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return (this.lesser ? "lesser" : "greater") + " location";
    }

}
