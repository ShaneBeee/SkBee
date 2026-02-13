package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.EntityBlockStorage;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class ExprEntityBlockStorageMax extends SimplePropertyExpression<Block, Long> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprEntityBlockStorageMax.class, Long.class, "max entit(ies|y storage)", "blocks")
            .name("EntityBlockStorage - Max Entities")
            .description("Get/Set the max amount of entities which can be stored in a block.",
                "As of 1.15 this only includes beehives/bee nests! Requires Spigot/Paper 1.15.2+")
            .examples("set {_m} to max entities of target block of player",
                "set max entities of target block of player to 20",
                "set max entity storage of event-block to 5")
            .since("1.0.0")
            .register();
    }

    @Override
    public Long convert(Block block) {
        BlockState state = block.getState();
        if (state instanceof EntityBlockStorage) {
            return ((long) ((EntityBlockStorage<?>) state).getMaxEntities());
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.RESET) {
            return new Class[]{Long.class};
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, Object[] delta, ChangeMode mode) {
        for (Block block : getExpr().getArray(event)) {
            int change = delta == null ? getDefault(block) : ((Number) delta[0]).intValue();
            BlockState state = block.getState();
            if (state instanceof EntityBlockStorage<?> storage) {
                int newVal = storage.getMaxEntities();
                switch (mode) {
                    case RESET, SET -> newVal = change;
                    case ADD -> newVal += change;
                    case REMOVE -> newVal -= change;
                }
                storage.setMaxEntities(Math.max(1, newVal));
                storage.update(true, false);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Long> getReturnType() {
        return Long.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "max entities of entity storage block";
    }

    // Simple util method for getting default max entities for a block
    // Future MC versions may include more blocks (like the possible termite block)
    private int getDefault(Block block) {
        return switch (block.getType().toString()) {
            case "BEEHIVE", "BEE_NEST" -> 3;
            default -> 0;
        };
    }

}
