package com.shanebeestudios.skbee.elements.display.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.elements.display.types.Types;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - See Through")
@Description({"Represents the see through state of a Text Display Entity.", Types.McWIKI})
@Examples("set see thru state of {_display} to true")
@Since("INSERT VERSION")
public class ExprTextDisplaySeeThru extends SimplePropertyExpression<Entity, Boolean> {

    static {
        register(ExprTextDisplaySeeThru.class, Boolean.class,
                "see (thru|through) state", "entities");
    }

    @Override
    public @Nullable Boolean convert(Entity entity) {
        if (entity instanceof TextDisplay textDisplay) return textDisplay.isSeeThrough();
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
        if (delta != null && delta[0] instanceof Boolean seeThrough) {
            for (Entity entity : getExpr().getArray(event)) {
                if (entity instanceof TextDisplay textDisplay) {
                    textDisplay.setSeeThrough(seeThrough);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "see thru state";
    }

}
