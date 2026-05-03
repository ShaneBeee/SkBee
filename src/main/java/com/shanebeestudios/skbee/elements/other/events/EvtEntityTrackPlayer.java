package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import com.github.shanebeee.skr.Registration;
import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import io.papermc.paper.event.player.PlayerUntrackEntityEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EvtEntityTrackPlayer extends SkriptEvent {

    @SuppressWarnings("unchecked")
    public static void register(Registration reg) {
        reg.newEvent(EvtEntityTrackPlayer.class,
                new Class[]{PlayerTrackEntityEvent.class, PlayerUntrackEntityEvent.class},
                "player track entity", "player untrack entity")
            .name("Player Track/Untrack Entity")
            .description("Called when a player tracks or untracks an entity " +
                    "(Refers to the entity being sent/removed to/from the client).",
                "Adding or removing entities from the world at the point in time this event is called is completely unsupported and should be avoided.",
                "Track: If cancelled entity is not shown to the player and interaction in both directions is not possible.",
                "(This is copied from Paper javadocs and does not seem true. When testing on a zombie, the zombie still attacked me)")
            .examples("on player track entity:",
                "\tif event-entity is a zombie:",
                "\t\tcancel event")
            .since("3.22.0")
            .register();

        reg.newEventValue(PlayerTrackEntityEvent.class, Entity.class)
            .converter(PlayerTrackEntityEvent::getEntity)
            .register();
        reg.newEventValue(PlayerUntrackEntityEvent.class, Entity.class)
            .converter(PlayerUntrackEntityEvent::getEntity)
            .register();
    }

    private boolean track;

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        this.track = matchedPattern == 0;
        return true;
    }

    @Override
    public boolean check(Event event) {
        if (event instanceof PlayerTrackEntityEvent) {
            return this.track;
        } else if (event instanceof PlayerUntrackEntityEvent) {
            return !this.track;
        }
        return false;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String track = this.track ? "track" : "untrack";
        return "player " + track + " entity";
    }

}
