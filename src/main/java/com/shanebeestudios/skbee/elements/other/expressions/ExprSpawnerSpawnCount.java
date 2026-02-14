package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprSpawnerSpawnCount extends SimplePropertyExpression<Block, Integer> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprSpawnerSpawnCount.class, Integer.class, "[spawner] spawn count", "blocks")
                .name("Spawner - Spawn Count")
                .description("Get how many mobs attempt to spawn. Default is 4.")
                .examples("on place of mob spawner:",
                        "\tset spawner spawn count of event-block to 10",
                        "\tadd 10 to spawn count of event-block",
                        "\treset spawn count of event-block")
                .since("2.16.0")
                .register();
    }

    private static final int DEFAULT_SPAWN_COUNT = 4;

    @Override
    public @Nullable Integer convert(Block block) {
        if (block.getState() instanceof CreatureSpawner spawner)
            return spawner.getSpawnCount();
        return null;
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case ADD, SET, REMOVE -> CollectionUtils.array(Integer.class);
            case RESET -> CollectionUtils.array();
            default -> null;
        };
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        int changeValue = delta == null ? DEFAULT_SPAWN_COUNT : (Integer) delta[0];
        switch (mode) {
            case RESET, SET:
                for (Block block : getExpr().getArray(event)) {
                    if (block.getState() instanceof CreatureSpawner spawner) {
                        spawner.setSpawnCount(Math.max(changeValue, 0));
                        spawner.update();
                    }
                }
                break;
            case REMOVE:
                changeValue = -changeValue;
            case ADD:
                for (Block block : getExpr().getArray(event)) {
                    if (block.getState() instanceof CreatureSpawner spawner) {
                        int value = spawner.getSpawnCount() + changeValue;
                        spawner.setSpawnCount(Math.max(value, 0));
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
        return "spawner spawn count";
    }

}
