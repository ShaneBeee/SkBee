package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.ItemComponentUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
@Name("ItemComponent - Enchantment Glint Override")
@Description({"Represents the enchantment glint override of an item. ",
    "Requires Minecraft 1.20.5+",
    "Overrides the enchantment glint effect on an item.",
    "When `true`, the item will display a glint, even without enchantments.",
    "When `false`, the item will not display a glint, even with enchantments.",
    "**Note**: If no override is applied, will return null.",
    "See [**EnchantmentGlintOverride**](https://minecraft.wiki/w/Data_component_format#enchantment_glint_override) on McWiki for more details.",
    "Requires Paper 1.21.3+",
    "",
    "**Changers**:",
    "- `set` = Allows you to override the glint.",
    "- `reset` = Reset back to default state.",
    "- `delete` = Will delete any value (vanilla or not)."})
@Examples({"set glint override of player's tool to true",
    "set glint override of player's tool to false"})
@Since("3.6.0")
public class ExprEnchantmentGlintOverride extends SimplePropertyExpression<Object, Boolean> {

    static {
        register(ExprEnchantmentGlintOverride.class, Boolean.class,
            "[enchantment] glint [override]", "itemstacks/itemtypes/slots");
    }

    @Override
    public @Nullable Boolean convert(Object object) {
        ItemStack itemStack = ItemUtils.getItemStackFromObjects(object);
        if (itemStack != null && itemStack.hasData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE)) {
            return itemStack.getData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Boolean.class);
        else if (mode == ChangeMode.RESET || mode == ChangeMode.DELETE) return CollectionUtils.array();
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Boolean glint = delta != null && delta[0] instanceof Boolean bool ? bool : null;

        ItemComponentUtils.modifyComponent(getExpr().getArray(event),  mode, DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, glint);
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
