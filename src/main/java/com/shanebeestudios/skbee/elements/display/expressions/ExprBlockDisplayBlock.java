package com.shanebeestudios.skbee.elements.display.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.elements.display.types.Types;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Block Display Blockdata")
@Description({"Represents the block data of a Block Display Entity.", Types.McWIKI})
@Examples("set display block data of {_display} to oak_stairs[facing=south]")
@Since("2.8.0")
public class ExprBlockDisplayBlock extends SimplePropertyExpression<Entity, BlockData> {

    static {
        register(ExprBlockDisplayBlock.class, BlockData.class, "display block[ ]data", "entities");
    }

    @Override
    public @Nullable BlockData convert(Entity entity) {
        if (entity instanceof BlockDisplay blockDisplay) return blockDisplay.getBlock();
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(BlockData.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof BlockData blockData) {
            for (Entity entity : getExpr().getArray(event)) {
                if (entity instanceof BlockDisplay blockDisplay) {
                    blockDisplay.setBlock(blockData);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends BlockData> getReturnType() {
        return BlockData.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "display block data";
    }

}
