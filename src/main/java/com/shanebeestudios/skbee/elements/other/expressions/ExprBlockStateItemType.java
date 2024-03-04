package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.BlockState;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("BlockState - ItemType")
@Description("Represents the itemtype of a block state.")
@Examples({"set {_type} to blockstate itemtype of {_blockstate}",
        "set blockstate itemtype of {_blockstate} to stone"})
@Since("2.13.0")
public class ExprBlockStateItemType extends SimplePropertyExpression<BlockState, ItemType> {

    static {
        Skript.registerExpression(ExprBlockStateItemType.class, ItemType.class, ExpressionType.PROPERTY,
                "block[ ]state [item[ ]]type of %blockstates%");
    }

    @Override
    public @Nullable ItemType convert(BlockState blockState) {
        // TODO use blockdata when Skript updates to 2.8.4 (See my PR)
        return new ItemType(blockState.getType());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(ItemType.class);
        return null;
    }

    @SuppressWarnings({"ConstantValue", "NullableProblems"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof ItemType itemType) {
            for (BlockState blockState : getExpr().getArray(event)) {
                blockState.setType(itemType.getMaterial());
            }
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "blockstate itemtype";
    }

}
