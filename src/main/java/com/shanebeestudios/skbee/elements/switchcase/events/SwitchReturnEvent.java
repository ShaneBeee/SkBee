package com.shanebeestudios.skbee.elements.switchcase.events;

import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

/**
 * Event for switch cases that return a value
 */
public class SwitchReturnEvent extends SwitchBaseEvent {

    private Object[] returnedObject;

    public SwitchReturnEvent(Object object, Event parentEvent) {
        super(object, parentEvent);
    }

    public @Nullable Object[] getReturnedObject() {
        return this.returnedObject;
    }

    public void setReturnedObject(Object[] returnedObject) {
        this.returnedObject = returnedObject;
    }

}
