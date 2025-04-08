package com.shanebeestudios.skbee.elements.other.expressions;

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
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("World Time")
@Description({"Get/set the time of world, represented as an integer.",
    "`world time` = The 24000 tick day cycle of a world.",
    "`full world time` = The time of a world over all days."})
@Examples({"set {_time} to world time of world of player",
    "set {_time} to full world time of world of player",
    "set time of world of player to 12000",
    "set full time of world of player to 1000000",
    "add 1000 to world time of world of player",
    "add 1000 to full world time of world of player",
    "remove 100 from world time of world of player",
    "remove 50 from full world time of world of player"})
@Since("3.11.0")
public class ExprWorldTime extends SimplePropertyExpression<World, Long> {

    static {
        register(ExprWorldTime.class, Long.class,
            "[:full] world time", "worlds");
    }

    private boolean full;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.full = parseResult.hasTag("full");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Long convert(World from) {
        if (this.full) return from.getFullTime();
        return from.getTime();
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, ADD, REMOVE -> CollectionUtils.array(Long.class);
            default -> null;
        };
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        long time = delta != null && delta[0] instanceof Long l ? l : 0;
        for (World world : getExpr().getArray(event)) {
            long changeValue = this.full ? world.getFullTime() : world.getTime();
            if (mode == ChangeMode.ADD) {
                changeValue += time;
            } else if (mode == ChangeMode.REMOVE) {
                changeValue -= time;
            } else {
                changeValue = time;
            }
            if (this.full) world.setFullTime(changeValue);
            else world.setTime(changeValue);
        }
    }

    @Override
    protected String getPropertyName() {
        return this.full ? "full world time" : "world time";
    }

    @Override
    public Class<? extends Long> getReturnType() {
        return Long.class;
    }

}
