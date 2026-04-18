package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EvtPlayerInteract extends SkriptEvent {

    @SuppressWarnings("deprecation")
    public static void register(Registration reg) {
        reg.newEvent(EvtPlayerInteract.class,
                CollectionUtils.array(PlayerInteractEvent.class, PlayerInteractAtEntityEvent.class),
                "player interact", "player interact (at|on) entity")
            .name("Player Interact")
            .description("Called when a player interacts (clicks) a block or entity.",
                "This is similar to Skript's click event, but more verbose giving you more freedom.",
                "Note: This event may be called once for each hand.",
                "`event-vector` = ",
                "`event-equipmentslot` = .",
                "`event-blockaction` = .")
            .examples("on player interact:",
                "\tif all:",
                "\t\tevent-equipmentslot = off_hand_slot",
                "\t\tevent-blockaction = right_click_block",
                "\t\ttype of event-item = torch",
                "\t\tname of event-item = \"Mr Torchie\"",
                "\t\tplayer is sneaking",
                "\tthen:",
                "\t\tcancel event",
                "\t\tset {_l} to (exact location of clicked block) ~ event-vector",
                "\t\tmake 5 of dust using dustOption(red, 1) at {_l}",
                "",
                "on player interact on entity:",
                "\tif all:",
                "\t\tevent-equipmentslot = off_hand_slot",
                "\t\ttype of event-item = leash",
                "\t\tname of event-item = \"Mr Leashie\"",
                "\tthen:",
                "\t\tcancel event",
                "\t\tkill clicked entity")
            .since("3.4.0")
            .register();

        reg.newEventValue(PlayerInteractEvent.class, EquipmentSlot.class)
            .description("The slot (hand_slot, off_hand_slot) used to click (may be null).")
            .converter(PlayerInteractEvent::getHand)
            .register();

        reg.newEventValue(PlayerInteractEvent.class, Action.class)
            .description("The action that happened, such as (left_click_air, physical, right_click_block).")
            .converter(PlayerInteractEvent::getAction)
            .register();
        // Deprecated, new method in paper to use later
        reg.newEventValue(PlayerInteractEvent.class, Vector.class)
            .description("An offset from the location of the clicked block/entity.")
            .converter(PlayerInteractEvent::getClickedPosition)
            .register();
        reg.newEventValue(PlayerInteractEntityEvent.class, EquipmentSlot.class)
            .description("The slot (hand_slot, off_hand_slot) used to click (may be null).")
            .converter(PlayerInteractEntityEvent::getHand)
            .register();
        reg.newEventValue(PlayerInteractAtEntityEvent.class, Location.class)
            .converter(event -> {
                Location location = event.getRightClicked().getLocation();
                return location.add(event.getClickedPosition());
            })
            .register();
        reg.newEventValue(PlayerInteractAtEntityEvent.class, Vector.class)
            .converter(PlayerInteractAtEntityEvent::getClickedPosition)
            .register();
    }

    private int pattern;

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        this.pattern = matchedPattern;
        return true;
    }

    @Override
    public boolean check(Event event) {
        if (this.pattern == 0 && event instanceof PlayerInteractEvent) return true;
        else return this.pattern == 1 && event instanceof PlayerInteractEntityEvent;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return this.pattern == 0 ? "player interact" : "player interact at entity";
    }

}
