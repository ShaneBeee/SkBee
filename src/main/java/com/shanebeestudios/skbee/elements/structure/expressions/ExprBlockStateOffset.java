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
import com.shanebeestudios.skbee.api.structure.BlockStateBee;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("BlockState - Offset")
@Description({"Represents the offset of a block in a structure. This is a vector, distance from the starting block of a structure.",
        "Requires MC 1.17.1+"})
@Examples("set {_offset} to offset of blockstate {_blockstate}")
@Since("1.12.3")
public class ExprBlockStateOffset extends SimpleExpression<Vector> {

    static {
        Skript.registerExpression(ExprBlockStateOffset.class, Vector.class, ExpressionType.PROPERTY,
                "offset of [blockstate[s]] %blockstates%");
    }

    private Expression<BlockStateBee> blockstate;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        blockstate = (Expression<BlockStateBee>) exprs[0];
        return true;
    }

    @Nullable
    @Override
    protected Vector[] get(Event e) {
        List<Vector> offsets = new ArrayList<>();
        for (BlockStateBee blockState : blockstate.getAll(e)) {
            offsets.add(blockState.getOffset());
        }
        return offsets.toArray(new Vector[0]);
    }

    @Override
    public boolean isSingle() {
        return blockstate.isSingle();
    }

    @Override
    public Class<? extends Vector> getReturnType() {
        return Vector.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "offset of blockstate " + blockstate.toString(e,d);
    }

}
