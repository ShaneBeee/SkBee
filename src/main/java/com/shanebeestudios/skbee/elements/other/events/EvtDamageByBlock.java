package com.shanebeestudios.skbee.elements.other.events;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EvtDamageByBlock extends SkriptEvent {

    public static void register(Registration reg) {
        reg.newEvent( EvtDamageByBlock.class, EntityDamageByBlockEvent.class,
                "damag(e|ing) [of %-entitydata%] (by|from) (block|%itemtypes/blockdatas%)")
            .name("Damage By Block")
            .description("Called when an entity is damaged by a block.",
                "Anything that works in vanilla Skript's damage event (victim/damage cause/damage/final damage)",
                "will all work in this event too.",
                "\n`victim` = Same as vanilla Skript, `victim` is used to get the damaged entity.",
                "\n`event-block` = The block that damaged the entity")
            .examples("on damage of player by sweet berry bush:",
                "\tcancel event",
                "",
                "on damage by block:",
                "\tbroadcast \"%victim% was damaged by %type of event-block%\"")
            .since("3.0.2")
            .register();

        EventValues.registerEventValue(EntityDamageByBlockEvent.class, Block.class, EntityDamageByBlockEvent::getDamager, EventValues.TIME_NOW);
    }

    private Literal<EntityData<?>> entityType;
    private Literal<?> blockType;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        this.entityType = (Literal<EntityData<?>>) args[0];
        this.blockType = args[1];
        return true;
    }

    @Override
    public boolean check(Event event) {
        if (event instanceof EntityDamageByBlockEvent damageEvent) {
            return checkDamaged(damageEvent.getEntity()) && checkDamager(damageEvent.getDamager());
        }
        return false;
    }

    // Copied from Skript
    private boolean checkDamaged(Entity entity) {
        if (this.entityType != null) {
            for (EntityData<?> entityData : this.entityType.getAll()) {
                if (entityData.isInstance(entity))
                    return true;
            }
            return false;
        }
        return true;
    }

    private boolean checkDamager(Block block) {
        if (this.blockType != null) {
            BlockData data = block.getBlockData();
            for (Object object : this.blockType.getAll()) {
                if (object instanceof ItemType itemType) {
                    if (itemType.isOfType(block)) return true;
                } else if (object instanceof BlockData blockData) {
                    if (data.matches(blockData)) return true;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String entity = this.entityType != null ? (" of " + this.entityType.toString(e, d)) : "";
        String block = this.blockType != null ? this.blockType.toString(e, d) : "block";
        return "damage" + entity + " by " + block;
    }

}
