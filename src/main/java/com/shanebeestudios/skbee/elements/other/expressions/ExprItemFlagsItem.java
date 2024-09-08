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
import com.shanebeestudios.skbee.api.util.ItemUtils;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("ItemFlag - Item with ItemFlags")
@Description({"Get an item with ItemFlags.",
    "As of Minecraft 1.20.5, the `hide attributes` flag doesn't work as intended. ",
    "See [**SkBee Wiki**](https://github.com/ShaneBeee/SkBee/wiki/Tricks-Hide-Attribute-Modifiers) for more info."})
@Examples({"set {_sword} to diamond sword with all item flags",
    "set {_sword} to diamond sword of sharpness 3 with hide enchants item flag",
    "set {_sword} to diamond sword of sharpness 3 with item flag hide enchants",
    "give player fishing rod of lure 10 with item flag hide enchants",
    "give player potion of extended regeneration with hide enchants itemflag",
    "give player netherite leggings with itemflag hide attributes"})
@Since("3.4.0")
public class ExprItemFlagsItem extends SimpleExpression<ItemType> {

    static {
        Skript.registerExpression(ExprItemFlagsItem.class, ItemType.class, ExpressionType.COMBINED,
            "%itemtype% with all item[ ]flags",
            "%itemtype% with item[ ]flag[s] %itemflags%",
            "%itemtype% with %itemflags% item[ ]flag[s]");
    }

    private int pattern;
    private Expression<ItemType> itemType;
    private Expression<ItemFlag> itemFlags;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;
        this.itemType = (Expression<ItemType>) exprs[0];
        if (matchedPattern > 0) {
            this.itemFlags = (Expression<ItemFlag>) exprs[1];
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected ItemType @Nullable [] get(Event event) {
        ItemType itemType = this.itemType.getSingle(event);
        ItemFlag[] flags = this.pattern == 0 ? ItemFlag.values() : this.itemFlags.getArray(event);
        if (itemType == null) return null;

        itemType = itemType.clone();
        ItemUtils.modifyItemMeta(itemType, itemMeta -> itemMeta.addItemFlags(flags));

        return new ItemType[]{itemType};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        if (this.pattern == 0) return this.itemType.toString(e, d) + " with all item flags";
        return this.itemType.toString(e, d) + " with item flag[s] " + this.itemFlags.toString(e, d);
    }

}
