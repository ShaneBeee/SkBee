package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import org.bukkit.block.Block;
import org.bukkit.block.Lockable;

@Name("Is Locked")
@Description("Check if a lockable container is in a locked state.")
@Examples({"on right click on shulker box or beacon:",
    "\tclicked block is locked",
    "\tplayer has permission \"see.locked\"",
    "\tsend action bar \"%container key of clicked block%\" to player"})
@Since("2.16.0")
public class CondIsLocked extends PropertyCondition<Block> {

    static {
        register(CondIsLocked.class, "locked", "blocks");
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
