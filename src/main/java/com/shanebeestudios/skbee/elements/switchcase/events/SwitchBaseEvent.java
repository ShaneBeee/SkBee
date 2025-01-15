package com.shanebeestudios.skbee.elements.switchcase.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Base event for SwitchCase events with shared values
 */
public class SwitchBaseEvent extends Event {

    private final @NotNull Object switchedObject;
    private final Event parentEvent;

    public SwitchBaseEvent(@NotNull Object switchedObject, Event parentEvent) {
        this.switchedObject = switchedObject;
        this.parentEvent = parentEvent;
    }

    public @NotNull Object getSwitchedObject() {
        return this.switchedObject;
    }

    public Event getParentEvent() {
        return this.parentEvent;
    }


    @Override
    @NotNull
    public HandlerList getHandlers() {
        throw new IllegalStateException();
    }

}
