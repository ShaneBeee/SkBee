package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.LiteralList;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.EventValues;
import com.shanebeestudios.skbee.api.registration.Registration;
import io.papermc.paper.event.entity.EntityKnockbackEvent;
import io.papermc.paper.event.entity.EntityPushedByEntityAttackEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EvtEntityKnockback extends SkriptEvent {

    public static void register(Registration reg) {
        //noinspection unchecked
        reg.newEvent(EvtEntityKnockback.class, new Class[]{EntityKnockbackEvent.class},
                "%entitydatas% knockback[ed] [by %-entitydatas%]")
            .name("Entity Knockback")
            .description("Fired when an Entity is knocked back by the hit of another Entity. " +
                "If this event is cancelled, the entity is not knocked back.")
            .examples("on entity knockback:", "\tif knockback victim is a cow:", "\t\tcancel event")
            .since("1.8.0")
            .register();

        EventValues.registerEventValue(EntityKnockbackEvent.class, EntityKnockbackEvent.Cause.class, EntityKnockbackEvent::getCause);
        EventValues.registerEventValue(EntityKnockbackEvent.class, Entity.class, EntityKnockbackEvent::getEntity,
            EventValues.TIME_NOW, "There may be multiple entities in an entity knockback event use knockback attacker/victim expression.", EntityKnockbackEvent.class);
    }

    private Literal<EntityData<?>> victims;
    private @Nullable Literal<EntityData<?>> attackers;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        this.victims = (Literal<EntityData<?>>) args[0];
        this.attackers = (Literal<EntityData<?>>) args[1];
        if (victims instanceof LiteralList<EntityData<?>> literalList && !victims.getAnd()) literalList.invertAnd();
        if (attackers instanceof LiteralList<EntityData<?>> literalList && !attackers.getAnd()) literalList.invertAnd();
        return true;
    }

    @Override
    public boolean check(Event event) {
        if (!(event instanceof EntityKnockbackEvent knockbackEvent)) return false;
        Entity pushedEntity = knockbackEvent.getEntity();
        EntityData<?>[] victims = this.victims.getArray(event);
        if (!SimpleExpression.check(victims, entitydata -> entitydata.isInstance(pushedEntity), false, false))
            return false;
        if (this.attackers != null && event instanceof EntityPushedByEntityAttackEvent pushedByEntityAttackEvent) {
            Entity pushedBy = pushedByEntityAttackEvent.getPushedBy();
            EntityData<?>[] attackers = this.attackers.getArray(event);
            return SimpleExpression.check(attackers, entityData -> entityData.isInstance(pushedBy), false, false);
        }
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        SyntaxStringBuilder syntaxStringBuilder = new SyntaxStringBuilder(event, debug);
        syntaxStringBuilder.append(this.victims).append("knockback");
        if (this.attackers != null)
            syntaxStringBuilder.append("by").append(this.attackers);
        return syntaxStringBuilder.toString();
    }
}
