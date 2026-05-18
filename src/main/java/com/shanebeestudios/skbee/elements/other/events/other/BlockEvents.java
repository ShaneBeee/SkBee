package com.shanebeestudios.skbee.elements.other.events.other;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.bukkitutil.SoundUtils;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.util.SimpleEvent;
import com.github.shanebeee.skr.Registration;
import io.papermc.paper.event.block.BlockLockCheckEvent;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.inventory.ItemStack;
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
        reg.newEventValue(BlockDamageAbortEvent.class, ItemStack.class)
            .description("Gets the ItemStack for the item currently in the player's hand.")
            .converter(BlockDamageAbortEvent::getItemInHand)
            .register();
        reg.newEventValue(BlockDamageAbortEvent.class, ItemType.class)
            .description("Gets the ItemType for the item currently in the player's hand.")
            .converter(event -> new ItemType(event.getItemInHand()))
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
        reg.newEventValue(BlockExplodeEvent.class, Number.class)
            .description("Represents the percentage of blocks to drop from this explosion.")
            .patterns("yield")
            .converter(BlockExplodeEvent::getYield)
            .changer(ChangeMode.SET, (event, value) -> event.setYield(value.floatValue()))
            .register();
        reg.newEventValue(BlockExplodeEvent.class, Block[].class)
            .description("Represents the blocks which exploded.")
            .patterns("exploded-blocks")
            .converter(from -> from.blockList().toArray(new Block[0]))
            .changer(ChangeMode.SET, (event, value) -> {
                event.blockList().clear();
                if (value != null && value.length > 0) {
                    event.blockList().addAll(List.of(value));
                }
            })
            .changer(ChangeMode.ADD, (event, value) -> {
                if (value != null) {
                    for (Block block : value) {
                        event.blockList().add(block);
                    }
                }
            })
            .changer(ChangeMode.REMOVE, (event, value) -> {
                if (value != null) {
                    for (Block block : value) {
                        event.blockList().remove(block);
                    }
                }
            })
            .changer(ChangeMode.DELETE, (event, value) -> event.blockList().clear())
            .register();

        // Block Lock Check Event
        reg.newEvent(BlockEvents.class, BlockLockCheckEvent.class, "block lock check")
            .name("Block Lock Check")
            .description("Called when the server tries to check the lock on a lockable block entity.")
            .register();

        reg.newEventValue(BlockLockCheckEvent.class, Player.class)
            .description("Get the player involved this lock check.")
            .converter(BlockLockCheckEvent::getPlayer)
            .register();
        reg.newEventValue(BlockLockCheckEvent.class, ItemStack.class)
            .description("Represents the item used to check the lock.")
            .converter(BlockLockCheckEvent::getKeyItem)
            .changer(ChangeMode.SET, BlockLockCheckEvent::setKeyItem)
            .register();
        reg.newEventValue(BlockLockCheckEvent.class, Component.class)
            .description("Represents the locked message that will be sent if the player cannot open the block.")
            .patterns("locked-message", "locked message")
            .converter(BlockLockCheckEvent::getLockedMessage)
            .changer(ChangeMode.SET, BlockLockCheckEvent::setLockedMessage)
            .register();
        reg.newEventValue(BlockLockCheckEvent.class, String.class)
            .description("Represents the locked sound that will play if the player cannot open the block.")
            .converter(event -> {
                Sound lockedSound = event.getLockedSound();
                if (lockedSound == null) return null;

                return lockedSound.name().toString();
            })
            .changer(ChangeMode.SET, (event, sound) -> {
                NamespacedKey key = SoundUtils.getKey(sound);
                if (key == null) return;

                Sound lockedSound = event.getLockedSound();

                Sound.Source source = lockedSound != null ? lockedSound.source() : Sound.Source.BLOCK;
                float volume = lockedSound != null ? lockedSound.volume() : 1.0f;
                float pitch = lockedSound != null ? lockedSound.pitch() : 1.0f;
                Sound sound1 = Sound.sound(key, source, volume, pitch);

                event.setLockedSound(sound1);
            })
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
