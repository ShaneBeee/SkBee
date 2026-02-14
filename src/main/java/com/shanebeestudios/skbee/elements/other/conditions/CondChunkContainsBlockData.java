package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.conditions.base.PropertyCondition.PropertyType;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Chunk;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CondChunkContainsBlockData extends Condition {

    public static void register(Registration reg) {
        reg.newCondition(CondChunkContainsBlockData.class,
                "%chunks% (has|have) block[ ]data %blockdata%",
                "%chunks% (doesn't|does not|do not|don't) have block[ ]data %blockdata%")
            .name("Chunk - Contains BlockData")
            .description("Check if a chunk contains a specific block data.",
                "This can be useful to check before looping blocks in a chunk.")
            .examples("if chunk at player contains block data sand[]:")
            .since("2.5.2")
            .register();
    }

    private Expression<Chunk> chunks;
    private Expression<BlockData> blockData;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.chunks = (Expression<Chunk>) exprs[0];
        this.blockData = (Expression<BlockData>) exprs[1];
        setNegated(matchedPattern == 1);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean check(Event event) {
        return chunks.check(event, chunk -> blockData.check(event, chunk::contains), isNegated());
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return PropertyCondition.toString(this, PropertyType.HAVE, e, d,
            chunks, "blockdata " + this.blockData.toString(e, d));
    }

}
