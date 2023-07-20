package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Spawner - Required Player Range")
@Description({"Get the maximum distance(squared) a player can be in order for a spawner to be active.",
        "If this value is less than 0, the spawner is always active (given that there are players online).",
        "Otherwise if the value is 0 the spawner is never active.", // Documentation is incorrect, saying <= 0 is always active
        "Default value is 16."})
@Examples({"on place of mob spawner:",
        "\tset required player range of event-block to 0"})
@Since("INSERT VERSION")
public class ExprSpawnerRequiredPlayerRange extends SimplePropertyExpression<Block, Integer> {

    static {
        register(ExprSpawnerRequiredPlayerRange.class, Integer.class, "required (player|activation) range", "blocks");
    }

    private static final int DEFAULT_ACTIVATION_RANGE = 16;

    @Override
    public @Nullable Integer convert(Block block) {
        if (block.getState() instanceof CreatureSpawner spawner)
            return spawner.getRequiredPlayerRange();
        return null;
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case ADD, REMOVE, SET -> CollectionUtils.array(Integer.class);
            case RESET -> CollectionUtils.array();
            default -> null;
        };
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        int changeValue = delta == null ? DEFAULT_ACTIVATION_RANGE : (Integer) delta[0];
        switch (mode) {
            case RESET, SET:
                for (Block block : getExpr().getArray(event)) {
                    if (block.getState() instanceof CreatureSpawner spawner) {
                        spawner.setRequiredPlayerRange(changeValue);
                        spawner.update();
                    }
                }
                break;
            case REMOVE:
                changeValue = -changeValue;
            case ADD:
                for (Block block : getExpr().getArray(event)) {
                    if (block.getState() instanceof CreatureSpawner spawner) {
                        int value = spawner.getRequiredPlayerRange() + changeValue;
                        spawner.setRequiredPlayerRange(value);
                        spawner.update();
                    }
                }
                break;
            default:
                assert false;
        }
    }

    @Override
    public Class<? extends Integer> getReturnType() {
        return Integer.class;
    }

    @Override
    protected String getPropertyName() {
        return "spawner required activation range";
    }

}
