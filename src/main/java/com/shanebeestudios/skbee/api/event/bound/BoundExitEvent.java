package com.shanebeestudios.skbee.api.event.bound;

import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.listener.BoundBorderListener.BoundMoveReason;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when a player exits a bound
 */
public class BoundExitEvent extends BoundEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final BoundMoveReason reason;
    private boolean cancelled = false;

    public BoundExitEvent(Bound bound, Player player, BoundMoveReason reason) {
        super(bound);
        this.player = player;
        this.reason = reason;
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
     * Get the reason the player moved out of this bound.
     *
     * @return Reason player moved out of bound
     */
    public BoundMoveReason getReason() {
        return this.reason;
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
