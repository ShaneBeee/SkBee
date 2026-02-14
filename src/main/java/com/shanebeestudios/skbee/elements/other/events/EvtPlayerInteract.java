package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
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
import org.skriptlang.skript.lang.converter.Converter;

public class EvtPlayerInteract extends SkriptEvent {

    public static void register(Registration reg) {
        reg.newEvent(EvtPlayerInteract.class,
                CollectionUtils.array(PlayerInteractEvent.class, PlayerInteractAtEntityEvent.class),
                "player interact", "player interact (at|on) entity")
            .name("Player Interact")
            .description("Called when a player interacts (clicks) a block or entity.",
                "This is similar to Skript's click event, but more verbose giving you more freedom.",
                "Note: This event may be called once for each hand.",
                "`event-vector` = An offset from the location of the clicked block/entity.",
                "`event-equipmentslot` = The slot (hand_slot, off_hand_slot) used to click (may be null).",
                "`event-blockaction` = The action that happened, such as (left_click_air, physical, right_click_block).")
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

        EventValues.registerEventValue(PlayerInteractEvent.class, EquipmentSlot.class, new Converter<>() {
            @Override
            public @Nullable EquipmentSlot convert(PlayerInteractEvent event) {
                return event.getHand();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(PlayerInteractEvent.class, Action.class, PlayerInteractEvent::getAction, EventValues.TIME_NOW);
        EventValues.registerEventValue(PlayerInteractEvent.class, Vector.class, new Converter<>() {
            @SuppressWarnings("deprecation")
            @Override
            public @Nullable Vector convert(PlayerInteractEvent from) {
                return from.getClickedPosition(); // Deprecated, new method in paper to use later
            }
        }, EventValues.TIME_NOW);
        EventValues.registerEventValue(PlayerInteractEntityEvent.class, EquipmentSlot.class, PlayerInteractEntityEvent::getHand, EventValues.TIME_NOW);
        EventValues.registerEventValue(PlayerInteractAtEntityEvent.class, Location.class, event -> {
            Location location = event.getRightClicked().getLocation();
            return location.add(event.getClickedPosition());
        }, EventValues.TIME_NOW);
        EventValues.registerEventValue(PlayerInteractAtEntityEvent.class, Vector.class, PlayerInteractAtEntityEvent::getClickedPosition, EventValues.TIME_NOW);
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
