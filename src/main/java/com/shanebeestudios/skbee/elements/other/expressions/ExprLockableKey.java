package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lockable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
@Name("Key of Block")
@Description({"Get or set the lock of a container or beacon.",
    "Removed in Minecraft 1.21.2.",
    "See 'Apply Lock to Block' effect to apply an item as a lock to a block."})
@Examples({"on right click on shulker box or beacon:",
    "\tclicked block is locked",
    "\tplayer has permission \"see.locked\"",
    "\tsend action bar \"%container key of clicked block%\" to player"})
@Since("2.16.0")
public class ExprLockableKey extends SimplePropertyExpression<Block, String> {

    private static final boolean INVALID = Skript.isRunningMinecraft(1, 21, 2);

    static {
        register(ExprLockableKey.class, String.class, "(container|lockable) key", "blocks");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (INVALID) {
            Skript.warning("String based container locks have been removed in Minecraft 1.21.2.");
        }
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable String convert(Block block) {
        if (block.getState() instanceof Lockable container) {
            return container.isLocked() ? container.getLock() : null;
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
                    BlockState state = block.getState();
                    if (state instanceof Lockable lockable) {
                        lockable.setLock(lock);
                        state.update();
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
