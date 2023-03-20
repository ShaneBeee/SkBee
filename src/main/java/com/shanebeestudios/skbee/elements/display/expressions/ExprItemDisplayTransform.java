package com.shanebeestudios.skbee.elements.display.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.elements.display.types.Types;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Item Display Transform")
@Description({"Represents the item display transform of an Item Display Entity.", Types.McWIKI})
@Examples({"set item display transform of {_display} to ground transform",
        "set item display transform of {_display} to fixed transform"})
@Since("2.8.0")
public class ExprItemDisplayTransform extends SimplePropertyExpression<Entity, ItemDisplayTransform> {

    static {
        register(ExprItemDisplayTransform.class, ItemDisplayTransform.class,
                "item display transform", "entities");
    }

    @Override
    public @Nullable ItemDisplayTransform convert(Entity entity) {
        if (entity instanceof ItemDisplay itemDisplay) return itemDisplay.getItemDisplayTransform();
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(ItemDisplayTransform.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof ItemDisplayTransform transform) {
            for (Entity entity : getExpr().getArray(event)) {
                if (entity instanceof ItemDisplay itemDisplay) {
                    itemDisplay.setItemDisplayTransform(transform);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends ItemDisplayTransform> getReturnType() {
        return ItemDisplayTransform.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "item display transform";
    }

}
