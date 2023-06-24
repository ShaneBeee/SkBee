package com.shanebeestudios.skbee.api.event;

import com.shanebeestudios.skbee.api.bound.Bound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a player exits a bound
 */
public class ExitBoundEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Bound bound;
    private final Player player;
    private boolean cancelled = false;

    public ExitBoundEvent(Bound bound, Player player) {
        this.bound = bound;
        this.player = player;
    }

    /** The player that exits the bound
     * @return Player that exited the bound
     */
    public Player getPlayer() {
        return player;
    }

    /** The bound that was exited
     * @return Bound that was exited
     */
    public Bound getBound() {
        return bound;
    }

    /** Check if this event is cancelled
     * @return True if event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /** Cancel this event
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
