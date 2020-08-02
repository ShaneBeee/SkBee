package tk.shanebee.bee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;

public class PaperEvents {

    // 1.12.2
    static {
        if (Skript.classExists("com.destroystokyo.paper.event.player.PlayerArmorChangeEvent")) {
            Skript.registerEvent("Armor Change Event", SimpleEvent.class, PlayerArmorChangeEvent.class, "player change armor")
                    .description("Called when the player themselves change their armor items. Requires Paper 1.12.2+")
                    .examples("on player change armor:",
                            "\tset helmet of player to pumpkin")
                    .since("1.3.1");
        }
    }

}
