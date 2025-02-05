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
import com.shanebeestudios.skbee.api.util.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("ItemComponent - Glider")
@Description({"If applied, allows players to glide (as with elytra) when equipped.",
    "If the item does not have a glider, it will return null, not false.",
    "See [**Glider Component**](https://minecraft.wiki/w/Data_component_format#glider) on McWiki for more details.",
    "Requires Paper 1.21.3+ and `item_component` feature.",
    "",
    "**Changers**:",
    "- `set` = If set to true, a glider will be applied, otherwise removed.",
    "- `reset` = Reset back to default state.",
    "- `delete` = Will delete any value (vanilla or not)."})
@Examples({"if glider component of player's tool is true:",
    "if glider component of {_item} is not set:",
    "set glider component of player's tool to true",
    "delete glider component of {_item}",
    "reset glider component of {_item}"})
@Since("3.8.0")
@SuppressWarnings("UnstableApiUsage")
public class ExprGliderComponent extends SimplePropertyExpression<Object, Boolean> {

    static {
        register(ExprGliderComponent.class, Boolean.class,
            "glider component", "itemstacks/itemtypes/slots");
    }

    @Override
    public @Nullable Boolean convert(Object from) {
        ItemStack itemStack = ItemUtils.getItemStackFromObjects(from);
        if (itemStack != null) {
            if (itemStack.hasData(DataComponentTypes.GLIDER)) return true;
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET -> CollectionUtils.array(Boolean.class);
            case DELETE, RESET -> CollectionUtils.array();
            default -> null;
        };
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        boolean glider = delta != null && delta[0] instanceof Boolean b ? b : false;

        ItemUtils.modifyItems(getExpr().getArray(event), itemStack -> {
            if (mode == ChangeMode.SET) {
                if (glider) itemStack.setData(DataComponentTypes.GLIDER);
                else itemStack.unsetData(DataComponentTypes.GLIDER);
            } else if (mode == ChangeMode.DELETE) {
                itemStack.unsetData(DataComponentTypes.GLIDER);
            } else if (mode == ChangeMode.RESET) {
                itemStack.resetData(DataComponentTypes.GLIDER);
            }
        });
    }

    @Override
    protected String getPropertyName() {
        return "glider component";
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

}
