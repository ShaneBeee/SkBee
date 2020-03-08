package tk.shanebee.bee.elements.bound.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.elements.bound.objects.Bound;

@Name("Bound - From ID")
@Description("Get a bound object from a bound ID")
@Examples("set {_b} to bound from id \"%player%.home\"")
@Since("1.0.0")
public class ExprBoundFromID extends SimpleExpression<Bound> {

    static {
        Skript.registerExpression(ExprBoundFromID.class, Bound.class, ExpressionType.SIMPLE,
                "bound (of|from|with) id %string%");
    }

    private Expression<String> id;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        this.id = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected Bound[] get(Event event) {
        String id = this.id.getSingle(event);
        return new Bound[]{SkBee.getPlugin().getBoundConfig().getBoundFromID(id)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Bound> getReturnType() {
        return Bound.class;
    }

    @Override
    public String toString(Event e, boolean d) {
        return "bound from id " + this.id.toString(e, d);
    }

}
