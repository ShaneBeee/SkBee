package com.shanebeestudios.skbee.api.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when an entity physically interacts with a block, for example, trampling
 */
public class EntityBlockInteractEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    private final Block block;
    private boolean cancel;

    public EntityBlockInteractEvent(@NotNull Entity entity, Block block, boolean cancel) {
        super(entity);
        this.block = block;
        this.cancel = cancel;
    }

    /**
     * Get the block that was interacted with
     *
     * @return Block interacted with
     */
    public Block getBlock() {
        return block;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

}
