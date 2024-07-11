package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("TextComponent - Inventory Name")
@Description({"Get/set the name of an inventory using components.",
    "NOTE: This will only rename OPEN inventories, not inventories saved in variables.",
    "NOTE: Internally the component is stringified, so things like fonts wont work.",
    "Requires Minecraft 1.21+",
    "For versions below 1.21, just use Skript's inventory title expression."})
@Examples("set component inventory title of player's current inventory to {_t}")
@Since("2.4.0")
public class ExprNameInventory extends SimplePropertyExpression<Inventory, ComponentWrapper> {

    static {
        register(ExprNameInventory.class, ComponentWrapper.class,
            "component inventory (name|title)", "inventories");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!Skript.isRunningMinecraft(1, 21)) {
            // Bukkit changed from Class to Interface, which won't work on older servers
            // I don't feel like doing handfuls of reflection to fix this
            Skript.error("'component inventory name' requires Minecraft 1.21+");
            return false;
        }
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
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
