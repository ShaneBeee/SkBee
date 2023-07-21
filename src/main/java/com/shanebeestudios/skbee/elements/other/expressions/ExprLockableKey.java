package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Lock of Block")
@Description("Get or set the lock of a container or beacon.")
@Examples({"on right click on shulker box or beacon:",
        "\tclicked block is locked",
        "\tplayer has permission \"see.locked\"",
        "\tsend action bar \"%container key of clicked block%\" to player"})
@Since("INSERT VERSION")
public class ExprLockableKey extends SimplePropertyExpression<Block, String> {

    static {
        register(ExprLockableKey.class, String.class, "(container|lockable) key", "blocks");
    }

    @Override
    public @Nullable String convert(Block block) {
        if (block.getState() instanceof Container container) {
            return container.isLocked() ? container.getLock() : null;
        } else if (block.getState() instanceof Beacon beacon) {
            return beacon.isLocked() ? beacon.getLock() : null;
        }
        return null;
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET -> CollectionUtils.array(String.class);
            case RESET -> CollectionUtils.array();
            default -> null;
        };
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        String lock = delta == null ? null : (String) delta[0];
        if (lock != null && lock.isBlank()) lock = null;
        switch (mode) {
            case RESET, SET -> {
                for (Block block : getExpr().getArray(event)) {
                    if (block.getState() instanceof Container container) {
                        container.setLock(lock);
                        container.update();
                    } else if (block.getState() instanceof Beacon beacon) {
                        beacon.setLock(lock);
                        beacon.update();
                    }
                }
            }
            default -> {
                assert false;
            }
        }
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected String getPropertyName() {
        return "container key";
    }

}
