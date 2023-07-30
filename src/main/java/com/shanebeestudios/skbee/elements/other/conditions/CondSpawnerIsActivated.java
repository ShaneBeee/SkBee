package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;

@Name("Spawner - Is Activated")
@Description("Returns true if a player is currently within the required player range of the spawner.")
@Examples({"at 7:00:",
        "\tloop {spawners::*}:",
        "\t\tloop-value is activated",
        "\t\tbroadcast loop-value"})
@Since("2.16.0")
public class CondSpawnerIsActivated extends PropertyCondition<Block> {

    static {
        register(CondSpawnerIsActivated.class, "activated", "blocks");
    }

    @Override
    public boolean check(Block block) {
        if (block.getState() instanceof CreatureSpawner spawner)
            return spawner.isActivated();
        return false;
    }

    @Override
    protected String getPropertyName() {
        return "activated";
    }

}
