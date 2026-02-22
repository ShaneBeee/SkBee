package com.shanebeestudios.skbee.elements.damagesource.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.damage.DamageSource;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprDamageSourceEvent extends SimpleExpression<DamageSource> {

    private static final boolean DEATH_EVENT_HAS_DAMAGE_SOURCE = Skript.methodExists(EntityDeathEvent.class, "getDamageSource");

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprDamageSourceEvent.class, DamageSource.class,
                "[the] damage source")
            .name("DamageSource - Event Value")
            .description("Get the damage source of a damage/death event.")
            .examples("on damage of player:",
                "\tif damage type of damage source = arrow:",
                "\t\tbroadcast \"OUCHIE\"",
                "\tif causing entity of damage source is a chicken:",
                "\t\tbroadcast \"YOU JERK\"")
            .since("3.3.0")
            .register();
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (DEATH_EVENT_HAS_DAMAGE_SOURCE) {
            if (ParserInstance.get().isCurrentEvent(EntityDamageEvent.class, EntityDeathEvent.class)) return true;
            Skript.error("'damage source' can only be used in a death/damage event");
            return false;
        }
        if (ParserInstance.get().isCurrentEvent(EntityDamageEvent.class)) return true;
        Skript.error("'damage source' can only be used in a damage event");
        return false;
    }

    @Override
    protected DamageSource @Nullable [] get(Event event) {
        if (event instanceof EntityDamageEvent entityDamageEvent) {
            return new DamageSource[]{entityDamageEvent.getDamageSource()};
        } else if (DEATH_EVENT_HAS_DAMAGE_SOURCE && event instanceof EntityDeathEvent entityDeathEvent) {
            return new DamageSource[]{entityDeathEvent.getDamageSource()};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends DamageSource> getReturnType() {
        return DamageSource.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "damage source";
    }

}
