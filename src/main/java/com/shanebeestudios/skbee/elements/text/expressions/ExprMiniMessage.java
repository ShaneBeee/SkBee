package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.text.BeeComponent;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Text Component - MiniMessage")
@Description({"Get a mini message from a string.",
        "These messages are still components, which you can still apply hover/click events to.",
        "For more info check out the mini message page <link>https://docs.adventure.kyori.net/minimessage/format.html</link>"})
@Examples("set {_m} to mini message from \"<rainbow>this is a rainbow message\"")
@Since("INSERT VERSION")
public class ExprMiniMessage extends SimpleExpression<BeeComponent> {

    static {
        Skript.registerExpression(ExprMiniMessage.class, BeeComponent.class, ExpressionType.SIMPLE,
                "mini[ ]message from %string%");
    }

    private Expression<String> string;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.string = (Expression<String>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable BeeComponent[] get(Event event) {
        String string = this.string.getSingle(event);
        if (this.string instanceof VariableString variableString) {
            string = variableString.toUnformattedString(event);
        }
        if (string == null) return null;
        return new BeeComponent[]{BeeComponent.fromMiniMessage(string)};
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
        return "mini message from " + this.string.toString(e, d);
    }

}
