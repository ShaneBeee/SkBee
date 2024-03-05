package com.shanebeestudios.skbee.elements.structure.expressions;

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
import com.shanebeestudios.skbee.api.structure.StructureWrapper;
import org.bukkit.block.BlockState;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("Structure - BlockStates")
@Description({"Get a list of the blockstates in a structure. This represents the palette of blocks a structure holds.",
        "Requires MC 1.17.1+"})
@Examples("set {_list::*} to blockstates of structure {_structure}")
@Since("1.12.3")
public class ExprStructureBlockStates extends SimpleExpression<BlockState> {

    static {
        Skript.registerExpression(ExprStructureBlockStates.class, BlockState.class, ExpressionType.PROPERTY,
                "blockstates of [structure] %structure%");
    }

    private Expression<StructureWrapper> structure;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        structure = (Expression<StructureWrapper>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Nullable
    @Override
    protected BlockState @Nullable [] get(Event event) {
        StructureWrapper structure = this.structure.getSingle(event);
        if (structure != null) {
            List<BlockState> blockStates = structure.getBlockStates();
            if (blockStates != null) {
                return blockStates.toArray(new BlockState[0]);
            }
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<? extends BlockState> getReturnType() {
        return BlockState.class;
    }

    @SuppressWarnings({"NullableProblems", "DataFlowIssue"})
    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "blockstates of structure " + structure.toString(e, d);
    }
}
