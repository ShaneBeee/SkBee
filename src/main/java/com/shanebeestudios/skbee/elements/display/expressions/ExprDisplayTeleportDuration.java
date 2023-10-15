package com.shanebeestudios.skbee.elements.display.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.util.MathUtil;
import com.shanebeestudios.skbee.elements.display.types.Types;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Teleport Duration")
@Description({"Represents the time it takes for a diplay entity to teleport from one location to another.",
        "This works similaly to interpolation, but this uses teleportation instead of quaternions and math.",
        "NOTE: Due to how this works, you will need to use integers as ticks instead of time spans.",
        "This number is clamped between 0 and 59.",
        "\n0 means that updates are applied immediately.",
        "\n1 means that the display entity will move from current position to the updated one over one tick.",
        "\nHigher values spread the movement over multiple ticks.",
        "Requires MC 1.20.2+", Types.McWIKI})
@Examples({"set display teleport duration of {_display} to 30",
        "reset display teleport duration of {_display}",
        "add 10 to display teleport duration of {_display}",
        "remove 5 from display teleport duration of {_display}"})
@Since("INSERT VERSION")
public class ExprDisplayTeleportDuration extends SimplePropertyExpression<Entity, Number> {

    static {
        if (Skript.methodExists(Display.class, "getTeleportDuration")) {
            register(ExprDisplayTeleportDuration.class, Number.class,
                    "[display] teleport[ation] duration", "entities");
        }
    }

    @Override
    public @Nullable Number convert(Entity entity) {
        if (entity instanceof Display display) return display.getTeleportDuration();
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case ADD, SET, REMOVE -> CollectionUtils.array(Number.class);
            case RESET -> CollectionUtils.array();
            default -> null;
        };
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        int newVal = 0;
        if (delta != null && delta[0] instanceof Number number) {
            newVal = number.intValue();
        }

        for (Entity entity : getExpr().getArray(event)) {
            if (entity instanceof Display display) {
                int oldVal = 0; // display.getTeleportDuration()
                int duration = switch (mode) {
                    case ADD -> oldVal + newVal;
                    case REMOVE -> oldVal - newVal;
                    case SET -> newVal;
                    default -> 0;
                };
                // Minecraft clamps this, but Bukkit throws an error
                duration = MathUtil.clamp(duration, 0, 59);
                display.setTeleportDuration(duration);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "display teleport duration";
    }

}
