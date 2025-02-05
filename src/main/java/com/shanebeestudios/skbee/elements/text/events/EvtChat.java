package com.shanebeestudios.skbee.elements.text.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import io.papermc.paper.event.player.AsyncChatEvent;

@SuppressWarnings("unused")
public class EvtChat extends SimpleEvent {

    static {
        Skript.registerEvent("Async Chat", EvtChat.class, AsyncChatEvent.class, "async chat")
            .description("An event fired when a Player sends a chat message to the server.",
                "This event is for use with text components, if you don't need components stick with Skript's chat event.",
                "",
                "**NOTE**: Due to being async, some Skript things won't work and will fire errors (ex: setting a block).",
                "Requires PaperMC.")
            .examples("on async chat:",
                "\tset {_m::1} to mini message from \"<gradient:##33FFE6:##33FF68>%player% <grey>» \"",
                "\tset {_m::2} to async chat message",
                "\tset {_m} to merge components {_m::*}",
                "\tset async chat format to {_m}")
            .since("2.18.0");
    }

    @Override
    public boolean canExecuteAsynchronously() {
        return true;
    }

}
