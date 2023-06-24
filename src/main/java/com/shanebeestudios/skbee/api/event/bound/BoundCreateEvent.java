package com.shanebeestudios.skbee.api.event.bound;

import com.shanebeestudios.skbee.api.bound.Bound;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BoundCreateEvent extends BoundEvent {

    public BoundCreateEvent(Bound bound) {
        super(bound);
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        throw new IllegalStateException();
    }

}
