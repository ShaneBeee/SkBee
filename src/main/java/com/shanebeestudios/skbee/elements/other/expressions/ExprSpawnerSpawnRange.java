package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprSpawnerSpawnRange extends SimplePropertyExpression<Block, Integer> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprSpawnerSpawnRange.class, Integer.class, "[spawner] spawn range", "blocks")
                .name("Spawner - Spawn Range")
                .description("Get the radius around which a spawner will attempt to spawn mobs in.",
                        "This area is square, includes the block the spawner is in, and is centered on the spawner's x,z coordinates - not the spawner itself.",
                        "It is 2 blocks high, centered on the spawner's y-coordinate (its bottom); thus allowing mobs to spawn as high as its top surface and as low as 1 block below its bottom surface.",
                        "Default value is 4.")
                .examples("on place of mob spawner:",
                        "\tset spawn range of event-block to random integer between 0 and 10")
                .since("2.16.0")
                .register();
    }

    private static final int DEFAULT_SPAWN_RANGE = 4;

    @Override
    public @Nullable Integer convert(Block block) {
        if (block.getState() instanceof CreatureSpawner spawner)
            return spawner.getSpawnRange();
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
        int changeValue = delta == null ? DEFAULT_SPAWN_RANGE : (Integer) delta[0];
        switch (mode) {
            case RESET, SET:
                for (Block block : getExpr().getArray(event)) {
                    if (block.getState() instanceof CreatureSpawner spawner) {
                        spawner.setSpawnRange(Math.max(changeValue, 0));
                        spawner.update();
                    }
                }
                break;
            case REMOVE:
                changeValue = -changeValue;
            case ADD:
                for (Block block : getExpr().getArray(event)) {
                    if (block.getState() instanceof CreatureSpawner spawner) {
                        int value = spawner.getSpawnRange() + changeValue;
                        spawner.setSpawnRange(Math.max(value, 0));
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
        return "spawner spawn range";
    }

}
