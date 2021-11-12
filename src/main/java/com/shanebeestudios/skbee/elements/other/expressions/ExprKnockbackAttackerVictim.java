package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Knockback Attacker/Victim")
@Description("The attacker/victim in an entity knockback event. Paper 1.12.2+")
@Examples({"on entity knockback:", "\tif knockback attacker is a player:", "\t\tif knockback victim is a sheep:", "\t\t\tcancel event"})
@Since("1.8.0")
public class ExprKnockbackAttackerVictim extends SimpleExpression<Entity> {

    static {
        Skript.registerExpression(ExprKnockbackAttackerVictim.class, Entity.class, ExpressionType.SIMPLE,
                "[the] knockback (0¦attacker|1¦victim)");
    }

    @SuppressWarnings("null")
    private Integer ent;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, @NotNull ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(EntityKnockbackByEntityEvent.class)) {
            Skript.error("Can not use 'knockback attacker/victim' outside of knockback event", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        ent = parseResult.mark;
        return true;
    }

    @Override
    protected Entity[] get(@NotNull Event e) {
        if (ent == 1) {
            return new Entity[]{getVictim(e)};
        } else {
            return new Entity[]{getAttacker(e)};
        }
    }

    @Override
    public @NotNull Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Nullable
    private static Entity getAttacker(final @Nullable Event e) {
        if (e instanceof EntityKnockbackByEntityEvent) {
            return ((EntityKnockbackByEntityEvent) e).getHitBy();
        }
        return null;
    }

    @Nullable
    private static Entity getVictim(final @Nullable Event e) {
        if (e instanceof EntityKnockbackByEntityEvent) {
            return ((EntityKnockbackByEntityEvent) e).getEntity();
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return String.format("knockback %s", ent == 1 ? "victim" : "attacker");
    }

}
