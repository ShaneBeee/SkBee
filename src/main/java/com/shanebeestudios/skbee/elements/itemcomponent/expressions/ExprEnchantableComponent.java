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
import io.papermc.paper.datacomponent.item.Enchantable;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("ItemComponent - Enchantable")
@Description({"If present, and applicable enchantments are available, items with the component can be enchanted in an enchanting table.",
    "Positive integer representing the item's enchantability. A higher value allows enchantments with a higher cost to be picked.",
    "Requires Paper 1.21.3+ and `item_component` feature.",
    "See [**Enchantable Component**](https://minecraft.wiki/w/Data_component_format#enchantable) on McWiki for more details.",
    "",
    "**Changers**:",
    "- `set` = Allows you to override the glint.",
    "- `reset` = Reset back to default state.",
    "- `delete` = Will delete any value (vanilla or not)."})
@Examples({"set enchantable component of player's tool to 10",
    "delete enchantable component of player's tool",
    "reset enchantable component of {_item}"})
@Since("3.8.0")
@SuppressWarnings("UnstableApiUsage")
public class ExprEnchantableComponent extends SimplePropertyExpression<Object, Number> {

    static {
        register(ExprEnchantableComponent.class, Number.class,
            "enchantable component", "itemstacks/itemtypes/slots");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Number convert(Object from) {
        ItemStack itemStack = ItemUtils.getItemStackFromObjects(from);
        if (itemStack != null && itemStack.hasData(DataComponentTypes.ENCHANTABLE)) {
            Enchantable data = itemStack.getData(DataComponentTypes.ENCHANTABLE);
            if (data != null) return data.value();
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET -> CollectionUtils.array(Number.class);
            case DELETE, RESET -> CollectionUtils.array();
            default -> null;
        };
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        int value = delta != null && delta[0] instanceof Number number ? number.intValue() : 0;
        Enchantable enchantable = Enchantable.enchantable(Math.max(1, value));

        ItemComponentUtils.modifyComponent(getExpr().getArray(event), mode, DataComponentTypes.ENCHANTABLE, enchantable);
    }

    @Override
    protected String getPropertyName() {
        return "enchantable component";
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

}
