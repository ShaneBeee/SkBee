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
import com.shanebeestudios.skbee.api.structure.StructureBee;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Structure - BlockStates")
@Description({"Get a list of the blockstates in a structure. This represents the palette of blocks a structure holds.",
        "Requires MC 1.17.1+"})
@Examples("set {_list::*} to blockstates of structure {_structure}")
@Since("1.12.3")
public class ExprStructureBlockStates extends SimpleExpression<BlockStateBee> {

    static {
        Skript.registerExpression(ExprStructureBlockStates.class, BlockStateBee.class, ExpressionType.PROPERTY,
                "blockstates of [structure] %structure%");
    }

    private Expression<StructureBee> structure;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        structure = (Expression<StructureBee>) exprs[0];
        return true;
    }

    @Nullable
    @Override
    protected BlockStateBee[] get(Event e) {
        StructureBee structure = this.structure.getSingle(e);
        if (structure != null) {
            List<BlockStateBee> blocks = new ArrayList<>();
            structure.getBukkitStructure().getPalettes().get(0).getBlocks().forEach(state -> blocks.add(new BlockStateBee(state)));
            return blocks.toArray(new BlockStateBee[0]);
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends BlockStateBee> getReturnType() {
        return BlockStateBee.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "blockstates of structure " + structure.toString(e,d);
    }
}
