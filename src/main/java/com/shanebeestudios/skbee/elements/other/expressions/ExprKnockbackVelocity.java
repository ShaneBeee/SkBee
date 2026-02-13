package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.EventRestrictedSyntax;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import io.papermc.paper.event.entity.EntityKnockbackEvent;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprKnockbackVelocity extends SimpleExpression<Vector> implements EventRestrictedSyntax {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprKnockbackVelocity.class, Vector.class, "simple",
                        "[the] knockback velocity")
                .name("Knockback Velocity")
                .description("The knockback velocity in an entity knockback event.")
                .examples("on entity knockback:",
                        "\tset knockback velocity to knockback velocity * -1")
                .since("3.16.0")
                .register();
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, @NotNull ParseResult parseResult) {
        return true;
    }

    @Override
    protected Vector[] get(@NotNull Event event) {
        if (!(event instanceof EntityKnockbackEvent knockbackEvent)) return new Vector[0];
        return new Vector[]{knockbackEvent.getKnockback()};
    }

    @Override
    public Class<? extends Event>[] supportedEvents() {
        //noinspection unchecked
        return new Class[]{EntityKnockbackEvent.class};
    }

    @Override
    public @NotNull Class<? extends Vector> getReturnType() {
        return Vector.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case ADD, REMOVE, SET -> CollectionUtils.array(Vector.class);
            default -> null;
        };
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        if (!(event instanceof EntityKnockbackEvent knockbackEvent)) return;
        if (acceptChange(mode) == null) return;
        assert delta != null && delta[0] instanceof Vector;
        Vector changeValue = (Vector) delta[0];
        Vector knockbackVector = knockbackEvent.getKnockback();

        knockbackVector = switch (mode) {
            case ADD -> knockbackVector.add(changeValue);
            case REMOVE -> knockbackVector.add(changeValue.multiply(-1));
            case SET -> changeValue;
            default -> knockbackVector;
        };

        knockbackEvent.setKnockback(knockbackVector);
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "knockback velocity";
    }

}
