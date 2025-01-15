package com.shanebeestudios.skbee.elements.switchcase.events;

import ch.njol.skript.lang.TriggerItem;
import org.bukkit.event.Event;

public class SwitchSecEvent extends SwitchBaseEvent {

    private final TriggerItem postSwitch;

    public SwitchSecEvent(Object switchedObject, Event parentEvent, TriggerItem postSwitch) {
        super(switchedObject, parentEvent);
        this.postSwitch = postSwitch;
    }

    public TriggerItem getPostSwitch() {
        return this.postSwitch;
    }

}
