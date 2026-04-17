package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pose;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprEntityPose extends SimplePropertyExpression<Entity, Pose> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprEntityPose.class, Pose.class,
                "[:fixed] pose", "entities")
            .name("Entity Pose")
            .description("Get/set the pose of an entity.",
                "Note: While poses affect some things like hitboxes, they do not change the entity's state",
                "(e.g. having sneaking pose does not guarantee `is sneaking` being true).",
                "fixed = Forces the state to remain until manually changed (this will not work on players).")
            .examples("set {_pose} to pose of player",
                "set pose of target entity to sleeping pose",
                "set fixed pose of target entity to swimming pose")
            .since("3.5.4", "3.20.0 (fixed)")
            .register();
    }

    private boolean fixed;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.fixed = parseResult.hasTag("fixed");
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Pose convert(Entity entity) {
        return entity.getPose();
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Pose.class);
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Pose pose) {
            for (Entity entity : getExpr().getArray(event)) {
                entity.setPose(pose, this.fixed);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Pose> getReturnType() {
        return Pose.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return this.fixed ? "fixed pose" : "pose";
    }

}
