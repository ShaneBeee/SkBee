package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.util.Timespan;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import org.bukkit.Chunk;
import org.jetbrains.annotations.Nullable;

public class ExprChunkInhabitedTime extends SimplePropertyExpression<Chunk, Timespan> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprChunkInhabitedTime.class, Timespan.class,
                "inhabited time", "chunks")
            .name("Chunk - Inhabited Time")
            .description("Get/set the time a chunk has been inhabited.",
                "Note that the time is incremented once per tick per player within mob spawning distance of this chunk.")
            .examples("if inhabited time of chunk at player > 10 minutes:",
                "set inhabited time of chunk at player to 1 day",
                "add 10 minutes to inhabited time of {_chunks::*}",
                "remove 3 minutes from inhabited time of {_c}")
            .since("INSERT VERSION")
            .register();
    }

    @Override
    public @Nullable Timespan convert(Chunk chunk) {
        return new Timespan(Timespan.TimePeriod.TICK, chunk.getInhabitedTime());
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, ADD, REMOVE -> CollectionUtils.array(Timespan.class);
            default -> null;
        };
    }

    @Override
    public void change(Chunk chunk, Object @Nullable [] delta, ChangeMode mode) {
        if (delta == null || delta.length == 0 || !(delta[0] instanceof Timespan timespan)) {
            return;
        }

        long oldValue = chunk.getInhabitedTime();
        long changeValue = timespan.getAs(Timespan.TimePeriod.TICK);
        long newValue = switch (mode) {
            case ADD -> oldValue + changeValue;
            case REMOVE -> oldValue - changeValue;
            default -> changeValue;
        };
        chunk.setInhabitedTime(newValue);
    }

    @Override
    public Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Override
    protected String getPropertyName() {
        return "inhabited time";
    }

}
