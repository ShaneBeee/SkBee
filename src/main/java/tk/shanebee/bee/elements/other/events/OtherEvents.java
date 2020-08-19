package tk.shanebee.bee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.block.Block;
import tk.shanebee.bee.api.event.EntityBlockInteractEvent;

import javax.annotation.Nullable;

public class OtherEvents {

    static {
        Skript.registerEvent("Block Physical Interact Event", SimpleEvent.class, EntityBlockInteractEvent.class,
                "block (interact|trample)")
                .description("Called when an entity physically interacts with a block, for example, trampling.")
                .examples("on block interact:",
                        "\tif event-entity is a villager:",
                        "\t\tcancel event")
                .since("1.5.0");

        EventValues.registerEventValue(EntityBlockInteractEvent.class, Block.class, new Getter<Block, EntityBlockInteractEvent>() {
            @Nullable
            @Override
            public Block get(EntityBlockInteractEvent event) {
                return event.getBlock();
            }
        }, 0);
    }

}
