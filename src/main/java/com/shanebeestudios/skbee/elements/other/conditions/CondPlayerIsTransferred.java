package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import com.google.common.base.Predicate;
import com.shanebeestudios.skbee.api.skript.base.Condition;
import io.papermc.paper.connection.PlayerConnection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class CondPlayerIsTransferred extends Condition {

    public static void register(Registration reg) {
        reg.newCondition(CondPlayerIsTransferred.class,
                "%players/playerconnections% (is|are) transferred",
                "%players/playerconnections% (is|are)(n't| not) transferred")
            .name("Is Transferred")
            .description("Check if a player transferred servers.")
            .examples("on join:",
                "\tif player is transferred:",
                "\t\tkick player due to \"No Transfers Bruh!\"")
            .since("3.5.0")
            .register();

    }

    private Expression<?> players;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.players = expressions[0];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        if (event instanceof AsyncPlayerPreLoginEvent preLoginEvent) {
            if (isNegated()) {
                return !preLoginEvent.isTransferred();
            } else {
                return preLoginEvent.isTransferred();
            }
        }
        return this.players.check(event, (Predicate<Object>) input -> {
            if (input instanceof Player player) {
                return player.isTransferred();
            } else if (input instanceof PlayerConnection playerConnection) {
                return playerConnection.isTransferred();
            }
            return false;
        }, isNegated());
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String neg = isNegated() ? " is not " : " is ";
        return this.players.toString(event,debug) + neg + "transferred";
    }

}
