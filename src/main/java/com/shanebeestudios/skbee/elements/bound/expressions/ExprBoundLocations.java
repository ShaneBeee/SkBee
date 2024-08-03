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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Bound - Locations")
@Description({"Get the locations of a bound.",
    "Greater will always equal the higher south-east corner. ",
    "Lesser will always equal the lower north-west corner."})
@Examples({"set {_center} to bound center of bound with id \"spawn-bound\"",
    "set block at bound greater corner of {_bound} to pink wool"})
@Since("3.5.9")
public class ExprBoundLocations extends SimplePropertyExpression<Bound, Location> {

    static {
        register(ExprBoundLocations.class, Location.class,
            "bound (0:center|(1:greater|2:lesser) corner)", "bounds");
    }

    private int type;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.type = parseResult.mark;
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Location convert(Bound bound) {
        return switch (this.type) {
            case 1 -> bound.getGreaterCorner();
            case 2 -> bound.getLesserCorner();
            default -> bound.getCenter();
        };
    }

    @Override
    public @NotNull Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "bound " + switch (this.type) {
            case 1 -> "greater corner";
            case 2 -> "lesser corner";
            default -> "center";
        };
    }

}
