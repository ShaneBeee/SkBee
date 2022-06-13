package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Villager - Profession")
@Description("Represents the profession of a villager.")
@Examples("set profession of target entity to nitwit profession")
@Since("INSERT VERSION")
public class ExprVillagerProfession extends SimplePropertyExpression<LivingEntity, Profession> {

    static {
        register(ExprVillagerProfession.class, Profession.class, "profession", "livingentities");
    }

    @Override
    public @Nullable Profession convert(LivingEntity entity) {
        if (entity instanceof Villager villager) {
            return villager.getProfession();
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, RESET -> CollectionUtils.array(Profession.class);
            default -> null;
        };
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Profession profession = delta != null ? ((Profession) delta[0]) : Profession.NONE;
        Expression<? extends LivingEntity> expr = getExpr();
        if (expr != null) {
            for (LivingEntity entity : expr.getArray(event)) {
                if (entity instanceof Villager villager) {
                    villager.setProfession(profession);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends Profession> getReturnType() {
        return Profession.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "villager profession";
    }

}
