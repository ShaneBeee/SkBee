package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExprNameInventory extends SimplePropertyExpression<Inventory, ComponentWrapper> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprNameInventory.class, ComponentWrapper.class,
                "component inventory (name|title)", "inventories")
            .name("TextComponent - Inventory Name")
            .description("Get/set the name of an inventory using components.",
                "NOTE: This will only rename OPEN inventories, not inventories saved in variables.",
                "NOTE: Internally the component is stringified, so things like fonts wont work.")
            .examples("set component inventory title of player's current inventory to {_t}")
            .since("2.4.0")
            .register();
    }

    @Override
    public @Nullable ComponentWrapper convert(Inventory inventory) {
        List<HumanEntity> viewers = inventory.getViewers();
        if (!viewers.isEmpty()) {
            Component title = viewers.get(0).getOpenInventory().title();
            return ComponentWrapper.fromComponent(title);
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(ComponentWrapper.class);
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            if (delta[0] instanceof ComponentWrapper component) {
                for (Inventory inventory : getExpr().getArray(event)) {
                    component.setInventoryName(inventory);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "component inventory name";
    }

}
