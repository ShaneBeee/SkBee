package com.shanebeestudios.skbee.elements.display.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.elements.display.types.Types;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Glow Color Override")
@Description({"Represents the glow color override of a Display Entity.", Types.McWIKI})
@Examples("set glow color override of {_display} to bukkitColor(255,1,1,100)")
@Since("INSERT VERSION")
public class ExprDisplayGlowColor extends SimplePropertyExpression<Entity, Color> {

    static {
        register(ExprDisplayGlowColor.class, Color.class, "glow color override", "entities");
    }

    @SuppressWarnings("deprecation")
    @Override
    public @Nullable Color convert(Entity entity) {
        if (entity instanceof Display display) return display.getGlowColorOverride();
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Color.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue", "deprecation"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Color color) {
            for (Entity entity : getExpr().getArray(event)) {
                if (entity instanceof Display display) display.setGlowColorOverride(color);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Color> getReturnType() {
        return Color.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "glow color override";
    }

}
