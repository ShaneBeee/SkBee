package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrushableBlock;
import org.bukkit.block.SuspiciousSand;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Name("Suspicious Block - Item")
@Description({"Represents the item hiding in a Suspicious Block. Requires Minecraft 1.19.4+"})
@Examples({"set suspicious item of target block to a diamond",
        "delete suspicious item of block at location(199,10,-199, \"very_goodNames\")"})
@Since("2.8.1, INSERT VERSION (suspicious gravel)")
public class ExprSuspiciousBlock extends SimplePropertyExpression<Block, ItemType> {

    private static final boolean HAS_SUSPICIOUS_SAND = Skript.classExists("org.bukkit.block.SuspiciousSand");
    private static final boolean HAS_BRUSHABLE_BLOCK = Skript.classExists("org.bukkit.block.BrushableBlock");

    static {
        if (HAS_BRUSHABLE_BLOCK || HAS_SUSPICIOUS_SAND) {
            register(ExprSuspiciousBlock.class, ItemType.class, "suspicious item", "blocks");
        }
    }

    @SuppressWarnings({"deprecation"})
    @Override
    public @Nullable ItemType convert(Block block) {
        BlockState state = block.getState();
        ItemStack itemStack = null;
        if (HAS_BRUSHABLE_BLOCK && state instanceof BrushableBlock brushableBlock) {
            itemStack = brushableBlock.getItem();
        } else if (HAS_SUSPICIOUS_SAND && state instanceof SuspiciousSand suspiciousSand) {
            itemStack = suspiciousSand.getItem();
        }
        if (itemStack != null && !itemStack.getType().isAir()) return new ItemType(itemStack);
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE)
            return CollectionUtils.array(ItemType.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue", "deprecation"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        ItemStack itemStack = null;
        if (delta != null && delta[0] instanceof ItemType itemType) itemStack = itemType.getRandom();

        for (Block block : getExpr().getArray(event)) {
            BlockState state = block.getState();
            if (HAS_BRUSHABLE_BLOCK && state instanceof BrushableBlock brushableBlock) {
                brushableBlock.setItem(itemStack);
                brushableBlock.update();
            } else if (HAS_SUSPICIOUS_SAND && state instanceof SuspiciousSand suspiciousSand) {
                suspiciousSand.setItem(itemStack);
                suspiciousSand.update();
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
