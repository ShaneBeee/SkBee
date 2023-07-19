package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;


@Name("Spawner - Spawn Delay")
@Description({"The delay between a spawner spawning a new entity.",
        "Maximum default is 800 ticks (40 seconds).",
        "Minimum default is 200 ticks (10 seconds)."})
@Examples({"on place of mob spawner:",
        "\tset spawner spawn delay of event-block to 3 seconds",
        "\tadd 100 to max spawn delay of event-block",
        "\tremove 1 second from min spawn delay of event-block",
        "\treset spawn delay of event-block"})
@Since("INSERT VERSION")
public class ExprSpawnerSpawnDelay extends SimplePropertyExpression<Block, Timespan> {

    static {
        register(ExprSpawnerSpawnDelay.class, Timespan.class, "[(-1:min|1:max)[imum]] [spawner] spawn delay", "blocks");
    }

    private static final int DEFAULT_MAX_TICKS = 800, DEFAULT_MIN_TICKS = 200;
    private Kleenean action;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        action = Kleenean.get(parseResult.mark);
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    @Nullable
    public Timespan convert(Block block) {
        BlockState blockState = block.getState();
        if (blockState instanceof CreatureSpawner spawner) {
            int delay = switch (action) {
                case FALSE -> spawner.getMinSpawnDelay();
                case TRUE -> spawner.getMaxSpawnDelay();
                default -> spawner.getDelay();
            };
            return Timespan.fromTicks_i(delay);
        }
        return null;
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, ADD, REMOVE -> CollectionUtils.array(Timespan.class, Integer.class);
            case RESET -> CollectionUtils.array();
            default -> null;
        };
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        int changeValue = 0;
        if (delta != null) {
            changeValue += delta[0] instanceof Timespan timespan ? timespan.getTicks_i() : ((Integer) delta[0]);
        } else {
            changeValue = switch (action) {
                case TRUE -> DEFAULT_MAX_TICKS;
                case FALSE -> DEFAULT_MIN_TICKS;
                default -> 0;
            };
        }
        for (Block block : getExpr().getArray(event)) {
            if (block.getState() instanceof CreatureSpawner spawner) {
                int value = switch (action) {
                    case TRUE -> spawner.getMaxSpawnDelay();
                    case FALSE -> spawner.getMinSpawnDelay();
                    default -> spawner.getDelay();
                };
                switch (mode) {
                    case REMOVE -> value -= changeValue;
                    case ADD -> value += changeValue;
                    case SET, RESET -> value = changeValue;
                }
                value = Math.max(value, 0);
                switch (action) {
                    case TRUE -> {
                        if (value < spawner.getMinSpawnDelay())
                            spawner.setMinSpawnDelay(value);
                        spawner.setMaxSpawnDelay(value);
                    }
                    case FALSE -> {
                        if (value > spawner.getMaxSpawnDelay())
                            spawner.setMaxSpawnDelay(value);
                        spawner.setMinSpawnDelay(value);
                    }
                    default -> spawner.setDelay(value);
                }
                spawner.update();
            }
        }
    }

    @Override
    public Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Override
    protected String getPropertyName() {
        return switch (action) {
            case TRUE -> "maximum ";
            case FALSE -> "minimum ";
            default -> "";
        } + "spawn delay";
    }

}
