package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pose;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Entity Pose")
@Description({"Get/set the pose of an entity.",
    "Note: While poses affect some things like hitboxes, they do not change the entity's state",
    "(e.g. having sneaking pose does not guarantee `is sneaking` being true). Set requires PaperMC."})
@Examples({"set {_pose} to pose of player",
    "set pose of target entity to sleeping pose"})
@Since("INSERT VERSION")
public class ExprEntityPose extends SimplePropertyExpression<Entity, Pose> {

    static {
        register(ExprEntityPose.class, Pose.class, "pose", "entities");
    }

    @Override
    public @Nullable Pose convert(Entity entity) {
        return entity.getPose();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (!Skript.methodExists(Entity.class, "setPose", Pose.class)) {
            Skript.error("Setting an entity's pose requires PaperMC.");
            return null;
        }
        if (mode == ChangeMode.SET) return CollectionUtils.array(Pose.class);
        return null;
    }

    @SuppressWarnings({"ConstantValue", "NullableProblems"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Pose pose) {
            for (Entity entity : getExpr().getArray(event)) {
                entity.setPose(pose);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Pose> getReturnType() {
        return Pose.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "pose";
    }

}
