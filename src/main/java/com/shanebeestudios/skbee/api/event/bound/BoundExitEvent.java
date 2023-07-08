package com.shanebeestudios.skbee.api.event.bound;

import com.shanebeestudios.skbee.api.bound.Bound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when a player exits a bound
 */
public class BoundExitEvent extends BoundEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private boolean cancelled = false;

    public BoundExitEvent(Bound bound, Player player) {
        super(bound);
        this.player = player;
    }

    /**
     * The player that exits the bound
     *
     * @return Player that exited the bound
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Check if this event is cancelled
     *
     * @return True if event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Cancel this event
     *
     * @param cancelled Whether this event should be cancelled or not
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
