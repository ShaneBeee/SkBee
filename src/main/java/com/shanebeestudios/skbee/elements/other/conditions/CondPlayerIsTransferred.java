package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CondPlayerIsTransferred extends PropertyCondition<Player> {

    public static void register(Registration reg) {
        reg.newPropertyCondition(CondPlayerIsTransferred.class, "transferred", "players")
            .name("Is Transferred")
            .description("Check if a player transferred servers.")
            .examples("on join:",
                "\tif player is transferred:",
                "\t\tkick player due to \"No Transfers Bruh!\"")
            .since("3.5.0")
            .register();

    }

    @Override
    public boolean check(Player player) {
        return player.isTransferred();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "transferred";
    }

}
