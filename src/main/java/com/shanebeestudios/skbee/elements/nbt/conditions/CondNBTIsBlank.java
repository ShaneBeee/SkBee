package com.shanebeestudios.skbee.elements.nbt.conditions;

import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.PropertyCondition;
import de.tr7zw.changeme.nbtapi.NBTCompound;

public class CondNBTIsBlank extends PropertyCondition<NBTCompound> {

    public static void register(Registration reg) {
        reg.newCondition(CondNBTIsBlank.class, "[a[n]] (blank|empty) nbt compound", "nbtcompounds")
            .name("NBT - Is Blank Compound")
            .description("Checks whether or not the provided nbt compounds are empty.")
            .examples("broadcast whether empty nbt compound is an empty nbt compound",
                "send whether nbt from \"{}\" is an empty nbt compound",
                "",
                "set {_nbt} to custom nbt copy of player's tool",
                "if {_nbt} is a blank nbt compound:",
                "\tbroadcast \"You got zero nbt ;(\"")
            .since("3.13.2")
            .register();
    }

    @Override
    public boolean check(NBTCompound nbtCompound) {
        return nbtCompound.getKeys().isEmpty();
    }

    @Override
    protected String getPropertyName() {
        return "empty nbt compound";
    }

}
