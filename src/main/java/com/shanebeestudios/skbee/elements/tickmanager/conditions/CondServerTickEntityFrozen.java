package com.shanebeestudios.skbee.elements.tickmanager.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

@Name("Server Tick - Is Entity Frozen")
@Description({"Checks if a given entity is frozen.", "Entities will only be frozen if 3 criteria are met:",
        "\n - Server is frozen (via `/tick freeze` command or frozen state expression).",
        "\n - Entity is not a player.",
        "\n - Entity has no player passengers.",
        "\nI understand the syntax is a bit silly but it's to prevent collission with Skript's `%entity% is frozen` condition.",
        Util.MCWIKI_TICK_COMMAND, "Requires Minecraft 1.20.4+"})
@Examples({"if target entity is tick frozen:",
        "if loop-entity is tick frozen:",
        "if all entities are tick frozen:"})
@Since("3.1.0")
public class CondServerTickEntityFrozen extends PropertyCondition<Entity> {

    static {
        register(CondServerTickEntityFrozen.class, PropertyType.BE, "tick frozen", "entities");
    }

    @Override
    public boolean check(Entity entity) {
        return Bukkit.getServerTickManager().isFrozen(entity);
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "tick frozen";
    }

}
