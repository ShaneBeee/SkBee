package com.shanebeestudios.skbee.elements.itemcomponent.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.ItemComponentUtils;
import com.shanebeestudios.skbee.api.util.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseRemainder;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class ExprUseRemainderComponent extends SimplePropertyExpression<Object, ItemStack> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprUseRemainderComponent.class, ItemStack.class,
                "use remainder [component]", "itemstacks/itemtypes/slots")
            .name("ItemComponent - Use Remainder")
            .description("If present, replaces the item with a remainder item if its stack count has decreased after use.",
                "Requires Paper 1.21.3+",
                "See [**Use Remainder Component**](https://minecraft.wiki/w/Data_component_format#use_remainder) on McWiki for more details.",
                "",
                "**Changers**:",
                "- `set` = Set the item to be replaced with.",
                "- `reset` = Reset back to default state.",
                "- `delete` = Will delete any value (vanilla or not).")
            .examples("set use remainder of player's tool to 1 of glass bottle",
                "delete use remainder of {_item}",
                "reset use remainder of {_item}")
            .since("3.8.0")
            .register();
    }

    @Override
    public @Nullable ItemStack convert(Object from) {
        ItemStack itemStack = ItemUtils.getItemStackFromObjects(from);
        if (itemStack != null && itemStack.hasData(DataComponentTypes.USE_REMAINDER)) {
            UseRemainder data = itemStack.getData(DataComponentTypes.USE_REMAINDER);
            if (data != null) return data.transformInto();
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET -> CollectionUtils.array(ItemStack.class);
            case RESET, DELETE -> CollectionUtils.array();
            default -> null;
        };
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        ItemStack transformInto = delta != null && delta[0] instanceof ItemStack is ? is : null;
        UseRemainder remainder = transformInto != null ? UseRemainder.useRemainder(transformInto) : null;

        ItemComponentUtils.modifyComponent(getExpr().getArray(event), mode, DataComponentTypes.USE_REMAINDER, remainder);
    }

    @Override
    protected String getPropertyName() {
        return "use remainder";
    }

    @Override
    public Class<? extends ItemStack> getReturnType() {
        return ItemStack.class;
    }

}
