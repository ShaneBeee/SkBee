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

@Name("Spawner - Max Nearby Entities")
@Description({"Set the new maximum amount of similar entities that are allowed to be within spawning range of a spawner.",
        "If more than the maximum number of entities are within range, the spawner will not spawn and try again.",
        "Default value is 16."})
@Examples({"add 10 to maximum nearby entities of {_spawner}",
        "remove 10 from maximum nearby entities of {_spawner}",
        "reset maximum nearby entities of {_spawner}",
        "set maximum nearby entities of {_spawner} to 10"})
@Since("2.16.0")
public class ExprSpawnerMaxNearbyEntities extends SimplePropertyExpression<Block, Integer> {

    static {
        register(ExprSpawnerMaxNearbyEntities.class, Integer.class, "max nearby [spawner] entities", "blocks");
    }

    private static final int DEFAULT_MAX_NEARBY_ENTITIES = 16;

    @Override
    public @Nullable Integer convert(Block block) {
        if (block.getState() instanceof CreatureSpawner spawner)
            return spawner.getMaxNearbyEntities();
        return null;
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, ADD, REMOVE -> CollectionUtils.array(Integer.class);
            case RESET -> CollectionUtils.array();
            default -> null;
        };
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        int changeValue = delta == null ? DEFAULT_MAX_NEARBY_ENTITIES : (Integer) delta[0];
        switch (mode) {
            case RESET:
            case SET:
                for (Block block : getExpr().getArray(event)) {
                    if (block.getState() instanceof CreatureSpawner spawner) {
                        spawner.setMaxNearbyEntities(Math.max(changeValue, 0));
                        spawner.update();
                    }
                }
                break;
            case REMOVE:
                changeValue = -changeValue;
            case ADD:
                for (Block block : getExpr().getArray(event)) {
                    if (block.getState() instanceof CreatureSpawner spawner) {
                        int value = spawner.getMaxNearbyEntities() + changeValue;
                        spawner.setMaxNearbyEntities(Math.max(value, 0));
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
        return "max nearby spawner entities";
    }

}
