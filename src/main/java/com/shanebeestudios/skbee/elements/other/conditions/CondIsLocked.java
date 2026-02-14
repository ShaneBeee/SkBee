package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.block.Block;
import org.bukkit.block.Lockable;

public class CondIsLocked extends PropertyCondition<Block> {

    public static void register(Registration reg) {
        reg.newPropertyCondition(CondIsLocked.class, "locked", "blocks")
            .name("Is Locked")
            .description("Check if a lockable container is in a locked state.")
            .examples("on right click on shulker box or beacon:",
                "\tclicked block is locked",
                "\tplayer has permission \"see.locked\"",
                "\tsend action bar \"%container key of clicked block%\" to player")
            .since("2.16.0")
            .register();
    }

    @Override
    public boolean check(Block block) {
        if (block.getState() instanceof Lockable lockable) {
            return lockable.isLocked();
        }
        return false;
    }

    @Override
    protected String getPropertyName() {
        return "locked";
    }

}
