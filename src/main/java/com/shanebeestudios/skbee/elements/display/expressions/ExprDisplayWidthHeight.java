package com.shanebeestudios.skbee.elements.display.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.elements.display.types.Types;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Display Width/Height")
@Description({"Represents the width/height of a display entity.", Types.McWIKI})
@Examples("set display width of {_display} to 3")
@Since("INSERT VERSION")
public class ExprDisplayWidthHeight extends SimplePropertyExpression<Entity, Float> {

    static {
        register(ExprDisplayWidthHeight.class, Float.class,
                "display (width|height:height)", "entities");
    }

    private boolean height;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.height = parseResult.hasTag("height");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Float convert(Entity entity) {
        if (!(entity instanceof Display display)) return null;
        return this.height ? display.getDisplayHeight() : display.getDisplayWidth();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Float.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof Float changeValue) {
            for (Entity entity : getExpr().getArray(event)) {
                if (!(entity instanceof Display display)) continue;
                if (this.height) {
                    display.setDisplayHeight(changeValue);
                } else {
                    display.setDisplayWidth(changeValue);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends Float> getReturnType() {
        return Float.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "display " + (this.height ? "height" : "width");
    }

}
