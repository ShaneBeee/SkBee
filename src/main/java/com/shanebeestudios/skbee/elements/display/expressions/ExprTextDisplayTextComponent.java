package com.shanebeestudios.skbee.elements.display.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import com.shanebeestudios.skbee.elements.display.types.Types;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Text Component")
@Description({"Represents the text component of a Text Display Entity.", Types.McWIKI})
@Examples("set display component of {_display} to mini message from \"<rainbow>OOO A RAINBOW\"")
@Since("2.8.0")
public class ExprTextDisplayTextComponent extends SimplePropertyExpression<Entity, ComponentWrapper> {

    static {
        if (SkBee.getPlugin().getAddonLoader().isTextComponentEnabled()) {
            register(ExprTextDisplayTextComponent.class, ComponentWrapper.class, "display [text] component", "entities");
        }
    }

    @Override
    public @Nullable ComponentWrapper convert(Entity entity) {
        if (entity instanceof TextDisplay textDisplay) {
            Component text = textDisplay.text();
            return ComponentWrapper.fromComponent(text);
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(ComponentWrapper.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof ComponentWrapper componentWrapper) {
            Component component = componentWrapper.getComponent();
            for (Entity entity : getExpr().getArray(event)) {
                if (entity instanceof TextDisplay textDisplay) {
                    textDisplay.text(component);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "display text component";
    }

}
