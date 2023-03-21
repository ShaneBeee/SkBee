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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Interaction - Interaction Width/Height")
@Description({"Represents the interaction width/height of an Interaction Entity.", Types.McWiki_INTERACTION})
@Examples({"set interaction height of last spawned entity to 10",
        "add 5 to interaction width of {_int}",
        "remove 2.5 from interaction width of {_int}",
        "reset interaction height of {_int}"})
@Since("INSERT VERSION")
public class ExprInteractionWidthHeight extends SimplePropertyExpression<Entity, Float> {

    static {
        register(ExprInteractionWidthHeight.class, Float.class,
                "interaction (width|h:height)", "entities");
    }

    private boolean height;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.height = parseResult.hasTag("h");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Float convert(Entity entity) {
        if (entity instanceof Interaction interaction) {
            return this.height ? interaction.getInteractionHeight() : interaction.getInteractionWidth();
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        return switch (mode) {
            case ADD, SET, RESET, REMOVE -> CollectionUtils.array(Float.class);
            default -> null;
        };
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        float changeValue = 1.0f;
        if (delta != null && delta[0] instanceof Float f) {
            changeValue = f;
        }
        for (Entity entity : getExpr().getArray(event)) {
            if (entity instanceof Interaction interaction) {
                float oldValue = this.height ? interaction.getInteractionHeight() : interaction.getInteractionWidth();
                if (mode == ChangeMode.ADD) changeValue += oldValue;
                else if (mode == ChangeMode.REMOVE) changeValue = oldValue - changeValue;
                else if (mode == ChangeMode.RESET) changeValue = 1.0f;
                if (this.height) {
                    interaction.setInteractionHeight(changeValue);
                } else {
                    interaction.setInteractionWidth(changeValue);
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
        return "interaction " + (this.height ? "height" : "width");
    }

}
