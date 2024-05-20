package com.shanebeestudios.skbee.elements.bound.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.event.bound.BoundEnterEvent;
import com.shanebeestudios.skbee.api.event.bound.BoundEvent;
import com.shanebeestudios.skbee.api.event.bound.BoundExitEvent;
import com.shanebeestudios.skbee.api.listener.BoundBorderListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class BoundEvents extends SkriptEvent {

    private static BoundBorderListener boundBorderListener;

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
            .description("Called when a player enters a bound. Optional ID of bound. 'event-string' = bound ID.",
                "NOTE: Due to breaking changes in Bukkit API, enter/exit events will not be called when a player mounts/dismounts an entity if running SkBee 3.5.0+ on MC 1.20.4 and below.")
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
            .description("Called when a player exits a bound. Optional ID of bound. 'event-string' = bound ID.",
                "NOTE: Due to breaking changes in Bukkit API, enter/exit events will not be called when a player mounts/dismounts an entity if running SkBee 3.5.0+ on MC 1.20.4 and below.")
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
        if (boundBorderListener == null) {
            // Register listener when a bound event is actually used
            SkBee plugin = SkBee.getPlugin();
            boundBorderListener = new BoundBorderListener(plugin);
            Bukkit.getPluginManager().registerEvents(boundBorderListener, plugin);
        }
        return true;
    }

    @Override
    public boolean check(@NotNull Event event) {
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
