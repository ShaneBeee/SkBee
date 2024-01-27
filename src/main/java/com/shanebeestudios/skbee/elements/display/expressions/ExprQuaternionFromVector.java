package com.shanebeestudios.skbee.elements.display.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

@Name("Quaternion - From Vector")
@Description({"Create a quaternion from a vector.",
        "The XYZ of the vector will represents the degrees the display entity will rotate on the XYZ axis."})
@Examples({"set {_q} to quaternion from vector(0,45,0)",
        "set display right rotation of {_e} to {_q}"})
@Since("3.2.0")
public class ExprQuaternionFromVector extends SimpleExpression<Quaternionf> {

    static {
        Skript.registerExpression(ExprQuaternionFromVector.class, Quaternionf.class, ExpressionType.PROPERTY,
                "quat[ernion] from [vector] %vector%");
    }

    private Expression<Vector> vector;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.vector = (Expression<Vector>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Quaternionf @Nullable [] get(Event event) {
        Quaternionf quat = new Quaternionf(0, 0, 0, 1);
        Vector vector = this.vector.getSingle(event);
        if (vector == null) return null;

        float x = (float) Math.toRadians(vector.getX());
        float y = (float) Math.toRadians(vector.getY());
        float z = (float) Math.toRadians(vector.getZ());
        quat = quat.rotateZYX(x, y, z);

        return new Quaternionf[]{quat};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Quaternionf> getReturnType() {
        return Quaternionf.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "quaterion from " + this.vector.toString(e, d);
    }

}
