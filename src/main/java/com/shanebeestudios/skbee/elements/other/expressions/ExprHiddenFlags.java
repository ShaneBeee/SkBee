package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Name("Hidden Item Flags")
@Description("Hides the item flags on items, allowing you to make super duper custom items.")
@Examples({"set player's tool to player's tool with attributes flag hidden",
        "give player 1 of diamond sword of sharpness 5 with hidden enchants flag",
        "set {_tool} to player's tool with all flags hidden",
        "give player potion of harming with hidden potion effects flag",
        "set {_b} to leather boots with dye flag hidden",
        "set {_i} to diamond sword of unbreaking 3 with all flags hidden",
        "set {_i} to unbreakable netherite pickaxe with hidden unbreakable flag",
        "set {_i} to unbreakable diamond sword of sharpness 3 with unbreakable flag and enchants flag hidden"})
@Since("1.0.0")
public class ExprHiddenFlags extends SimpleExpression<ItemType> {

    static {
        Skript.registerExpression(ExprHiddenFlags.class, ItemType.class, ExpressionType.PROPERTY,
                "%itemtype% with all flag[s] hidden",
                "%itemtype% with %itemflags% hidden",
                "%itemtype% with hidden %itemflags%");
    }

    @SuppressWarnings("null")
    private boolean flagType;
    private Expression<ItemType> itemType;
    private Expression<ItemFlag> itemFlag;

    @SuppressWarnings({"unchecked", "null", "NullableProblems"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, ParseResult parseResult) {
        this.itemType = (Expression<ItemType>) exprs[0];
        this.flagType = matchedPattern > 0;
        if (this.flagType) {
            this.itemFlag = (Expression<ItemFlag>) exprs[1];
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable ItemType[] get(Event event) {
        ItemType item = this.itemType.getSingle(event);
        if (item == null) return null;

        ItemMeta meta = item.getItemMeta();
        if (flagType) {
            for (ItemFlag flag : this.itemFlag.getArray(event)) {
                meta.addItemFlags(flag);
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
