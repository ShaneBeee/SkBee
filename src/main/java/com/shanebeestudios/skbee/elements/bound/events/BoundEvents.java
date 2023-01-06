package com.shanebeestudios.skbee.elements.bound.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.shanebeestudios.skbee.api.bound.Bound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.shanebeestudios.skbee.api.event.EnterBoundEvent;
import com.shanebeestudios.skbee.api.event.ExitBoundEvent;

@SuppressWarnings("unused")
public class BoundEvents extends SkriptEvent {

    static {
        Skript.registerEvent("Bound - Enter", BoundEvents.class, EnterBoundEvent.class, "(bound enter|enter bound) [with id %-string%]")
                .description("Called when a player enters a bound. Optional ID of bound. 'event-string' = bound ID.")
                .examples("on bound enter:",
                        "\tif event-bound = {bounds::spawn}:",
                        "\t\tsend \"You entered spawn!\"",
                        "on enter bound with id \"spawn\":",
                        "\tcancel event")
                .since("1.0.0, 1.12.2 (Bound IDs)");
        EventValues.registerEventValue(EnterBoundEvent.class, Player.class, new Getter<>() {
            @Override
            public Player get(EnterBoundEvent event) {
                return event.getPlayer();
            }
        }, 0);
        EventValues.registerEventValue(EnterBoundEvent.class, Bound.class, new Getter<>() {
            @Override
            public Bound get(EnterBoundEvent event) {
                return event.getBound();
            }
        }, 0);
        EventValues.registerEventValue(EnterBoundEvent.class, String.class, new Getter<>() {
            @Nullable
            @Override
            public String get(EnterBoundEvent event) {
                return event.getBound().getId();
            }
        }, 0);


        Skript.registerEvent("Bound - Exit", BoundEvents.class, ExitBoundEvent.class, "(bound exit|exit bound) [with id %-string%]")
                .description("Called when a player exits a bound. Optional ID of bound. 'event-string' = bound ID.")
                .examples("on bound exit:",
                        "\tsend \"You left a bound\"",
                        "\tif event-bound = {bound}:",
                        "\t\tsend \"You left Spawn!\"",
                        "on exit bound with id \"spawn\":",
                        "\tcancel event")
                .since("1.0.0, 1.12.2 (Bound IDs)");
        EventValues.registerEventValue(ExitBoundEvent.class, Player.class, new Getter<>() {
            @Override
            public Player get(ExitBoundEvent event) {
                return event.getPlayer();
            }
        }, 0);
        EventValues.registerEventValue(ExitBoundEvent.class, Bound.class, new Getter<>() {
            @Override
            public Bound get(ExitBoundEvent event) {
                return event.getBound();
            }
        }, 0);
        EventValues.registerEventValue(ExitBoundEvent.class, String.class, new Getter<>() {
            @Nullable
            @Override
            public String get(ExitBoundEvent event) {
                return event.getBound().getId();
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
            if (event instanceof EnterBoundEvent enterBoundEvent) {
                return enterBoundEvent.getBound().getId().equals(boundID);
            } else if (event instanceof ExitBoundEvent exitBoundEvent) {
                return exitBoundEvent.getBound().getId().equals(boundID);
            }
            return false;
        });
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "bound enter/exit" + (this.boundID != null ? " with id " + this.boundID.toString(e, d) : "");
    }

}
