package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.config.BoundConfig;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Bound - Locations")
@Description({"Get/modify the locations of a bound.",
    "Greater will always equal the higher south-east corner. ",
    "Lesser will always equal the lower north-west corner.",
    "You can set the two corners of the bound to new values.",
    "You can also add/subtract vectors to/from the corners."})
@Examples({"set {_center} to bound center of bound with id \"spawn-bound\"",
    "set block at bound greater corner of {_bound} to pink wool",
    "set bound lesser corner of {_bound} to location of player",
    "set bound greater corner of bound with id \"ma_bound\" to {_loc}",
    "add vector(5,5,5) to bound greater corner of {_bound}",
    "subtract vector(0,10,0) from bound greater corner of {_bound}"})
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

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(@NotNull ChangeMode mode) {
        if (type == 0) {
            Skript.error("You cannot modify the center of a bound. You can modify the lesser/greater corners.");
            return null;
        }
        return switch (mode) {
            case SET -> CollectionUtils.array(Location.class);
            case ADD, REMOVE -> CollectionUtils.array(Vector.class);
            default -> null;
        };
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        BoundConfig boundConfig = SkBee.getPlugin().getBoundConfig();
        if (delta == null) return;

        if (mode == ChangeMode.SET) {
            if (delta[0] instanceof Location location) {
                for (Bound bound : getExpr().getArray(event)) {
                    Location less = this.type == 2 ? location : bound.getLesserCorner();
                    Location great = this.type == 1 ? location : bound.getGreaterCorner();
                    bound.resize(less, great);
                    boundConfig.saveBound(bound);
                }
            }
        } else {
            if (delta[0] instanceof Vector vector) {
                for (Bound bound : getExpr().getArray(event)) {
                    Location less = bound.getLesserCorner();
                    Location great = bound.getGreaterCorner().subtract(1,1,1);
                    if (mode == ChangeMode.ADD) {
                        if (this.type == 1) great.add(vector);
                        else less.add(vector);
                    } else {
                        if (this.type == 1) great.subtract(vector);
                        else less.subtract(vector);
                    }
                    bound.resize(less, great);
                    boundConfig.saveBound(bound);
                }
            }
        }
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
