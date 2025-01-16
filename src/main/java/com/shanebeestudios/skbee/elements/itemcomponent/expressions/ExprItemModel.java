package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registry.KeyUtils;
import com.shanebeestudios.skbee.api.util.ItemComponentUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("ItemComponent - Item Model")
@Description({"Represents the item model component of an item.",
    "See [**ItemModel**](https://minecraft.wiki/w/Data_component_format#item_model) on McWiki for more details.",
    "Requires Paper 1.21.3+",
    "",
    "**Changers**:",
    "`set` = Will set the item model of the item.",
    "`delete` = Will delete the item model of this item.",
    "`reset` = Will reset the item model back to the vanilla value."})
@Examples({"set {_model} to item model of player's tool",
    "set item model of player's tool to \"minecraft:diamond_sword\"",
    "set item model of {_item} to \"my_pack:some_cool_model\"",
    "reset item model of player's tool",
    "delete item model of {_item}"})
@Since("3.8.0")
@SuppressWarnings("UnstableApiUsage")
public class ExprItemModel extends SimplePropertyExpression<Object, String> {

    static {
        register(ExprItemModel.class, String.class, "item model", "itemstacks/itemtypes/slots");
    }

    @Override
    public @Nullable String convert(Object from) {
        ItemStack itemStack = ItemUtils.getItemStackFromObjects(from);
        if (itemStack != null && itemStack.hasData(DataComponentTypes.ITEM_MODEL)) {
            Key data = itemStack.getData(DataComponentTypes.ITEM_MODEL);
            if (data != null) return data.toString();
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, DELETE, RESET -> new Class[]{String.class};
            default -> null;
        };
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        String string = delta != null && delta[0] instanceof String s ? s : null;
        Key key = KeyUtils.getKey(string);

        ItemComponentUtils.modifyComponent(getExpr().getArray(event), mode, DataComponentTypes.ITEM_MODEL, key);
    }

    @Override
    protected String getPropertyName() {
        return "item model";
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

}
