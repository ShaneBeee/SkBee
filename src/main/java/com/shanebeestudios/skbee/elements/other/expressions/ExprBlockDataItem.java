package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.BlockDataUtils;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("BlockData - Item BlockData")
@Description("Get/set the BlockData that is attached to an item.")
@Examples({"set {_item} to a campfire",
        "set item blockdata of {_item} to campfire[lit=false]"})
@Since("INSERT VERSION")
public class ExprBlockDataItem extends SimplePropertyExpression<ItemType, BlockData> {

    static {
        PropertyExpression.register(ExprBlockDataItem.class, BlockData.class,
                "item [block[ ]](data|state)", "itemtypes");
    }

    @Override
    public @Nullable BlockData convert(ItemType itemType) {
        if (itemType.getItemMeta() instanceof BlockDataMeta meta) {
            Material blockForm = BlockDataUtils.getBlockForm(itemType.getMaterial());
            if (blockForm.isBlock()) return meta.getBlockData(blockForm);
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(BlockData.class);
        }
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof BlockData blockData) {
            for (ItemType itemType : getExpr().getAll(event)) {
                if (itemType.getItemMeta() instanceof BlockDataMeta itemMeta) {
                    itemMeta.setBlockData(blockData);
                    itemType.setItemMeta(itemMeta);
                }
            }
        }
    }

    @Override
    public @NotNull Class<BlockData> getReturnType() {
        return BlockData.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "item blockdata";
    }

}
