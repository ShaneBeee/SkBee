package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.block.BlockState;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("BlockState - Offset")
@Description({"Represents the offset of a blockstate in a structure.",
        "This is a vector, distance from the starting block of a structure."})
@Examples("set {_offset} to blockstate offset of {_blockstate}")
@Since("2.13.0")
public class ExprBlockStateOffset extends SimpleExpression<Vector> {

    static {
        Skript.registerExpression(ExprBlockStateOffset.class, Vector.class, ExpressionType.PROPERTY,
                "block[ ]state offset[s] of %blockstates%");
    }

    private Expression<BlockState> blockstate;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        blockstate = (Expression<BlockState>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Nullable
    @Override
    protected Vector[] get(Event event) {
        List<Vector> offsets = new ArrayList<>();
        for (BlockState blockState : blockstate.getAll(event)) {
            offsets.add(blockState.getLocation().toVector());
        }
        return offsets.toArray(new Vector[0]);
    }

    @Override
    public boolean isSingle() {
        return blockstate.isSingle();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<? extends Vector> getReturnType() {
        return Vector.class;
    }

    @SuppressWarnings({"NullableProblems", "DataFlowIssue"})
    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "blockstate offset of " + blockstate.toString(e, d);
    }

}
