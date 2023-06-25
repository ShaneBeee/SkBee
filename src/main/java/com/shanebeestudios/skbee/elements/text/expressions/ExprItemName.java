package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Text Component - ItemType Name")
@Description("Get/set the component name of an ItemType.")
@Examples("set component item name of player's tool to translate component of \"item.minecraft.diamond_sword\"")
@Since("2.4.0")
public class ExprItemName extends SimplePropertyExpression<ItemType, ComponentWrapper> {

    static {
        register(ExprItemName.class, ComponentWrapper.class,
                "component item[[ ]type] name", "itemtypes");
    }

    @Override
    public @Nullable ComponentWrapper convert(ItemType itemType) {
        return ComponentWrapper.fromComponent(itemType.getItemMeta().displayName());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
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
                for (ItemType itemType : getExpr().getArray(event)) {
                    component.setItemName(itemType);
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
        return "component itemtype name";
    }

}
