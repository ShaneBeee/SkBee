package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;

public class CondSpawnerIsActivated extends PropertyCondition<Block> {

    public static void register(Registration reg) {
        reg.newPropertyCondition(CondSpawnerIsActivated.class, "activated", "blocks")
            .name("Spawner - Is Activated")
            .description("Returns true if a player is currently within the required player range of the spawner.")
            .examples("at 7:00:",
                "\tloop {spawners::*}:",
                "\t\tloop-value is activated",
                "\t\tbroadcast loop-value")
            .since("2.16.0")
            .register();
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
