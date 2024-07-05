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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Absorption Amount")
@Description("Represents the absorption amount of an entity.")
@Examples({"set {_absorption} to absorption amount of player",
    "set absorption amount of player to 5",
    "add 2 to absorption amount of player",
    "remove 2 from absorption amount of player",
    "reset absorption amount of player"})
@Since("1.17.0")
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
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, ADD, REMOVE, RESET -> CollectionUtils.array(Number.class);
            default -> null;
        };
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        double value = (delta != null && delta[0] != null) ? ((Number) delta[0]).doubleValue() : 0;
        for (Entity entity : getExpr().getArray(event)) {
            if (entity instanceof Damageable damageable) {
                double oldAmount = damageable.getAbsorptionAmount();
                double change = switch (mode) {
                    case SET -> value;
                    case ADD -> oldAmount + value;
                    case REMOVE -> oldAmount - value;
                    default -> 0;
                };
                damageable.setAbsorptionAmount(Math.max(0, change));
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
