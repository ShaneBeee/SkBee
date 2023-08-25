package com.shanebeestudios.skbee.elements.villager.expressions;

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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Villager - Level/Experience")
@Description({"Represents the level/experience of a villager.",
        "Level is between 1 and 5.","" +
        "Experience is a number greater than or equal to 0."})
@Examples({"set villager level of target entity to 5",
        "set villager experience of last spawned villager to 10",
        "if villager level of target entity > 2:",
        "if villager experience of target entity > 10:"})
@Since("1.17.0")
public class ExprVillagerLevel extends SimplePropertyExpression<LivingEntity, Number> {

    static {
        register(ExprVillagerLevel.class, Number.class, "villager (level|1¦experience)", "livingentities");
    }

    private int pattern;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = matchedPattern;
        setExpr((Expression<? extends LivingEntity>) exprs[0]);
        return true;
    }

    @Override
    public @Nullable Number convert(LivingEntity livingEntity) {
        if (livingEntity instanceof Villager villager) {
            return getValue(villager);
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case SET, RESET, ADD, REMOVE -> CollectionUtils.array(Number.class);
            default -> null;
        };
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        int value = delta != null ? ((Number) delta[0]).intValue() : 0;

        for (LivingEntity entity : getExpr().getArray(event)) {
            if (entity instanceof Villager villager) {
                int i = value;
                switch (mode) {
                    case ADD -> i += getValue(villager);
                    case REMOVE -> i = getValue(villager) - i;
                    case RESET -> i = 0;
                }
                setValue(villager, i);
            }
        }
    }

    private int getValue(Villager villager) {
        if (pattern == 0) {
            return villager.getVillagerLevel();
        }
        return villager.getVillagerExperience();
    }

    private void setValue(Villager villager, int value) {
        if (pattern == 0) {
            if (value < 1) {
                value = 1;
            } else if (value > 5) {
                value = 5;
            }
            villager.setVillagerLevel(value);
        } else {
            if (value < 0) value = 0;
            villager.setVillagerExperience(value);
        }
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return pattern == 1 ? "experience" : "level";
    }

}
