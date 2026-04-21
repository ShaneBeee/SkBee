package com.shanebeestudios.skbee.elements.other.events.other;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import io.papermc.paper.event.packet.UncheckedSignChangeEvent;
import io.papermc.paper.math.BlockPosition;
import org.bukkit.Location;

public class PacketEvents extends SimpleEvent {

    @SuppressWarnings("UnstableApiUsage")
    public static void register(Registration reg) {
        // UncheckedSignChangeEvent
        if (Skript.classExists("io.papermc.paper.event.packet.UncheckedSignChangeEvent")) {
            reg.newEvent(PacketEvents.class, UncheckedSignChangeEvent.class, "unchecked sign change")
                .name("Unchecked Sign Change")
                .description("Called when a client attempts to modify a sign, but the location at which the sign should be edited has not yet been checked for the existence of a real sign.",
                    "This event is used for client side sign changes.",
                    "`event-text components` = The lines from the sign (will include all 4 lines, reglardless if they were changed).",
                    "`event-location` = The location of the client side sign block.")
                .examples("")
                .since("3.11.3")
                .register();

            reg.newEventValue(UncheckedSignChangeEvent.class, ComponentWrapper[].class)
                .converter(from -> {
                    ComponentWrapper[] comps = new ComponentWrapper[4];
                    for (int i = 0; i < 4; i++) {
                        comps[i] = ComponentWrapper.fromComponent(from.lines().get(i));
                    }
                    return comps;
                })
                .register();
            reg.newEventValue(UncheckedSignChangeEvent.class, Location.class)
                .converter(from -> {
                    BlockPosition editedBlockPosition = from.getEditedBlockPosition();
                    return editedBlockPosition.toLocation(from.getPlayer().getWorld());
                })
                .register();
        }
    }

}
