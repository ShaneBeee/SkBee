package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprShootBowProjectile extends SimpleExpression<Entity> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprShootBowProjectile.class, Entity.class, "projectile entity")
                .name("Entity Shoot Bow - Projectile")
                .description("Get/set the projectile which will be launched in an entity shoot bow event.",
                        "\nNOTE: Setting doesn't appear to do anything, server bug I guess?!?!")
                .examples("on entity shoot bow:",
                        "\tmake player ride projectile entity")
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
    protected @Nullable Entity[] get(Event event) {
        if (event instanceof EntityShootBowEvent shootBowEvent) return new Entity[]{shootBowEvent.getProjectile()};
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Entity.class);
        return null;
    }

    @SuppressWarnings({"ConstantValue", "NullableProblems"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (!(event instanceof EntityShootBowEvent shootBowEvent)) return;
        if (mode != ChangeMode.SET) return; // this shouldn't happen
        if (delta != null && delta[0] instanceof Entity projectile) {
            shootBowEvent.setProjectile(projectile);
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "projectile entity";
    }

}
