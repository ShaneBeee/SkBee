package tk.shanebee.bee.elements.structure.expressions;

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
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.bee.api.structure.BlockStateBee;

import java.util.ArrayList;
import java.util.List;

@Name("BlockState - BlockData")
@Description("Represents the blockdata of a block in a structure. Requires MC 1.17.1+")
@Examples("set {_data} to blockdata of blockstate {_blockstate}")
@Since("INSERT VERSION")
public class ExprBlockStateBlockData extends SimpleExpression<BlockData> {

    static {
        Skript.registerExpression(ExprBlockStateBlockData.class, BlockData.class, ExpressionType.PROPERTY,
                "block[ ]data of [blockstate[s]] %blockstates%");
    }

    private Expression<BlockStateBee> blockState;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        blockState = (Expression<BlockStateBee>) exprs[0];
        return true;
    }

    @Nullable
    @Override
    protected BlockData[] get(Event e) {
        List<BlockData> blockDatas = new ArrayList<>();
        for (BlockStateBee blockState : blockState.getAll(e)) {
            blockDatas.add(blockState.getBlockData());
        }
        return blockDatas.toArray(new BlockData[0]);
    }

    @Override
    public boolean isSingle() {
        return blockState.isSingle();
    }

    @Override
    public Class<? extends BlockData> getReturnType() {
        return BlockData.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "blockdata of blockstate[s] " + blockState.toString(e,d);
    }

}
