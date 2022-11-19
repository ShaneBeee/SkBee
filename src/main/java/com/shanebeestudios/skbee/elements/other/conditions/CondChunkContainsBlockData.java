package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.conditions.base.PropertyCondition.PropertyType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Chunk;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Chunk - Contains BlockData")
@Description({"Check if a chunk contains a specific block data.",
        "This can be useful to check before looping blocks in a chunk."})
@Examples("if chunk at player contains block data sand[]:")
@Since("2.5.2")
public class CondChunkContainsBlockData extends Condition {

    static {
        Skript.registerCondition(CondChunkContainsBlockData.class,
                "%chunks% (has|have) block[ ]data %blockdata%",
                "%chunks% (doesn't|does not|do not|don't) have block[ ]data %blockdata%");
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
