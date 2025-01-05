package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("ItemComponent - Tooltip Style")
@Description({"The key of the custom sprites for the tooltip background and frame which references textures.",
    "See [**Tooltip Style Component**](https://minecraft.wiki/w/Data_component_format#tooltip_style) on McWiki for more details.",
    "Requires Paper 1.21.3+",
    "",
    "**Changers**:",
    "- `set` = Set the key of the texture to use.",
    "- `reset` = Reset back to default state.",
    "- `delete` = Will delete any value (vanilla or not)."})
@Examples({"set tooltip style of {_item} to \"my_pack:some_style\"",
    "delete tooltip style of player's tool",
    "reset tooltip style of player's tool"})
@Since("INSERT VERSION")
@SuppressWarnings("UnstableApiUsage")
public class ExprTooltipStyleComponent extends SimplePropertyExpression<Object, String> {

    static {
        register(ExprTooltipStyleComponent.class, String.class,
            "tool[ ]tip style [component]", "itemstacks/itemtypes/slots");
    }

    @Override
    public @Nullable String convert(Object from) {
        ItemStack itemStack = ItemUtils.getItemStackFromObjects(from);
        if (itemStack != null && itemStack.hasData(DataComponentTypes.TOOLTIP_STYLE)) {
            Key data = itemStack.getData(DataComponentTypes.TOOLTIP_STYLE);
            if (data != null) return data.toString();
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        return switch (mode) {
            case SET -> CollectionUtils.array(String.class);
            case REMOVE, DELETE -> CollectionUtils.array();
            default -> null;
        };
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {
        String string = delta != null && delta[0] instanceof String s ? s : null;
        NamespacedKey namespacedKey = Util.getNamespacedKey(string, false);

        ItemUtils.modifyComponent(getExpr().getArray(event), mode, DataComponentTypes.TOOLTIP_STYLE, namespacedKey);
    }

    @Override
    protected String getPropertyName() {
        return "tooltip style";
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

}
