package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class ExprGliderComponent extends SimplePropertyExpression<Object, Boolean> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprGliderComponent.class, Boolean.class,
                "glider component", "itemstacks/itemtypes/slots")
            .name("ItemComponent - Glider")
            .description("If applied, allows players to glide (as with elytra) when equipped.",
                "If the item does not have a glider, it will return null, not false.",
                "See [**Glider Component**](https://minecraft.wiki/w/Data_component_format#glider) on McWiki for more details.",
                "Requires Paper 1.21.3+",
                "",
                "**Changers**:",
                "- `set` = If set to true, a glider will be applied, otherwise removed.",
                "- `reset` = Reset back to default state.",
                "- `delete` = Will delete any value (vanilla or not).")
            .examples("if glider component of player's tool is true:",
                "if glider component of {_item} is not set:",
                "set glider component of player's tool to true",
                "delete glider component of {_item}",
                "reset glider component of {_item}")
            .since("3.8.0")
            .register();
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
