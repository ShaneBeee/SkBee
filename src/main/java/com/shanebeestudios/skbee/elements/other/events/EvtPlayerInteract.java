package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.util.coll.CollectionUtils;
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

    static {
        Skript.registerEvent("Player Interact", EvtPlayerInteract.class,
                        CollectionUtils.array(PlayerInteractEvent.class, PlayerInteractAtEntityEvent.class),
                        "player interact", "player interact (at|on) entity")
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
                .since("3.4.0");

        EventValues.registerEventValue(PlayerInteractEvent.class, EquipmentSlot.class, new Getter<>() {
            @Override
            public @Nullable EquipmentSlot get(PlayerInteractEvent event) {
                return event.getHand();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(PlayerInteractEvent.class, Action.class, new Getter<>() {
            @Override
            public Action get(PlayerInteractEvent event) {
                return event.getAction();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(PlayerInteractEvent.class, Vector.class, new Getter<>() {
            @SuppressWarnings("deprecation")
            @Override
            public @Nullable Vector get(PlayerInteractEvent event) {
                return event.getClickedPosition();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(PlayerInteractEntityEvent.class, EquipmentSlot.class, new Getter<>() {
            @Override
            public EquipmentSlot get(PlayerInteractEntityEvent event) {
                return event.getHand();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(PlayerInteractAtEntityEvent.class, Location.class, new Getter<>() {
            @Override
            public Location get(PlayerInteractAtEntityEvent event) {
                Location location = event.getRightClicked().getLocation();
                return location.add(event.getClickedPosition());
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(PlayerInteractAtEntityEvent.class, Vector.class, new Getter<>() {
            @Override
            public Vector get(PlayerInteractAtEntityEvent event) {
                return event.getClickedPosition();
            }
        }, EventValues.TIME_NOW);
    }

    private int pattern;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        this.pattern = matchedPattern;
        return true;
    }

    @SuppressWarnings("NullableProblems")
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
