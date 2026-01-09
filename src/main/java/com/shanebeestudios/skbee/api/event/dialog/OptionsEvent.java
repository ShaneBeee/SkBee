package com.shanebeestudios.skbee.api.event.dialog;

import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class OptionsEvent extends Event {

    private final List<OptionEntry> entries = new ArrayList<>();

    public OptionsEvent() {
    }

    public void addEntry(OptionEntry entry) {
        this.entries.add(entry);
    }

    public List<OptionEntry> getEntries() {
        return this.entries;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        throw new IllegalStateException("This event should never be called!");
    }

}
