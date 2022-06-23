package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Absorption Amount")
@Description("Represents the absorption amount of an entity.")
@Examples("set absorption amount of player to 5")
@Since("INSERT VERSION")
public class ExprAbsorptionAmount extends SimplePropertyExpression<Entity, Number> {

    static {
        register(ExprAbsorptionAmount.class, Number.class, "absorption amount", "entities");
    }

    @Override
    public @Nullable Number convert(Entity entity) {
        if (entity instanceof Damageable damageable) {
            return damageable.getAbsorptionAmount();
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, ADD, REMOVE -> CollectionUtils.array(Number.class);
            default -> null;
        };
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        double change = delta[0] != null ? ((Number) delta[0]).doubleValue() : 0;
        for (Entity entity : getExpr().getArray(event)) {
            if (entity instanceof Damageable damageable) {
                double oldAmount = damageable.getAbsorptionAmount();
                if (mode == ChangeMode.ADD) {
                    change = oldAmount + change;
                } else if (mode == ChangeMode.REMOVE) {
                    change = oldAmount - change;
                }
                if (change < 0) change = 0;
                damageable.setAbsorptionAmount(change);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "absorption amount";
    }

}
