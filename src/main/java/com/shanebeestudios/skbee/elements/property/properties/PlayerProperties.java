package com.shanebeestudios.skbee.elements.property.properties;

import ch.njol.skript.Skript;
import com.shanebeestudios.skbee.api.property.Property;
import com.shanebeestudios.skbee.api.property.PropertyRegistry;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class PlayerProperties {

    static {
        if (Skript.methodExists(Player.class, "getDeathScreenScore")) {
            PropertyRegistry.registerProperty("death screen score", new Property<>(Player.class, Integer.class) {
                    @Override
                    public Integer get(Player player) {
                        return player.getDeathScreenScore();
                    }

                    @Override
                    public void set(Player player, Integer value) {
                        player.setDeathScreenScore(value);
                    }
                })
                .description("Represents the \"Score\" the player sees in their death screen.",
                    "Requires Paper 1.21.4+")
                .since("INSERT VERSION");
        }
    }

}
