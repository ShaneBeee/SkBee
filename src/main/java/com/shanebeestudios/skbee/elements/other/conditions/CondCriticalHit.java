package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Is Critical Hit")
@Description("Check if a hit was a critical hit in a damage event. Requires a PaperMC server.")
@Examples({"on damage:",
        "\tif attacker is a player:",
        "\t\tif hit is critical:",
        "\t\t\tcancel event"})
@Since("INSERT VERSION")
public class CondCriticalHit extends Condition {

    static {
        Skript.registerCondition(CondCriticalHit.class, "hit (is|1:(isn't|is not)) [a] critical");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!Skript.methodExists(EntityDamageByEntityEvent.class, "isCritical")) {
            Skript.error("'his is critical' requires a PaperMC server.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        if (!ParserInstance.get().isCurrentEvent(EntityDamageByEntityEvent.class)) {
            Skript.error("'hit is critical' is only available in an entity damage event!", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        setNegated(parseResult.hasTag("1"));
        return true;
    }

    @SuppressWarnings("NullableProblems")
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
