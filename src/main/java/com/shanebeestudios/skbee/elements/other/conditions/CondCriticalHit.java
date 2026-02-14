package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CondCriticalHit extends Condition {

    public static void register(Registration reg) {
        reg.newCondition(CondCriticalHit.class, "hit (is|1:(isn't|is not)) [a] critical")
            .name("Is Critical Hit")
            .description("Check if a hit was a critical hit in a damage event. Requires a PaperMC server.")
            .examples("on damage:",
                "\tif attacker is a player:",
                "\t\tif hit is critical:",
                "\t\t\tcancel event")
            .since("2.8.3")
            .register();
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(EntityDamageEvent.class)) {
            Skript.error("'hit is critical' is only available in an entity damage event!", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        setNegated(parseResult.hasTag("1"));
        return true;
    }

    @Override
    public boolean check(Event event) {
        if (event instanceof EntityDamageByEntityEvent entityEvent) {
            return isNegated() != entityEvent.isCritical();
        }
        return false;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String neg = isNegated() ? "isn't" : "is";
        return "hit " + neg + " critical";
    }

}
