package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@SuppressWarnings("rawtypes")
public class ExprHoverEventOf extends PropertyExpression<ComponentWrapper, HoverEvent> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprHoverEventOf.class, HoverEvent.class,
                "hover event", "textcomponents")
            .name("TextComponent - Hover Event Of")
            .description("Set the hover event of a text component.")
            .examples("set {_t} to text component from \"Check out my cool tool!\"",
                "set hover event of {_t} to a new hover event showing player's tool",
                "send component {_t} to player")
            .since("1.5.0")
            .register();
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<ComponentWrapper>) exprs[0]);
        return true;
    }

    @Override
    protected HoverEvent<?> @NotNull [] get(Event e, ComponentWrapper[] source) {
        return get(source, ComponentWrapper::getHoverEvent);
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET)
            return CollectionUtils.array(HoverEvent.class);
        return null;
    }

    @Override
    public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
        HoverEvent hoverEvent = delta != null ? ((HoverEvent) delta[0]) : null;

        if (hoverEvent == null) return;

        for (ComponentWrapper component : getExpr().getArray(e)) {
            component.setHoverEvent(hoverEvent);
        }
    }

    @Override
    public @NotNull Class<? extends HoverEvent> getReturnType() {
        return HoverEvent.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "hover event of " + getExpr().toString(e, d);
    }

}
