package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Text Component - Merge Components")
@Description("Merge multiple components into one.")
@Examples({"set {_t} to merge components {_t::*}",
        "set {_t} to merge components {_t::*} joined with newline"})
@Since("2.4.0, 2.8.5 (delimiter)")
public class ExprMergeComponents extends SimpleExpression<ComponentWrapper> {

    static {
        Skript.registerExpression(ExprMergeComponents.class, ComponentWrapper.class, ExpressionType.SIMPLE,
                "merge components %textcomponents% [[join[ed]] with %-string%]");
    }

    private Expression<ComponentWrapper> components;
    private Expression<String> delimiter;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.components = (Expression<ComponentWrapper>) exprs[0];
        this.delimiter = (Expression<String>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable ComponentWrapper[] get(Event event) {
        if (this.components == null)
            return null;
        ComponentWrapper[] components = this.components.getArray(event);
        if (this.delimiter != null)
            return new ComponentWrapper[]{ComponentWrapper.fromComponents(components, this.delimiter.getSingle(event))};
        return new ComponentWrapper[]{ComponentWrapper.fromComponents(components)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "merge components " + this.components.toString(e,d);
    }

}
