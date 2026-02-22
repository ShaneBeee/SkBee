package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprShootBowForce extends SimpleExpression<Number> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprShootBowForce.class, Number.class, "shoot force", "shot force", "launch force", "bow force")
                .name("Entity Shoot Bow - Force")
                .description("Gets the force the arrow was launched with in an entity shoot bow event.",
                        "This is a number between 0 and 1.")
                .examples("on entity shoot bow:",
                        "\tif shot force < 0.5:",
                        "\t\tcancel event")
                .since("2.16.0")
                .register();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(EntityShootBowEvent.class)) {
            Skript.error("'" + parseResult.expr + "' can only be used in the entity shoot bow event.");
            return false;
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Number[] get(Event event) {
        if (event instanceof EntityShootBowEvent entityShootBowEvent)
            return new Number[]{entityShootBowEvent.getForce()};
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "shoot force";
    }

}
