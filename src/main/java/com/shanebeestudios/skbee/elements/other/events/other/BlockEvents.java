package com.shanebeestudios.skbee.elements.other.events.other;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.util.SimpleEvent;
import com.github.shanebeee.skr.Registration;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValue;
import org.skriptlang.skript.lang.converter.Converter;

import java.util.List;

public class BlockEvents extends SimpleEvent {

    public static void register(Registration reg) {
        // Block Damage Abort Event
        reg.newEvent(BlockEvents.class, BlockDamageAbortEvent.class,
                "block damage abort")
            .name("Block Damage Abort")
            .description("Called when a player stops damaging a Block. Requires MC 1.18.x+")
            .examples("on block damage abort:",
                "\tsend \"get back to work\"")
            .since("2.8.3")
            .register();

        reg.newEventValue(BlockDamageAbortEvent.class, Player.class)
            .converter(BlockDamageAbortEvent::getPlayer)
            .register();

        // Block Explode Event
        reg.newEvent(BlockEvents.class, BlockExplodeEvent.class, "block explode")
            .name("Block Explode")
            .description("Called when a block explodes interacting with blocks.",
                "The event isn't called if the gamerule MOB_GRIEFING is disabled as no block interaction will occur.",
                "The Block returned by this event is not necessarily the block that caused the explosion,",
                "just the block at the location where the explosion originated.")
            .examples("")
            .since("3.2.0")
            .register();

        reg.newEventValue(BlockExplodeEvent.class, BlockData.class)
            .converter(event -> event.getBlock().getBlockData())
            .register();
        reg.newEventValue(BlockExplodeEvent.class, BlockData.class)
            .description("The blockdata of the block which exploded.")
            .time(EventValue.Time.PAST)
            .converter(new Converter<>() {
                @Override
                public @NotNull BlockData convert(BlockExplodeEvent event) {
                    BlockState explodedBlockState = event.getExplodedBlockState();
                    return explodedBlockState.getBlockData();
                }
            })
            .register();
        reg.newEventValue(BlockExplodeEvent.class, ItemType.class)
            .converter(event -> new ItemType(event.getBlock().getType()))
            .register();
        reg.newEventValue(BlockExplodeEvent.class, ItemType.class)
            .description("The item type of the block which exploded.")
            .time(EventValue.Time.PAST)
            .converter(new Converter<>() {
                @Override
                public @NotNull ItemType convert(BlockExplodeEvent event) {
                    BlockState explodedBlockState = event.getExplodedBlockState();
                    return new ItemType(explodedBlockState.getType());
                }
            })
            .register();
        reg.newEventValue(BlockExplodeEvent.class, Block[].class)
            .description("The blocks which exploded.")
            .patterns("exploded-blocks")
            .converter(from -> from.blockList().toArray(new Block[0]))
            .changer(Changer.ChangeMode.SET, (event, value) -> {
                event.blockList().clear();
                if (value != null && value.length > 0) {
                    event.blockList().addAll(List.of(value));
                }
            })
            .changer(Changer.ChangeMode.ADD, (event, value) -> {
                if (value != null) {
                    for (Block block : value) {
                        event.blockList().add(block);
                    }
                }
            })
            .changer(Changer.ChangeMode.REMOVE, (event, value) -> {
                if (value != null) {
                    for (Block block : value) {
                        event.blockList().remove(block);
                    }
                }
            })
            .changer(Changer.ChangeMode.DELETE, (event, value) -> event.blockList().clear())
            .register();

        // Moisture Change Event
        reg.newEvent(BlockEvents.class, MoistureChangeEvent.class, "moisture change")
            .name("Moisture Change")
            .description("Called when the moisture level of a farmland block changes.")
            .examples("on moisture change:",
                "\tcancel event",
                "\tset event-block to farmland[moisture=7]")
            .since("3.0.0")
            .register();

        reg.newEventValue(MoistureChangeEvent.class, Block.class)
            .time(EventValue.Time.FUTURE)
            .converter(MoistureChangeEvent::getBlock)
            .register();
    }

}
