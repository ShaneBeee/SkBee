package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Name("Is Transferred")
@Description("Check if a player transferred servers. Requires Minecraft 1.20.5+")
@Examples({"on join:",
    "\tif player is transferred:",
    "\t\tkick player due to \"No Transfers Bruh!\""})
@Since("INSERT VERSION")
public class CondPlayerIsTransferred extends PropertyCondition<Player> {

    static {
        if (Skript.methodExists(Player.class, "isTransferred")) {
            register(CondPlayerIsTransferred.class, PropertyType.BE, "transferred", "players");
        }
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
