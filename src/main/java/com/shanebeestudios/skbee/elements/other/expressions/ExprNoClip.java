package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.EntityUtils;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Entity NoClip")
@Description("Set or get the noClip status of an entity (This will not work on players)")
@Examples({"spawn a zombie at player",
        "set no clip state of last spawned zombie to true",
        "set {_var} to no clip state of last spawned sheep",
        "loop all entities in radius 5 around player:",
        "\tset no clip state of loop-entity to true",
        "\tpush loop-entity up with speed 5"})
@Since("1.0.2")
public class ExprNoClip extends SimplePropertyExpression<Entity, Boolean> {

    static {
        PropertyExpression.register(ExprNoClip.class, Boolean.class,
                "no[( |-)](clip|physics) (state|mode)", "entities");
    }

    @Override
    public @Nullable Boolean convert(Entity entity) {
        return EntityUtils.getNoPhysics(entity);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) {
            return CollectionUtils.array(Boolean.class);
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Boolean changeValue = (Boolean) delta[0];
        if (changeValue == null) return;

        if (mode == ChangeMode.SET) {
            for (Entity entity : getExpr().getArray(event)) {
                EntityUtils.setNoPhysics(entity, changeValue);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "no clip state";
    }

}
