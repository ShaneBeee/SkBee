package com.shanebeestudios.skbee.api.event.bound;

import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.listener.BoundBorderListener.BoundMoveReason;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when a player enters a bound
 */
public class BoundEnterEvent extends BoundEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final BoundMoveReason reason;
    private boolean cancelled = false;

    public BoundEnterEvent(Bound bound, Player player, BoundMoveReason reason) {
        super(bound);
        this.player = player;
        this.reason = reason;
    }

    /**
     * The player that entered the bound
     *
     * @return Player that entered the bound
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the reason the player moved into this bound.
     *
     * @return Reason player moved into bound
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
