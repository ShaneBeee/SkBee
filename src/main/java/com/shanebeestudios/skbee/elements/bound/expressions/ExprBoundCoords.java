package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.bound.BoundConfig;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class ExprBoundCoords extends PropertyExpression<Bound, Object> {

    private static final BoundConfig BOUND_CONFIG = SkBee.getPlugin().getBoundConfig();

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprBoundCoords.class, Object.class, "bound",
                "lesser (x|1:y|2:z) coord[inate]",
                "greater (x|1:y|2:z) coord[inate]",
                "world of bound")
            .name("Bound - Coords")
            .description("DEPRECATED - Use bound locations/world expressions instead",
                "The coords and world of a bounding box. You can get the world/coords for a specific bound, you can also " +
                    "set the coords of a bounding box. You can NOT set the world of a bounding box. ",
                "Greater will always equal the higher south-east corner. ",
                "Lesser will always equal the lower north-west corner.")
            .examples("set lesser y coord of {bound} to 10", "set {_x} to greater x coord of bound with id \"my.bound\"")
            .since("1.0.0")
            .register();
    }

    private boolean WORLD;
    private boolean LESSER;
    private int parse;

    @SuppressWarnings({"unchecked", "null", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        setExpr((Expression<Bound>) exprs[0]);
        this.WORLD = matchedPattern == 2;
        this.LESSER = matchedPattern == 0;
        this.parse = parseResult.mark;
        if (matchedPattern == 2) {
            Skript.warning("Depreacted - use the 'bound world of %bounds%' expression instead.");
        } else {
            Skript.warning("Deprecated - use the 'Bound - Locations' expression instead.");
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Object[] get(Event event, Bound[] bounds) {
        return get(bounds, bound -> {
            if (WORLD) {
                return bound.getWorld();
            } else {
                return switch (parse) {
                    case 0 -> LESSER ? bound.getLesserCorner().getX() : bound.getGreaterCorner().getX();
                    case 1 -> LESSER ? bound.getLesserCorner().getY() : bound.getGreaterCorner().getY();
                    default -> LESSER ? bound.getLesserCorner().getZ() : bound.getGreaterCorner().getZ();
                };
            }
        });
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (!WORLD && (mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.REMOVE)) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event e, Object[] delta, ChangeMode mode) {
        for (Bound bound : getExpr().getArray(e)) {
            int coord = ((Number) delta[0]).intValue();
            if (mode == ChangeMode.REMOVE) coord = -coord;

            Location less = bound.getLesserCorner();
            Location great = bound.getGreaterCorner().subtract(1, 1, 1);
            switch (this.parse) {
                case 0 -> {
                    if (LESSER) less.setX(mode == ChangeMode.SET ? coord : less.getX() + coord);
                    else great.setX(mode == ChangeMode.SET ? coord : great.getX() + coord);
                }
                case 1 -> {
                    if (LESSER) less.setY(mode == ChangeMode.SET ? coord : less.getY() + coord);
                    else great.setY(mode == ChangeMode.SET ? coord : great.getY() + coord);
                }
                case 2 -> {
                    if (LESSER) less.setZ(mode == ChangeMode.SET ? coord : less.getZ() + coord);
                    else great.setZ(mode == ChangeMode.SET ? coord : great.getZ() + coord);
                }
            }

            bound.resize(less, great);
            BOUND_CONFIG.saveBound(bound, true);
        }
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        if (WORLD) {
            return World.class;
        } else {
            return Integer.class;
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String type = "";
        String lesser = "";
        if (WORLD) {
            type = "world";
        } else {
            if (LESSER) lesser = "lesser ";
            if (parse == 0) type = "x coord";
            else if (parse == 1) type = "y coord";
            else if (parse == 2) type = "z coord";
        }
        return lesser + type + " of bound " + getExpr().toString(e, d);
    }

}
