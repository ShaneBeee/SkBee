package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprTextDisplayTextComponent extends SimplePropertyExpression<Display, ComponentWrapper> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprTextDisplayTextComponent.class, ComponentWrapper.class,
                "display [text] component", "displays")
            .name("TextComponent - Text Display Entity Text")
            .description("Represents the text component of a Text Display Entity.")
            .examples("set display component of {_display} to mini message from \"<rainbow>OOO A RAINBOW\"")
            .since("2.8.0")
            .register();
    }

    @Override
    public @Nullable ComponentWrapper convert(Display entity) {
        if (entity instanceof TextDisplay textDisplay) {
            Component text = textDisplay.text();
            return ComponentWrapper.fromComponent(text);
        }
        return null;
    }

    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(ComponentWrapper.class);
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof ComponentWrapper componentWrapper) {
            Component component = componentWrapper.getComponent();
            for (Display entity : getExpr().getArray(event)) {
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
