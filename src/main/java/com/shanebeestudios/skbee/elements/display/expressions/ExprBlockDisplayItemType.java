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
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Block Display Item Type")
@Description({"Represents the block type of a Block Display Entity.",
        "I HIGHLY recommend not using this, use the block data method instead.", Types.McWIKI})
@Examples("set display block item type of {_omgPleaseStopUsingItemTypesForThisPleaseUseBlockDataInstead} to diamond ore")
@Since("INSERT VERSION")
public class ExprBlockDisplayItemType extends SimplePropertyExpression<Entity, ItemType> {

    private static final BlockData STONE = Material.STONE.createBlockData();

    static {
        register(ExprBlockDisplayItemType.class, ItemType.class,
                "display block item[ ]type", "entities");
    }

    @Override
    public @Nullable ItemType convert(Entity entity) {
        if (entity instanceof BlockDisplay blockDisplay) {
            return new ItemType(blockDisplay.getBlock().getMaterial());
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(ItemType.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof ItemType itemType) {
            Material material = itemType.getMaterial();
            BlockData blockData = material.isBlock() ? material.createBlockData() : STONE;
            for (Entity entity : getExpr().getArray(event)) {
                if (entity instanceof BlockDisplay blockDisplay) {
                    blockDisplay.setBlock(blockData);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "display block item type";
    }

}
