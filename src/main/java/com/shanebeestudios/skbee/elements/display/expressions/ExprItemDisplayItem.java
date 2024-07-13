package com.shanebeestudios.skbee.elements.display.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.elements.display.types.Types;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Display Item")
@Description({"Represents the display ItemType of an Item/Block Display Entity", Types.McWIKI})
@Examples({"set display item of {_display} to diamond sword",
    "set display item of {_display} to air",
    "delete display item of {_display}"})
@Since("2.8.0")
public class ExprItemDisplayItem extends SimplePropertyExpression<Entity, ItemType> {

    static {
        register(ExprItemDisplayItem.class, ItemType.class, "display item[[ ]type]", "entities");
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public @Nullable ItemType convert(Entity entity) {
        ItemType itemType = null;
        if (entity instanceof ItemDisplay itemDisplay) {
            ItemStack itemStack = itemDisplay.getItemStack();
            if (itemStack != null) itemType = new ItemType(itemStack);
        } else if (entity instanceof BlockDisplay blockDisplay) {
            itemType = new ItemType(blockDisplay.getBlock().getMaterial());
        }
        return itemType;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE) return CollectionUtils.array(ItemType.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        ItemType itemType = (delta != null && delta[0] instanceof ItemType it) ? it : null;
        ItemStack itemStack = itemType != null ? itemType.getRandom() : null;
        BlockData blockData = Material.AIR.createBlockData();
        if (itemType != null) {
            Material material = itemType.getMaterial();
            if (material.isBlock()) blockData = material.createBlockData();
        }
        for (Entity entity : getExpr().getArray(event)) {
            if (entity instanceof ItemDisplay itemDisplay) {
                itemDisplay.setItemStack(itemStack);
            } else if (entity instanceof BlockDisplay blockDisplay) {
                blockDisplay.setBlock(blockData);
            }
        }
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "display item";
    }

}
