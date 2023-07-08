package com.shanebeestudios.skbee.elements.bound.expressions;

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
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.config.BoundConfig;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Bound - Last Created Bound")
@Description("Returns the last created bound.")
@Examples({"create a bound with id \"\" between {_pos1} and {_pos2}",
        "broadcast last created bound",
        "resize last created bound between {_pos1^2} and {_pos2^2}"})
@Since("INSERT VERSION")
public class ExprLastCreatedBound extends SimpleExpression<Bound> {

    private static final BoundConfig boundConfig = SkBee.getPlugin().getBoundConfig();

    static {
        Skript.registerExpression(ExprLastCreatedBound.class, Bound.class, ExpressionType.SIMPLE,
                "[the] last[ly] created bound");
    }

    public static Bound lastCreated = null;

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected @Nullable Bound[] get(Event event) {
        if (lastCreated == null) return null;
        if (!boundConfig.boundExists(lastCreated.getId())) {
            lastCreated = null;
            return null;
        }
        return new Bound[]{lastCreated};
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
    public String toString(@Nullable Event event, boolean b) {
        return "lastly created bound";
    }

}
