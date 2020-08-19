package tk.shanebee.bee.elements.other.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class PaperEvents {

    static {
        // Player Armor Change Event
        if (Skript.classExists("com.destroystokyo.paper.event.player.PlayerArmorChangeEvent")) {
            Skript.registerEvent("Armor Change Event", SimpleEvent.class, PlayerArmorChangeEvent.class, "player change armor")
                    .description("Called when the player themselves change their armor items. Requires Paper 1.12.2+")
                    .examples("on player change armor:",
                            "\tset helmet of player to pumpkin")
                    .since("1.3.1");
        }

        // Player Recipe Book Click Event
        if (Skript.classExists("com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent")) {
            Skript.registerEvent("Recipe Book Click Event", SimpleEvent.class, PlayerRecipeBookClickEvent.class, "[player] recipe book click")
                    .description("Called when the player clicks on a recipe in their recipe book. Requires Paper 1.15+")
                    .examples("on recipe book click:",
                            "\tif event-string = \"minecraft:diamond_sword\":",
                            "\t\tcancel event")
                    .since("INSERT VERSION");

            EventValues.registerEventValue(PlayerRecipeBookClickEvent.class, String.class, new Getter<String, PlayerRecipeBookClickEvent>() {
                @Nullable
                @Override
                public String get(@NotNull PlayerRecipeBookClickEvent event) {
                    return event.getRecipe().toString();
                }
            }, 0);
        }
    }

}
