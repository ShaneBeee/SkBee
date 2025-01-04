package com.shanebeestudios.skbee.elements.gameevent.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import org.bukkit.GameEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockReceiveGameEvent;
import org.bukkit.event.world.GenericGameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.Converter;

@SuppressWarnings("unused")
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
            .since("1.14.0");
        Skript.registerEvent("Block Receive Game Event", EvtGameEvents.class, BlockReceiveGameEvent.class,
                "block receive game[ ]event [%-gameevent%]")
            .description("Called when a block receives a Minecraft game event.",
                "As of now the only block that receives game events are sculk shrieker, sculk sensor, and calibrated sculk sensor.",
                "Requires MC 1.17+")
            .examples("on block receive game event:",
                "\tset {_e} to event-entity",
                "\tif {_e} is a player:",
                "\t\tif event-block is set:",
                "\t\t\tdamage {_e} by 0.5")
            .since("1.14.0");

        EventValues.registerEventValue(GenericGameEvent.class, Entity.class, new Converter<>() {
            @Nullable
            @Override
            public Entity convert(GenericGameEvent event) {
                return event.getEntity();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(GenericGameEvent.class, GameEvent.class, GenericGameEvent::getEvent, EventValues.TIME_NOW);
        EventValues.registerEventValue(GenericGameEvent.class, Location.class, GenericGameEvent::getLocation, EventValues.TIME_NOW);
        EventValues.registerEventValue(GenericGameEvent.class, Player.class, event -> {
            if (event.getEntity() instanceof Player player) return player;
            return null;
        }, EventValues.TIME_NOW);
        EventValues.registerEventValue(BlockReceiveGameEvent.class, Entity.class, BlockReceiveGameEvent::getEntity, EventValues.TIME_NOW);
        EventValues.registerEventValue(BlockReceiveGameEvent.class, GameEvent.class, BlockReceiveGameEvent::getEvent, EventValues.TIME_NOW);
        EventValues.registerEventValue(BlockReceiveGameEvent.class, Player.class, event -> {
            if (event.getEntity() instanceof Player player) return player;
            return null;
        }, EventValues.TIME_NOW);
    }

    private Literal<GameEvent> gameEvents;

    @SuppressWarnings("unchecked")
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
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "generic/block receive game event" + ((gameEvents != null) ? " " + gameEvents.toString(e, d) : "");
    }
}
