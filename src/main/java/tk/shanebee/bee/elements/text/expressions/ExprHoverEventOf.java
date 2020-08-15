package tk.shanebee.bee.elements.text.expressions;

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
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Name("Text Component - Hover Event Of")
@Description("Set the hover event of a text component.")
@Examples({"set {_t} to text component from \"Check out my cool tool!\"",
        "set hover event of {_t} to a new hover event showing player's tool",
        "send component {_t} to player"})
@Since("INSERT VERSION")
public class ExprHoverEventOf extends PropertyExpression<BaseComponent, HoverEvent> {

    static {
        register(ExprHoverEventOf.class, HoverEvent.class, "hover event", "basecomponents");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<BaseComponent>) exprs[0]);
        return true;
    }

    @Override
    protected HoverEvent @NotNull [] get(Event e, BaseComponent[] source) {
        return get(source, BaseComponent::getHoverEvent);
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

        for (BaseComponent component : getExpr().getArray(e)) {
            component.setHoverEvent(hoverEvent);
        }
    }

    @Override
    public @NotNull Class<? extends HoverEvent> getReturnType() {
        return HoverEvent.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "hover event of " + getExpr().toString(e, d);
    }

}
