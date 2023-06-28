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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Text Component - Children")
@Description("Get the children of a text component. This essentially splits the component up into its parts.")
@Examples("set {_children::*} to component children of {_textcomp}")
@Since("INSERT VERSION")
public class ExprComponentChildren extends SimpleExpression<ComponentWrapper> {

    static {
        Skript.registerExpression(ExprComponentChildren.class, ComponentWrapper.class, ExpressionType.PROPERTY,
                "[text] component children of %textcomponents%");
    }

    private Expression<ComponentWrapper> parents;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.parents = (Expression<ComponentWrapper>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable ComponentWrapper[] get(Event event) {
        List<ComponentWrapper> children = new ArrayList<>();
        for (ComponentWrapper parent : this.parents.getArray(event)) {
            children.addAll(parent.getChildren());
        }
        return children.toArray(new ComponentWrapper[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends ComponentWrapper> getReturnType() {
        return ComponentWrapper.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "text component children of " + this.parents.toString(e, d);
    }

}
