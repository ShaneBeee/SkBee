package tk.shanebee.bee.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import tk.shanebee.bee.elements.bound.objects.Bound;

/**
 * Called when a player enters a bound
 */
public class EnterBoundEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Bound bound;
    private final Player player;
    private boolean cancelled = false;

    public EnterBoundEvent(Bound bound, Player player) {
        this.bound = bound;
        this.player = player;
    }

    /** The player that entered the bound
     * @return Player that entered the bound
     */
    public Player getPlayer() {
        return player;
    }

    /** The bound that was entered
     * @return Bound that was entered
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
