package com.shanebeestudios.skbee.elements.generator.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.generator.event.BlockPopulateEvent;
import com.shanebeestudios.skbee.api.generator.event.ChunkGenEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("ChunkGenerator - ChunkData X/Z")
@Description({"Represents the ChunkData's X/Z coordinates.",
        "This will typically be used to calculate world position from chunk position for your noise system."})
@Examples({"chunk gen:",
        "\tloop 16 times:",
        "\t\tloop 16 times:",
        "\t\t\tset {_x} to (loop-number-1) - 1",
        "\t\t\tset {_z} to (loop-number-2) - 1",
        "\t\t\tset {_noise} to getNoise({_x} + (16 * chunkdata chunk x), {_z} + (16 * chunkdata chunk z))"})
@Since("3.5.0")
public class ExprChunkDataXZ extends SimpleExpression<Number> {

    static {
        Skript.registerExpression(ExprChunkDataXZ.class, Number.class, ExpressionType.SIMPLE,
                "chunk[ ]data chunk (:x|z)");
    }

    private boolean x;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(ChunkGenEvent.class, BlockPopulateEvent.class)) {
            Skript.error("'" + parseResult.expr + "' can only be used in chunk gen/block pop sections.");
            return false;
        }
        this.x = parseResult.hasTag("x");
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Number @Nullable [] get(Event event) {
        if (event instanceof ChunkGenEvent genEvent) {
            return new Number[]{this.x ? genEvent.getChunkX() : genEvent.getChunkZ()};
        } else if (event instanceof BlockPopulateEvent popEvent) {
            return new Number[]{this.x ? popEvent.getChunkX() : popEvent.getChunkZ()};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "chunkdata chunk " + (this.x ? "x" : "z");
    }

}
