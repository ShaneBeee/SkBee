package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.other.type.OldItemFlag;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NoDoc
@Deprecated // deprecated on Feb 16/2024
public class ExprHiddenItemFlagsItem extends SimpleExpression<ItemType> {

    static {
        Skript.registerExpression(ExprHiddenItemFlagsItem.class, ItemType.class, ExpressionType.PROPERTY,
                "%itemtype% with all flag[s] hidden",
                "%itemtype% with %olditemflags% hidden",
                "%itemtype% with hidden %olditemflags%");
    }

    @SuppressWarnings("null")
    private boolean flagType;
    private Expression<ItemType> itemType;
    private Expression<OldItemFlag> itemFlag;

    @SuppressWarnings({"unchecked", "null", "NullableProblems"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, ParseResult parseResult) {
        this.itemType = (Expression<ItemType>) exprs[0];
        this.flagType = matchedPattern > 0;
        if (this.flagType) {
            this.itemFlag = (Expression<OldItemFlag>) exprs[1];
        }
        if (matchedPattern == 0) {
            Skript.warning("'" + parseResult.expr + "' is deprecated, please use new expression: '" +
                    "%itemtype% with all item[ ]flags'");
        } else {
            Skript.warning("'" + parseResult.expr + "' is deprecated, please use new expression: '" +
                    "%itemtype% with item[ ]flag[s] %itemflags%'");
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected ItemType @Nullable [] get(Event event) {
        ItemType item = this.itemType.getSingle(event);
        if (item == null) return null;

        item = item.clone();
        ItemMeta meta = item.getItemMeta();
        if (flagType) {
            for (OldItemFlag flag : this.itemFlag.getArray(event)) {
                meta.addItemFlags(flag.getBukkitItemFlag());
            }
        } else {
            meta.addItemFlags(ItemFlag.values());
        }

        item.setItemMeta(meta);
        return new ItemType[]{item};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String itemString = this.itemType.toString(e, d);
        if (this.flagType) {
            return itemString + " with " + this.itemFlag.toString(e, d) + " hidden";
        }
        return itemString + " with all flags hidden";
    }

}
