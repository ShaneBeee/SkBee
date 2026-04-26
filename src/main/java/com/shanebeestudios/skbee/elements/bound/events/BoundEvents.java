package com.shanebeestudios.skbee.elements.bound.events;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.event.bound.BoundEnterEvent;
import com.shanebeestudios.skbee.api.event.bound.BoundEvent;
import com.shanebeestudios.skbee.api.event.bound.BoundExitEvent;
import com.shanebeestudios.skbee.api.listener.BoundBorderListener;
import com.shanebeestudios.skbee.api.listener.BoundBorderListener.BoundMoveReason;
import com.github.shanebeee.skr.Registration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class BoundEvents extends SkriptEvent {

    private static BoundBorderListener boundBorderListener;

    public static void register(Registration reg) {
        reg.newEvent(BoundEvents.class, BoundEnterEvent.class, "(bound enter|enter bound) [with id %-string%]")
            .name("Bound - Enter")
            .description("Called when a player enters a bound. Optional ID of bound. 'event-string' = bound ID.",
                "NOTE: Due to breaking changes in Bukkit API, enter/exit events will not be called when a player mounts/dismounts an entity if running SkBee 3.5.0+ on MC 1.20.4 and below.")
            .examples("on bound enter:",
                "\tif event-bound = {bounds::spawn}:",
                "\t\tsend \"You entered spawn!\"",
                "on enter bound with id \"spawn\":",
                "\tcancel event",
                "",
                "on bound enter:",
                "\tif event-bound move reason = teleport:",
                "\t\tcancel event")
            .since("1.0.0, 1.12.2 (Bound IDs)")
            .register();

        reg.newEvent(BoundEvents.class, BoundExitEvent.class, "(bound exit|exit bound) [with id %-string%]")
            .name("Bound - Exit")
            .description("Called when a player exits a bound. Optional ID of bound. 'event-string' = bound ID.")
            .examples("on bound exit:",
                "\tsend \"You left a bound\"",
                "\tif event-bound = {bound}:",
                "\t\tsend \"You left Spawn!\"",
                "on exit bound with id \"spawn\":",
                "\tcancel event")
            .since("1.0.0, 1.12.2 (Bound IDs)")
            .register();

        reg.newEventValue(BoundEvent.class, Bound.class)
            .converter(BoundEvent::getBound)
            .register();
        reg.newEventValue(BoundEvent.class, String.class)
            .description("The ID of the bound in this event.")
            .converter(event -> event.getBound().getId())
            .register();
        reg.newEventValue(BoundEnterEvent.class, Player.class)
            .converter(BoundEnterEvent::getPlayer)
            .register();
        reg.newEventValue(BoundExitEvent.class, Player.class)
            .converter(BoundExitEvent::getPlayer)
            .register();
        reg.newEventValue(BoundEnterEvent.class, BoundMoveReason.class)
            .converter(BoundEnterEvent::getReason)
            .register();
        reg.newEventValue(BoundExitEvent.class, BoundMoveReason.class)
            .converter(BoundExitEvent::getReason)
            .register();
    }

    private Literal<String> boundID;

    @SuppressWarnings("unchecked")
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
