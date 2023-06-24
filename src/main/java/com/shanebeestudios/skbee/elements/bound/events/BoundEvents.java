package com.shanebeestudios.skbee.elements.bound.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.event.bound.BoundEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.shanebeestudios.skbee.api.event.bound.BoundEnterEvent;
import com.shanebeestudios.skbee.api.event.bound.BoundExitEvent;

@SuppressWarnings("unused")
public class BoundEvents extends SkriptEvent {

    static {

        EventValues.registerEventValue(BoundEvent.class, Bound.class, new Getter<>() {
            @Override
            public @Nullable Bound get(BoundEvent event) {
                return event.getBound();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(BoundEvent.class, String.class, new Getter<>() {
            @Override
            public @Nullable String get(BoundEvent event) {
                return event.getBound().getId();
            }
        }, EventValues.TIME_NOW);

        Skript.registerEvent("Bound - Enter", BoundEvents.class, BoundEnterEvent.class, "(bound enter|enter bound) [with id %-string%]")
                .description("Called when a player enters a bound. Optional ID of bound. 'event-string' = bound ID.")
                .examples("on bound enter:",
                        "\tif event-bound = {bounds::spawn}:",
                        "\t\tsend \"You entered spawn!\"",
                        "on enter bound with id \"spawn\":",
                        "\tcancel event")
                .since("1.0.0, 1.12.2 (Bound IDs)");

        EventValues.registerEventValue(BoundEnterEvent.class, Player.class, new Getter<>() {
            @Override
            public Player get(BoundEnterEvent event) {
                return event.getPlayer();
            }
        }, 0);

        Skript.registerEvent("Bound - Exit", BoundEvents.class, BoundExitEvent.class, "(bound exit|exit bound) [with id %-string%]")
                .description("Called when a player exits a bound. Optional ID of bound. 'event-string' = bound ID.")
                .examples("on bound exit:",
                        "\tsend \"You left a bound\"",
                        "\tif event-bound = {bound}:",
                        "\t\tsend \"You left Spawn!\"",
                        "on exit bound with id \"spawn\":",
                        "\tcancel event")
                .since("1.0.0, 1.12.2 (Bound IDs)");

        EventValues.registerEventValue(BoundExitEvent.class, Player.class, new Getter<>() {
            @Override
            public Player get(BoundExitEvent event) {
                return event.getPlayer();
            }
        }, 0);

    }

    private Literal<String> boundID;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        boundID = (Literal<String>) args[0];
        return true;
    }

    @Override
    public boolean check(Event event) {
        if (this.boundID == null) {
            return true;
        }
        return this.boundID.check(event, boundID -> {
            if (event instanceof BoundEvent boundEvent) {
                return boundEvent.getBound().getId().equals(boundID);
            }
            return false;
        });
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "bound enter/exit" + (this.boundID != null ? " with id " + this.boundID.toString(event, debug) : "");
    }

}
