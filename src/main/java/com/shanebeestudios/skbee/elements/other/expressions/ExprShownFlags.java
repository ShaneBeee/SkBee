package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Name("Show Item Flags")
@Description("Shows the item flags on items, allowing you to make super duper custom items. Dye item flag added in 1.5.0 and only available on 1.16.2+.")
@Examples({
        "set {_item} to unbreakable diamond sword of sharpness 10 named \"Pointy\" with all flags hidden with enchant flag shown"
})
@Since("INSERT VERSION")
public class ExprShownFlags extends SimplePropertyExpression<ItemType, ItemType> {

    private static final String flags = "[(0¦all|1¦enchant[s]|2¦destroy[s]|3¦potion[ ]effect[s]|4¦unbreakable|5¦attribute[s]|6¦dye|7¦placed on)]";

    static {
        Skript.registerExpression(ExprShownFlags.class, ItemType.class, ExpressionType.PROPERTY,
                "%itemtype% with " + flags + " flag[s] shown",
                "%itemtype% with shown " + flags + " flag[s]");
    }

    @SuppressWarnings("null")
    private int parse;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<ItemType>) exprs[0]);
        parse = parseResult.mark;
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    @Nullable
    public ItemType convert(@NotNull ItemType item) {
        if (item == null) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        switch (parse) {
            case 0 -> {
                meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.removeItemFlags(ItemFlag.HIDE_DESTROYS);
                meta.removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                meta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                meta.removeItemFlags(ItemFlag.HIDE_DYE);
                meta.removeItemFlags(ItemFlag.HIDE_PLACED_ON);
            }
            case 1 -> meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            case 2 -> meta.removeItemFlags(ItemFlag.HIDE_DESTROYS);
            case 3 -> meta.removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            case 4 -> meta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            case 5 -> meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            case 6 -> meta.removeItemFlags(ItemFlag.HIDE_DYE);
            case 7 -> meta.removeItemFlags(ItemFlag.HIDE_PLACED_ON);
        }

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "Show Item Flags";
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String[] flags = new String[]{"all", "enchant", "destroy", "potion effect", "unbreakable", "attribute", "dye", "placed on"};
        return getExpr().toString(e, d) + " with " + flags[parse] + " flags shown";
    }

}
