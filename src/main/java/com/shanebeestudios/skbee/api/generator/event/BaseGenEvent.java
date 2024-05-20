package com.shanebeestudios.skbee.api.generator.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BaseGenEvent extends Event {
    @Override
    public @NotNull HandlerList getHandlers() {
        throw new RuntimeException("This event shouldn't be called!");
    }
}
