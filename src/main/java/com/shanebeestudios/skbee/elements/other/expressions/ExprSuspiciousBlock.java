package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.SuspiciousSand;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Suspicious Block - Item")
@Description({"Represents the item hiding in a Suspicious Block.",
        "As of Minecraft 1.19.4 the only block is Suspicious Sand."})
@Examples("set suspicious item of target block to a diamond")
@Since("INSERT VERSION")
public class ExprSuspiciousBlock extends SimplePropertyExpression<Block, ItemType> {

    static {
        if (Skript.classExists("org.bukkit.block.SuspiciousSand")) {
            register(ExprSuspiciousBlock.class, ItemType.class, "suspicious item", "blocks");
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public @Nullable ItemType convert(Block block) {
        BlockState state = block.getState();
        if (state instanceof SuspiciousSand suspiciousSand) {
            ItemStack item = suspiciousSand.getItem();
            if (item != null) return new ItemType(item);
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.DELETE)
            return CollectionUtils.array(ItemType.class);
        return null;
    }

    @SuppressWarnings({"UnstableApiUsage", "NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, Changer.ChangeMode mode) {
        ItemStack itemStack = null;
        if (delta != null && delta[0] instanceof ItemType itemType) itemStack = itemType.getRandom();

        for (Block block : getExpr().getArray(event)) {
            BlockState state = block.getState();
            if (state instanceof SuspiciousSand suspiciousSand) {
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
