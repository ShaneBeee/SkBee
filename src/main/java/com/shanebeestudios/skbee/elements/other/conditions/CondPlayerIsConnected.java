package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

@Name("OfflinePlayer is Connected")
@Description({"Checks whether the connection to this player is still valid.",
        " This will return true as long as this specific instance of the player is still connected.",
        "This will return false after this instance has disconnected, even if the same player has reconnected since.",
        "\nNOTE: This will be better for while loops to prevent stacking loops if the player relogs within your wait time.",
        "\nRequires PaperMC 1.20.1[build-161]+"})
@Examples({"while player is connected:",
        "\tgive player a diamond",
        "\twait 1 minute"})
@Since("2.18.0")
public class CondPlayerIsConnected extends PropertyCondition<OfflinePlayer> {

    static {
       if (Skript.methodExists(OfflinePlayer.class, "isConnected") && !Util.IS_RUNNING_SKRIPT_2_9) {
           register(CondPlayerIsConnected.class, PropertyType.BE, "connected", "offlineplayers");
       }
    }

    @Override
    public boolean check(OfflinePlayer offlinePlayer) {
        return offlinePlayer.isConnected();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "connected";
    }

}
