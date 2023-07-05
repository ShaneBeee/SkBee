package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Hurt Animation")
@Description({"Makes this living entity flash red as if they were damaged. Requires MC 1.20.1+",
        "\nNote: Yaw = The direction the damage is coming from in relation to the entity,",
        "where 0 is in front of the player, 90 is to the right, 180 is behind, and 270 is to the left"})
@Examples({"play hurt animation on player",
        "play hurt animation on all players",
        "play hurt animation on all players with yaw 270",
        "play hurt animation on all mobs"})
@Since("INSERT VERSION")
public class EffHurtAnimation extends Effect {

    static {
        if (Skript.methodExists(LivingEntity.class, "playHurtAnimation", float.class)) {
            Skript.registerEffect(EffHurtAnimation.class,
                    "play hurt animation on %entities% [with yaw %-number%]");
        }
    }

    private Expression<Entity> entities;
    private Expression<Number> yaw;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.entities = (Expression<Entity>) exprs[0];
        this.yaw = (Expression<Number>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        float yaw = 0;
        if (this.yaw != null) {
            Number yawSingle = this.yaw.getSingle(event);
            if (yawSingle != null) yaw = yawSingle.floatValue();
        }
        for (Entity entity : this.entities.getArray(event)) {
            if (entity instanceof LivingEntity livingEntity) livingEntity.playHurtAnimation(yaw);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String yaw = this.yaw != null ? (" with yaw " + this.yaw.toString(e, d)) : "";
        return "player hurt animation on " + this.entities.toString(e, d) + yaw;
    }

}
