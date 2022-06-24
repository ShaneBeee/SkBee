package com.shanebeestudios.skbee.elements.villager.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Type;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Villager - Type")
@Description("Represents the type of villager. Resetting will set a villager to a plains villager.")
@Examples({"set {_t} to villager type of last spawned villager",
        "set villager type of target entity to desert villager"})
@Since("1.17.0")
public class ExprVillagerType extends SimplePropertyExpression<LivingEntity, Type> {

    static {
        register(ExprVillagerType.class, Type.class, "villager type", "livingentities");
    }

    @Override
    public @Nullable Type convert(LivingEntity livingEntity) {
        if (livingEntity instanceof Villager villager) {
            return villager.getVillagerType();
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, RESET -> CollectionUtils.array(Type.class);
            default -> null;
        };
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Type type = delta != null ? ((Type) delta[0]) : Type.PLAINS;
        for (LivingEntity entity : getExpr().getArray(event)) {
            if (entity instanceof Villager villager) {
                villager.setVillagerType(type);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Type> getReturnType() {
        return Type.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "villager type";
    }

}
