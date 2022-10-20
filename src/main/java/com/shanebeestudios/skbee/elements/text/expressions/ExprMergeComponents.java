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
import com.shanebeestudios.skbee.api.text.BeeComponent;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Text Component - Merge Components")
@Description("Merge multiple components into one.")
@Examples("set {_t} to merge components {_t::*}")
@Since("2.4.0")
public class ExprMergeComponents extends SimpleExpression<BeeComponent> {

    static {
        Skript.registerExpression(ExprMergeComponents.class, BeeComponent.class, ExpressionType.SIMPLE,
                "merge components %textcomponents%");
    }

    private Expression<BeeComponent> components;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.components = (Expression<BeeComponent>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable BeeComponent[] get(Event event) {
        BeeComponent[] components = this.components.getArray(event);
        return new BeeComponent[]{BeeComponent.fromComponents(components)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends BeeComponent> getReturnType() {
        return BeeComponent.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "merge components " + this.components.toString(e,d);
    }

}
