package com.shanebeestudios.skbee.elements.structure.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.structure.StructureWrapper;
import org.bukkit.block.BlockState;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExprStructureBlockStates extends SimpleExpression<BlockState> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprStructureBlockStates.class, BlockState.class,
                "blockstates of [structure] %structure%")
            .name("Structure - BlockStates")
            .description("Get a list of the blockstates in a structure. This represents the palette of blocks a structure holds.")
            .examples("set {_list::*} to blockstates of structure {_structure}")
            .since("1.12.3")
            .register();
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
