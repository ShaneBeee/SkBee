package com.shanebeestudios.skbee.elements.display.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.text.BeeComponent;
import com.shanebeestudios.skbee.elements.display.types.Types;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Text Component")
@Description({"Represents the text component of a Text Display Entity.", Types.McWIKI})
@Examples("set display component of {_display} to mini message from \"<rainbow>OOO A RAINBOW\"")
@Since("INSERT VERSION")
public class ExprTextDisplayTextComponent extends SimplePropertyExpression<Display, BeeComponent> {

    static {
        if (SkBee.getPlugin().getAddonLoader().isTextComponentEnabled()) {
            register(ExprTextDisplayTextComponent.class, BeeComponent.class, "display [text] component", "displayentities");
        }
    }

    @Override
    public @Nullable BeeComponent convert(Display display) {
        if (display instanceof TextDisplay textDisplay) {
            Component text = textDisplay.text();
            return BeeComponent.fromComponent(text);
        }
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(BeeComponent.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantValue"})
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (delta != null && delta[0] instanceof BeeComponent beeComponent) {
            Component component = beeComponent.getComponent();
            for (Display display : getExpr().getArray(event)) {
                if (display instanceof TextDisplay textDisplay) {
                    textDisplay.text(component);
                }
            }
        }
    }

    @Override
    public @NotNull Class<? extends BeeComponent> getReturnType() {
        return BeeComponent.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "display text component";
    }

}
