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

@Name("Quaternion - Rotated")
@Description("Rotate a quaternion along the XYZ axis using a vector.")
@Examples({"function rotateDisplay(e: entity, v: vector):",
        "\tset {_q} to display right rotation of {_e}",
        "\tset {_q} to {_q} rotated by {_v}",
        "\tset display right rotation of {_e} to {_q}",
        "",
        "rotateDisplay({_entity}, vector(45,0,0))"})
@Since("INSERT VERSION")
public class ExprQuaternionRotated extends SimpleExpression<Quaternionf> {

    static {
        Skript.registerExpression(ExprQuaternionRotated.class, Quaternionf.class, ExpressionType.COMBINED,
                "%quaternions% rotated (by|around) %vector%");
    }

    private Expression<Quaternionf> quaterions;
    private Expression<Vector> vector;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.quaterions = (Expression<Quaternionf>) exprs[0];
        this.vector = (Expression<Vector>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Quaternionf @Nullable [] get(Event event) {
        Quaternionf[] quaterions = this.quaterions.getArray(event);
        Vector vector = this.vector.getSingle(event);
        if (vector == null) return null;

        float x = (float) Math.toRadians(vector.getX());
        float y = (float) Math.toRadians(vector.getY());
        float z = (float) Math.toRadians(vector.getZ());

        for (int i = 0; i < quaterions.length; i++) {
            quaterions[i] = quaterions[i].rotateXYZ(x, y, z);
        }
        return quaterions;
    }

    @Override
    public boolean isSingle() {
        return this.quaterions.isSingle();
    }

    @Override
    public @NotNull Class<? extends Quaternionf> getReturnType() {
        return Quaternionf.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return this.quaterions.toString(e, d) + " rotated by " + this.vector.toString(e, d);
    }

}
