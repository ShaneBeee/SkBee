package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.lang.EventRestrictedSyntax;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import io.papermc.paper.event.entity.EntityKnockbackEvent;
import io.papermc.paper.event.entity.EntityPushedByEntityAttackEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class ExprKnockbackAttackerVictim extends SimpleExpression<Entity> implements EventRestrictedSyntax {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprKnockbackAttackerVictim.class, Entity.class, "simple",
                        "[the] knockback (:attacker|victim)")
                .name("Knockback Attacker/Victim")
                .description("The attacker/victim in an entity knockback event.")
                .examples("on entity knockback:",
                        "\tif knockback attacker is a player:",
                        "\t\tif knockback victim is a sheep:",
                        "\t\t\tcancel event")
                .since("1.8.0")
                .register();
    }

    private boolean useAttacker;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, @NotNull ParseResult parseResult) {
        this.useAttacker = parseResult.hasTag("attacker");
        return true;
    }

    @Override
    protected Entity[] get(@NotNull Event event) {
        if (!(event instanceof EntityKnockbackEvent knockbackEvent)) return new Entity[0];
        if (!this.useAttacker) return new Entity[]{knockbackEvent.getEntity()};
        if (!(event instanceof EntityPushedByEntityAttackEvent pushedByEntityAttackEvent)) return new Entity[0];
        return new Entity[]{pushedByEntityAttackEvent.getPushedBy()};
    }

    @Override
    public Class<? extends Event>[] supportedEvents() {
        return new Class[]{EntityKnockbackEvent.class};
    }

    @Override
    public @NotNull Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return String.format("knockback %s", this.useAttacker ? "attacker" : "victim");
    }

}
