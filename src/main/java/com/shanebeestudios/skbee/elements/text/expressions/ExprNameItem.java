package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.text.BeeComponent;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Text Component - ItemType Name")
@Description("Get/set the component name of an ItemType.")
@Examples("set component name of player's tool to translate component of \"item.minecraft.diamond_sword\"")
@Since("INSERT VERSION")
public class ExprNameItem extends SimplePropertyExpression<ItemType, BeeComponent> {

    static {
        register(ExprNameItem.class, BeeComponent.class,
                "component item[[ ]type] name", "itemtypes");
    }

    @Override
    public @Nullable BeeComponent convert(ItemType itemType) {
        return BeeComponent.fromComponent(itemType.getItemMeta().displayName());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(BeeComponent.class);
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            if (delta[0] instanceof BeeComponent component) {
                for (ItemType itemType : getExpr().getArray(event)) {
                    component.setItemName(itemType);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends BeeComponent> getReturnType() {
        return BeeComponent.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "component itemtype name";
    }

}
