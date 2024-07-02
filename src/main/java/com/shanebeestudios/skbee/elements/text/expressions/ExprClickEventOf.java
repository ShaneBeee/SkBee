package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("TextComponent - Click Event Of")
@Description("Set the click event of a text component.")
@Examples({"set {_t} to text component from \"Check out this cool thing at SPAWN!\"",
        "set hover event of {_t} to a new hover event showing \"Clicky clicky to go to spawn!\"",
        "set click event of {_t} to a new click event to run command \"/spawn\"",
        "send component {_t} to player"})
@Since("1.5.0")
public class ExprClickEventOf extends PropertyExpression<ComponentWrapper, ClickEvent> {

    static {
        register(ExprClickEventOf.class, ClickEvent.class, "click event", "textcomponents");
    }

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<ComponentWrapper>) exprs[0]);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected ClickEvent @NotNull [] get(Event e, ComponentWrapper[] source) {
        return get(source, ComponentWrapper::getClickEvent);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET)
            return CollectionUtils.array(ClickEvent.class);
        return null;
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    @Override
    public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
        ClickEvent clickEvent = delta != null ? ((ClickEvent) delta[0]) : null;

        if (clickEvent == null) return;

        for (ComponentWrapper component : getExpr().getArray(e)) {
            component.setClickEvent(clickEvent);
        }
    }

    @Override
    public @NotNull Class<? extends ClickEvent> getReturnType() {
        return ClickEvent.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "click event of " + getExpr().toString(e, d);
    }

}
