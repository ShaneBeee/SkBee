package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Name("Entity Visibility")
@Description("Get/set visibility for entities. Armor stands on all versions, ItemFrames on 1.15+ and LivingEntities on 1.16.3+")
@Examples({"set visibility of target entity to false",
    "set {_v} to visibility of target entity",
    "if visibility of target entity is true:"})
@Since("1.7.0")
public class ExprEntityVisibility extends PropertyExpression<Entity, Boolean> {

    private static final boolean ITEM_FRAME;
    private static final boolean LIVING_ENTITY;

    static {
        ITEM_FRAME = Skript.methodExists(ItemFrame.class, "setVisible", boolean.class);
        LIVING_ENTITY = Skript.methodExists(LivingEntity.class, "setInvisible", boolean.class);
        register(ExprEntityVisibility.class, Boolean.class, "visibility", "entities");
    }

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        setExpr((Expression<Entity>) exprs[0]);
        return true;
    }

    @Override
    protected Boolean @NotNull [] get(@NotNull Event e, Entity @NotNull [] source) {
        return get(source, entity -> {
            if (entity instanceof ArmorStand armorStand) {
                return armorStand.isVisible();
            } else if (entity instanceof ItemFrame itemFrame && ITEM_FRAME) {
                return itemFrame.isVisible();
            } else if (entity instanceof LivingEntity livingEntity && LIVING_ENTITY) {
                return !livingEntity.isInvisible();
            }
            return null;
        });
    }

    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(Boolean[].class);
        }
        return null;
    }

    @Override
    public void change(@NotNull Event event, @Nullable Object[] delta, @NotNull ChangeMode mode) {
        boolean visible = delta != null && ((boolean) delta[0]);
        for (Entity entity : getExpr().getArray(event)) {
            if (entity instanceof ArmorStand armorStand) {
                armorStand.setVisible(visible);
            } else if (entity instanceof ItemFrame itemFrame && ITEM_FRAME) {
                itemFrame.setVisible(visible);
            } else if (entity instanceof LivingEntity livingEntity && LIVING_ENTITY) {
                livingEntity.setInvisible(!visible);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "visibility of " + getExpr().toString(e, d);
    }

}
