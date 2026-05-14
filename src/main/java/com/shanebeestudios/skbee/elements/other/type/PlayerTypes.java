package com.shanebeestudios.skbee.elements.other.type;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.StringUtils;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.connection.PlayerConnection;
import net.kyori.adventure.resource.ResourcePackStatus;

import java.util.ArrayList;
import java.util.List;

public class PlayerTypes {

    public static void register(Registration reg) {
        if (Skript.classExists("io.papermc.paper.connection.PlayerConnection")) {
            if (Classes.getExactClassInfo(PlayerConnection.class) == null) {
                reg.newType(PlayerConnection.class, "playerconnection")
                    .user("player ?connections?")
                    .name("Player Connection")
                    .description("Represents the connection of a player in an async connect config event and custom click event.")
                    .defaultExpression(new EventValueExpression<>(PlayerConnection.class))
                    .parser(SkriptUtils.getDefaultParser())
                    .since("3.16.0")
                    .register();
            } else {
                Util.logLoading("It looks like another addon registered 'playerconnection' already.");
                Util.logLoading("You may have to use their PlayerConnection in SkBee's syntaxes.");
            }
        }

        // Add [intermediate] to status names
        List<String> status = new ArrayList<>();
        for (ResourcePackStatus value : ResourcePackStatus.values()) {
            status.add(value.name().toLowerCase() + "[" + value.intermediate() + "]");
        }
        status.sort(String::compareTo);
        reg.newEnumType(ResourcePackStatus.class, "resourcepackstatus", info -> info.usage(StringUtils.join(status, ", ")))
            .name("ResourcePack - Status")
            .user("resource ?pack ?status(es)?")
            .description("Represents the status of a resource pack request.",
                "The values include in square brackets whether they're intermediate " +
                    "(Whether, after receiving this status, further status events might occur), " +
                    "this is not actually part of the pattern.",
                Util.AUTO_GEN_NOTE)
            .since("3.21.0")
            .register();
    }

}
