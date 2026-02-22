package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrushableBlock;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprSuspiciousBlock extends SimplePropertyExpression<Block, ItemType> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprSuspiciousBlock.class, ItemType.class, "suspicious item", "blocks")
            .name("Suspicious Block - Item")
            .description("Represents the item hiding in a Suspicious Block. Requires Minecraft 1.19.4+")
            .examples("set suspicious item of target block to a diamond",
                "delete suspicious item of block at location(199,10,-199, \"very_goodNames\")")
            .since("2.8.1, 2.14.0 (suspicious gravel)")
            .register();
    }

    @Override
    public @Nullable ItemType convert(Block block) {
        BlockState state = block.getState();
        ItemStack itemStack = null;
        if (state instanceof BrushableBlock brushableBlock) {
            itemStack = brushableBlock.getItem();
        }
        if (itemStack != null && !itemStack.getType().isAir()) return new ItemType(itemStack);
        return null;
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE)
            return CollectionUtils.array(ItemType.class);
        return null;
    }

    @SuppressWarnings({"ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        ItemStack itemStack = null;
        if (delta != null && delta[0] instanceof ItemType itemType) itemStack = itemType.getRandom();

        for (Block block : getExpr().getArray(event)) {
            BlockState state = block.getState();
            if (state instanceof BrushableBlock brushableBlock) {
                brushableBlock.setItem(itemStack);
                brushableBlock.update();
            }
        }
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "suspicious item";
    }

}
