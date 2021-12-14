package com.shanebeestudios.skbee.elements.gameevent.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.GameEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockReceiveGameEvent;
import org.bukkit.event.world.GenericGameEvent;
import org.jetbrains.annotations.Nullable;

public class EvtGameEvents extends SkriptEvent {

    static {
        Skript.registerEvent("Generic Game Event", EvtGameEvents.class, GenericGameEvent.class,
                        "[generic] game[ ]event [%-gameevent%]")
                .description("Called when a Minecraft game event is fired. These events are provided directly by Minecraft.",
                        "NOTE: Cancelling this event will not cancel the action, only cancel broadcasting event to blocks.",
                        "Requires MC 1.17+")
                .examples("on game event splash:",
                        "\tset {_e} to event-entity",
                        "\tif {_e} is a player:",
                        "\t\tpush {_e} up with speed 0.5")
                .since("INSERT VERSION");
        Skript.registerEvent("Block Receive Game Event", EvtGameEvents.class, BlockReceiveGameEvent.class,
                        "block receive game[ ]event [%-gameevent%]")
                .description("Called when a block receives a Minecraft game event. As of now the only block that receives these are Skulk sensors.",
                        "Requires MC 1.17+")
                .examples("on block receive game event:",
                        "\tset {_e} to event-entity",
                        "\tif {_e} is a player:",
                        "\t\tif event-block is set:",
                        "\t\t\tdamage {_e} by 0.5")
                .since("INSERT VERSION");

        EventValues.registerEventValue(GenericGameEvent.class, Entity.class, new Getter<Entity, GenericGameEvent>() {
            @Nullable
            @Override
            public Entity get(GenericGameEvent event) {
                return event.getEntity();
            }
        }, 0);

        EventValues.registerEventValue(GenericGameEvent.class, GameEvent.class, new Getter<GameEvent, GenericGameEvent>() {
            @Nullable
            @Override
            public GameEvent get(GenericGameEvent event) {
                return event.getEvent();
            }
        }, 0);

        EventValues.registerEventValue(GenericGameEvent.class, Location.class, new Getter<Location, GenericGameEvent>() {
            @Nullable
            @Override
            public Location get(GenericGameEvent event) {
                return event.getLocation();
            }
        }, 0);

        EventValues.registerEventValue(BlockReceiveGameEvent.class, Entity.class, new Getter<Entity, BlockReceiveGameEvent>() {
            @Nullable
            @Override
            public Entity get(BlockReceiveGameEvent event) {
                return event.getEntity();
            }
        }, 0);

        EventValues.registerEventValue(BlockReceiveGameEvent.class, GameEvent.class, new Getter<GameEvent, BlockReceiveGameEvent>() {
            @Nullable
            @Override
            public GameEvent get(BlockReceiveGameEvent event) {
                return event.getEvent();
            }
        }, 0);
    }

    private Literal<GameEvent> gameEvents;

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        if (args[0] != null) {
            gameEvents = ((Literal<GameEvent>) args[0]);
        }
        return true;
    }

    @Override
    public boolean check(Event event) {
        if (event instanceof GenericGameEvent) {
            if (gameEvents != null) {
                GameEvent eventGameEvent = ((GenericGameEvent) event).getEvent();
                return gameEvents.check(event, gameEvent -> gameEvent == eventGameEvent);
            } else {
                return true;
            }
        } else if (event instanceof BlockReceiveGameEvent) {
            if (gameEvents != null) {
                GameEvent eventGameEvent = ((BlockReceiveGameEvent) event).getEvent();
                return gameEvents.check(event, gameEvent -> gameEvent == eventGameEvent);
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "generic/block receive game event" + ((gameEvents != null) ? " " + gameEvents.toString(e, d) : "");
    }
}
