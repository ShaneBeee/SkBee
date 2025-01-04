package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.jetbrains.annotations.NotNull;

public class EvtEntitiesLoad extends SkriptEvent {
    static {
        Skript.registerEvent("Entities Loading", EvtEntitiesLoad.class,
                CollectionUtils.array(EntitiesLoadEvent.class, EntitiesUnloadEvent.class),
                "entities [:un]load[ed]")
            .description("Called when entities are loaded/unloaded.",
                "Event-Values:",
                "`event-entities` = The entities that were loaded.",
                "`event-chunk` = The chunk these entities loaded in (the chunk may or may not be loaded).")
            .examples("on entities loaded:",
                "\tif event-entities is set:",
                "\t\tdelete event-entities",
                "\t\tbroadcast \"REMOVED ENTITIES!\"")
            .since("3.5.0");

        EventValues.registerEventValue(EntitiesLoadEvent.class, Entity[].class, event -> event.getEntities().toArray(new Entity[0]), EventValues.TIME_NOW);
        EventValues.registerEventValue(EntitiesUnloadEvent.class, Entity[].class, event -> event.getEntities().toArray(new Entity[0]), EventValues.TIME_NOW);
    }

    private boolean load;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        this.load = !parseResult.hasTag("un");
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean check(Event event) {
        if (this.load && event instanceof EntitiesLoadEvent) return true;
        else return !this.load && event instanceof EntitiesUnloadEvent;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String load = this.load ? "loaded" : "unloaded";
        return "entities " + load;
    }

}
