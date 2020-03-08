package tk.shanebee.bee.elements.bound.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Getter;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.World;
import org.bukkit.event.Event;
import tk.shanebee.bee.elements.bound.objects.Bound;

@Name("Bound - Coords")
@Description({"The coords and world of a bounding box. You can get the world/coords for a specific bound, you can also " +
        "set the coords of a bounding box. You can NOT set the world of a bounding box. ",
        "\nLesser will always equal the lower south-east corner. ", "Greater will always equal the higher north-west corner."})
@Examples({"set lesser y coord of {bound} to 10", "set {_x} to greater x coord of bound with id \"my.bound\""})
@Since("1.0.0")
public class ExprBoundCoords extends PropertyExpression<Bound, Object> {

    static {
        Skript.registerExpression(ExprBoundCoords.class, Object.class, ExpressionType.PROPERTY,
                "lesser (0¦x|1¦y|2¦z) coord[inate] of [bound] %bound%",
                "greater (0¦x|1¦y|2¦z) coord[inate] of [bound] %bound%",
                "world of bound %bound%");
    }

    private boolean WORLD;
    private boolean LESSER;
    private int parse;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<Bound>) exprs[0]);
        this.WORLD = matchedPattern == 2;
        this.LESSER = matchedPattern == 0;
        this.parse = parseResult.mark;
        return true;
    }

    @Override
    protected Object[] get(Event event, Bound[] bounds) {
        return get(bounds, new Getter<Object, Bound>() {
            @Override
            public Object get(Bound bound) {
                if (WORLD) {
                    return bound.getWorld();
                } else {
                    switch (parse) {
                        case 0:
                            return LESSER ? bound.getLesserX() : bound.getGreaterX();
                        case 1:
                            return LESSER ? bound.getLesserY() : bound.getGreaterY();
                        default:
                            return LESSER ? bound.getLesserZ() : bound.getGreaterZ();
                    }
                }
            }
        });
    }

    @Override
    public Class<?> getReturnType() {
        if (WORLD) {
            return World.class;
        } else  {
            return Integer.class;
        }
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (!WORLD && mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }

    @Override
    public void change(Event e, Object[] delta, Changer.ChangeMode mode) {
        for (Bound bound : getExpr().getArray(e)) {
            final int coord = ((Number) delta[0]).intValue();
            switch (parse) {
                case 0:
                    if (LESSER) bound.setLesserX(coord);
                    else bound.setGreaterX(coord);
                    break;
                case 1:
                    if (LESSER) bound.setLesserY(coord);
                    else bound.setGreaterY(coord);
                    break;
                case 2:
                    if (LESSER) bound.setLesserZ(coord);
                    else bound.setGreaterZ(coord);
                    break;
            }
        }
    }

    @Override
    public String toString(Event e, boolean d) {
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
