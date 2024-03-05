package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.BlockDataUtils;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("BlockData - Item BlockData Tag")
@Description("Get/set the value of a tag in the BlockData of an item.")
@Examples("")
@Since("INSERT VERSION")
public class ExprBlockDataItemTag extends PropertyExpression<ItemType, Object> {

    static {
        PropertyExpression.register(ExprBlockDataItemTag.class, Object.class,
                "item [block[ ]](data|state) tag %string%", "itemtypes");
    }

    private Expression<String> tag;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.tag = (Expression<String>) exprs[matchedPattern];
        setExpr((Expression<ItemType>) exprs[1 - matchedPattern]);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Object[] get(Event event, ItemType[] source) {
        return get(source, itemType -> {
            if (!(itemType.getItemMeta() instanceof BlockDataMeta meta)) return null;

            Material blockForm = BlockDataUtils.getBlockForm(itemType.getMaterial());
            if (!blockForm.isBlock()) return null;

            BlockData blockData = meta.getBlockData(blockForm);

            return BlockDataUtils.getBlockDataValueFromTag(blockData, this.tag.getSingle(event));
        });
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Object.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta == null) return;
        for (ItemType itemType : getExpr().getAll(event)) {
            Material blockForm = BlockDataUtils.getBlockForm(itemType.getMaterial());
            BlockDataMeta itemMeta = (BlockDataMeta) itemType.getItemMeta();
            BlockData oldBlockData;
            if (itemMeta.hasBlockData()) {
                oldBlockData = itemMeta.getBlockData(blockForm);
            } else {
                oldBlockData = blockForm.createBlockData();
            }
            BlockData newBlockData = BlockDataUtils.setBlockDataTag(oldBlockData, this.tag.getSingle(event), delta[0]);
            itemMeta.setBlockData(newBlockData);
            itemType.setItemMeta(itemMeta);
        }
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return Object.class;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "item blockdata tag " + this.tag.toString(e, d) + " of " + getExpr().toString(e, d);
    }

}
