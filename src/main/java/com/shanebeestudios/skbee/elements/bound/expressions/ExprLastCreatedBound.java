package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.bound.BoundConfig;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprLastCreatedBound extends SimpleExpression<Bound> {

    private static final BoundConfig boundConfig = SkBee.getPlugin().getBoundConfig();

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprLastCreatedBound.class, Bound.class,
                "[the] last[ly] created bound")
            .name("Bound - Last Created Bound")
            .description("Returns the last created bound.")
            .examples("create a bound with id \"\" between {_pos1} and {_pos2}",
                "broadcast last created bound",
                "resize last created bound between {_pos1^2} and {_pos2^2}")
            .since("2.15.0")
            .register();
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
