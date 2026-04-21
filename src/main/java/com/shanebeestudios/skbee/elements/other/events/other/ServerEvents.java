package com.shanebeestudios.skbee.elements.other.events.other;

import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.util.Timespan;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import io.papermc.paper.event.server.ServerResourcesReloadedEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.event.command.UnknownCommandEvent;

public class ServerEvents extends SimpleEvent {

    public static void register(Registration reg) {
        // Server Resources Reloaded Event
        reg.newEvent(ServerEvents.class, ServerResourcesReloadedEvent.class,
                "server resources reload[ed]")
            .name("Server Resources Reloaded")
            .description("Called when resources such as datapacks are reloaded (e.g. /minecraft:reload).",
                "Intended for use to re-register custom recipes, advancements that may be lost during a reload like this.",
                "This can also be used after SkBriggy commands are loaded (since they appear to wipe recipes).")
            .examples("function loadRecipes():",
                "\tregister shaped recipe:",
                "\t\t...",
                "",
                "on skript load:",
                "\t# Load recipes when the server starts",
                "\tloadRecipes()",
                "",
                "on server resources reload:",
                "\t# Reload recipes when datapacks get reloaded",
                "\tloadRecipes()")
            .since("3.15.0")
            .register();

        // Server Tick End/Start Event
        reg.newEvent(ServerEvents.class, ServerTickEndEvent.class, "server tick end")
            .name("Tick End Event")
            .description("Called when the server has finished ticking the main loop.",
                "There may be time left after this event is called, and before the next tick starts.")
            .examples("")
            .since("3.10.0")
            .register();

        reg.newEventValue(ServerTickEndEvent.class, Number.class)
            .description("The current tick number.")
            .converter(ServerTickEndEvent::getTickNumber)
            .patterns("tick-number")
            .register();
        reg.newEventValue(ServerTickEndEvent.class, Timespan.class)
            .description("Time of how long this tick took.")
            .converter(event -> new Timespan(Timespan.TimePeriod.MILLISECOND, (long) event.getTickDuration()))
            .patterns("tick-duration")
            .register();
        reg.newEventValue(ServerTickEndEvent.class, Timespan.class)
            .description("Amount of time remaining before the next tick should start.")
            .converter(event -> new Timespan(Timespan.TimePeriod.MILLISECOND, event.getTimeRemaining() / 1_000_000))
            .patterns("tick-remaining")
            .register();

        reg.newEvent(ServerEvents.class, ServerTickStartEvent.class, "server tick start")
            .name("Tick Start Event")
            .description("Called each time the server starts its main tick loop.")
            .examples("")
            .since("3.10.0")
            .register();
        reg.newEventValue(ServerTickStartEvent.class, Number.class)
            .description("The current tick number.")
            .converter(ServerTickStartEvent::getTickNumber)
            .patterns("tick-number")
            .register();

        // Unknown Command Event
        reg.newEvent(ServerEvents.class, UnknownCommandEvent.class, "unknown command")
            .name("Unknown Command")
            .description("This event is fired when a player executes a command that is not defined.")
            .examples("")
            .since("3.10.0")
            .register();

        reg.newEventValue(UnknownCommandEvent.class, String.class)
            .description("The command that was sent.")
            .converter(UnknownCommandEvent::getCommandLine)
            .register();
        reg.newEventValue(UnknownCommandEvent.class, CommandSender.class)
            .description("Who sent the command.")
            .converter(UnknownCommandEvent::getSender)
            .register();
        reg.newEventValue(UnknownCommandEvent.class, ComponentWrapper.class)
            .description("The message that will be returned.")
            .converter(event -> ComponentWrapper.fromComponent(event.message()))
            .patterns("message")
            .register();
    }

}
