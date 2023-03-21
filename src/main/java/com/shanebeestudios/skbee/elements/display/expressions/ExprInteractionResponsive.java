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
import org.bukkit.entity.Interaction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Interaction - Is Responsive")
@Description({"Represents the state of being responsive of an Interaction Entity.", Types.McWiki_INTERACTION})
@Examples("set is responsive of {_int} to true")
@Since("INSERT VERSION")
public class ExprInteractionResponsive extends SimplePropertyExpression<Entity, Boolean> {

    static {
        register(ExprInteractionResponsive.class, Boolean.class, "is responsive", "entities");
    }

    @Override
    public @Nullable Boolean convert(Entity entity) {
        if (entity instanceof Interaction interaction) return interaction.isResponsive();
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Boolean.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Boolean isResponsive) {
            for (Entity entity : getExpr().getArray(event)) {
                if (entity instanceof Interaction interaction) {
                    interaction.setResponsive(isResponsive);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "is responsive";
    }

}
