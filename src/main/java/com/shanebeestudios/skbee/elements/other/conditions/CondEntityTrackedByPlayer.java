package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.Condition;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class CondEntityTrackedByPlayer extends Condition {

    public static void register(Registration reg) {
        reg.newCondition(CondEntityTrackedByPlayer.class,
                "%entities% (is|are) tracked by %players%",
                "%entities% (isn't|are not) tracked by %players%")
            .name("Entity Tracked By Player")
            .description("Check if entities are tracked by players.")
            .examples("if {_e} is tracked by player:",
                "if {_e::*} is not tracked by {_p::*}:")
            .since("INSERT VERSION")
            .register();
    }

    private Expression<Entity> entities;
    private Expression<Player> players;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.entities = (Expression<Entity>) expressions[0];
        this.players = (Expression<Player>) expressions[1];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        return this.players.check(event, player ->
                this.entities.check(event, entity ->
                    entity.isTrackedBy(player)),
            isNegated());
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String neg;
        if (this.entities.isSingle()) {
            neg = isNegated() ? "is not" : "is";
        } else {
            neg = isNegated() ? "are not" : "are";
        }
        return this.entities.toString(event, debug) + neg + " tracked by " + this.players.toString(event, debug);
    }

}
