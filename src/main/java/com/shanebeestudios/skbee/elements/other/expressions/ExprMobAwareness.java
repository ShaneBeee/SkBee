package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Mob Awareness")
@Description({"Represents whether this mob is aware of its surroundings.",
        "Unaware mobs will still move if pushed, attacked, etc. but will not move or perform any actions on their own.",
        "Unaware mobs will still be affected by gravity.",
        "Unaware mobs may also have other unspecified behaviours disabled, such as drowning."})
@Examples({"if mob awareness of target entity = true:",
        "set mob awareness of target entity to false"})
@Since("INSERT VERSION")
public class ExprMobAwareness extends SimplePropertyExpression<Entity, Boolean> {

    static {
        register(ExprMobAwareness.class, Boolean.class, "mob awareness", "entities");
    }

    @Override
    public @Nullable Boolean convert(Entity entity) {
        if (entity instanceof Mob mob) return mob.isAware();
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
        if (delta != null && delta[0] instanceof Boolean aware) {
            for (Entity entity : getExpr().getArray(event)) {
                if (entity instanceof Mob mob) mob.setAware(aware);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "mob awareness";
    }

}
