package com.shanebeestudios.skbee.elements.structure.expressions;

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
import com.shanebeestudios.skbee.api.structure.BlockStateBee;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("BlockState - ItemType")
@Description("Represents the itemtype of a block in a structure. Requires MC 1.17.1+")
@Examples("set {_type} to itemtype of blockstate {_blockstate}")
@Since("1.12.3")
public class ExprBlockStateItemType extends SimpleExpression<ItemType> {

    static {
        Skript.registerExpression(ExprBlockStateItemType.class, ItemType.class, ExpressionType.PROPERTY,
                "[item[ ]]type of [blockstate[s]] %blockstates%");
    }

    private Expression<BlockStateBee> blockState;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        blockState = (Expression<BlockStateBee>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Nullable
    @Override
    protected ItemType[] get(Event event) {
        List<ItemType> itemTypes = new ArrayList<>();
        for (BlockStateBee blockState : blockState.getAll(event)) {
            itemTypes.add(blockState.getItemType());
        }
        return itemTypes.toArray(new ItemType[0]);
    }

    @Override
    public boolean isSingle() {
        return blockState.isSingle();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "itemtype of blockstate[s] " + blockState.toString(e, d);
    }

}
