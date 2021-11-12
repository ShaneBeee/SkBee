package tk.shanebee.bee.elements.structure.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.bee.api.structure.BlockStateBee;

import java.util.ArrayList;
import java.util.List;

@Name("BlockState - ItemType")
@Description("Represents the itemtype of a block in a structure. Requires MC 1.17.1+")
@Examples("set {_type} to itemtype of blockstate {_blockstate}")
@Since("INSERT VERSION")
public class ExprBlockStateItemType extends SimpleExpression<ItemType> {

    static {
        Skript.registerExpression(ExprBlockStateItemType.class, ItemType.class, ExpressionType.PROPERTY,
                "[item[ ]]type of [blockstate[s]] %blockstates%");
    }

    private Expression<BlockStateBee> blockState;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        blockState = (Expression<BlockStateBee>) exprs[0];
        return true;
    }

    @Nullable
    @Override
    protected ItemType[] get(Event e) {
        List<ItemType> itemTypes = new ArrayList<>();
        for (BlockStateBee blockState : blockState.getAll(e)) {
            itemTypes.add(blockState.getItemType());
        }
        return itemTypes.toArray(new ItemType[0]);
    }

    @Override
    public boolean isSingle() {
        return blockState.isSingle();
    }

    @Override
    public Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "itemtype of blockstate[s] " + blockState.toString(e,d);
    }

}
