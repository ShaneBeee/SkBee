package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.block.BlockState;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprBlockStateItemType extends SimplePropertyExpression<BlockState, ItemType> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprBlockStateItemType.class, ItemType.class,
                "block[ ]state [item[ ]]type of %blockstates%")
            .name("BlockState - ItemType")
            .description("Represents the itemtype of a block state.")
            .examples("set {_type} to blockstate itemtype of {_blockstate}",
                "set blockstate itemtype of {_blockstate} to stone")
            .since("2.13.0")
            .register();
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
