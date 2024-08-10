package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("ItemComponent - Enchantment Glint Override")
@Description({"Represents the enchantment glint override of an item (Requires Minecraft 1.20.5+).",
    "Overrides the enchantment glint effect on an item. When `true`, the item will display a glint, even without enchantments.",
    "When `false`, the item will not display a glint, even with enchantments.",
    "**Note**: If no override is applied, will return null.",
    "**Changers**:",
    "- `set` = Allows you to override the glint.",
    "- `reset` = Reset back to default state."})
@Examples({"set glint override of player's tool to true",
    "set glint override of player's tool to false"})
@Since("3.6.0")
public class ExprEnchantmentGlintOverride extends SimplePropertyExpression<ItemType, Boolean> {

    static {
        if (Skript.methodExists(ItemMeta.class, "getEnchantmentGlintOverride")) {
            register(ExprEnchantmentGlintOverride.class, Boolean.class, "[enchantment] glint [override]", "itemtypes");
        }
    }

    @Override
    public @Nullable Boolean convert(ItemType itemType) {
        ItemMeta itemMeta = itemType.getItemMeta();
        if (!itemMeta.hasEnchantmentGlintOverride()) return null;
        return itemMeta.getEnchantmentGlintOverride();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Boolean.class);
        else if (mode == ChangeMode.RESET) return CollectionUtils.array();
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Boolean glint = delta != null && delta[0] instanceof Boolean bool ? bool : null;
        for (ItemType itemType : getExpr().getArray(event)) {
            ItemMeta itemMeta = itemType.getItemMeta();
            itemMeta.setEnchantmentGlintOverride(glint);
            itemType.setItemMeta(itemMeta);
        }
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "enchantment glint override";
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

}
