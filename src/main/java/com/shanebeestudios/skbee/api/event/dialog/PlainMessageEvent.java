package com.shanebeestudios.skbee.api.event.dialog;

import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlainMessageEvent extends Event {

    private PlainMessageDialogBody plainMessage;

    public PlainMessageEvent() {
    }

    public void setPlainMessage(PlainMessageDialogBody plainMessage) {
        this.plainMessage = plainMessage;
    }

    public PlainMessageDialogBody getPlainMessage() {
        return this.plainMessage;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        throw new IllegalStateException("This event should never be called!");
    }

}
