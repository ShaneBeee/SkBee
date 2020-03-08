package tk.shanebee.bee.elements.bound.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.entity.Player;
import tk.shanebee.bee.api.event.EnterBoundEvent;
import tk.shanebee.bee.api.event.ExitBoundEvent;
import tk.shanebee.bee.elements.bound.objects.Bound;

public class SimpleEvents {

    static {
        Skript.registerEvent("Bound - Enter", SimpleEvent.class, EnterBoundEvent.class, "bound enter")
                .description("Called when a player enters a bound")
                .examples("on bound enter:", "\tif event-bound = {bounds::spawn}:", "\t\tsend \"You entered spawn!\"")
                .since("2.7.0");
        EventValues.registerEventValue(EnterBoundEvent.class, Player.class, new Getter<Player, EnterBoundEvent>() {
            @Override
            public Player get(EnterBoundEvent event) {
                return event.getPlayer();
            }
        }, 0);
        EventValues.registerEventValue(EnterBoundEvent.class, Bound.class, new Getter<Bound, EnterBoundEvent>() {
            @Override
            public Bound get(EnterBoundEvent event) {
                return event.getBound();
            }
        }, 0);


        Skript.registerEvent("Bound - Exit", SimpleEvent.class, ExitBoundEvent.class, "bound exit")
                .description("Called when a player exits a bound")
                .examples("on bound exit:", "\tsend \"You left a bound\"", "\tif event-bound = {bound}:", "\t\tsend \"You left Spawn!\"")
                .since("2.7.0");
        EventValues.registerEventValue(ExitBoundEvent.class, Player.class, new Getter<Player, ExitBoundEvent>() {
            @Override
            public Player get(ExitBoundEvent event) {
                return event.getPlayer();
            }
        }, 0);
        EventValues.registerEventValue(ExitBoundEvent.class, Bound.class, new Getter<Bound, ExitBoundEvent>() {
            @Override
            public Bound get(ExitBoundEvent event) {
                return event.getBound();
            }
        }, 0);
    }

}
