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
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Text Component - MiniMessage")
@Description({"Get a mini message from a string.",
        "These messages are still components, which you can still apply hover/click events to.",
        "For more info check out the mini message page <link>https://docs.adventure.kyori.net/minimessage/format.html</link>"})
@Examples({"set {_m} to mini message from \"<rainbow>this is a rainbow message\"",
        "set {_m} to mini message from \"<gradient:##F30A0A:##0A2AF3>PRETTY MESSAGE FROM RED TO BLUE\"",
        "set {_m} to mini message from \"<red>This is a <green>test!\"",
        "send component mini message from \"<red>This is a <green>test!\" to all players"})
@Since("2.4.0")
public class ExprMiniMessage extends SimpleExpression<ComponentWrapper> {

    static {
        Skript.registerExpression(ExprMiniMessage.class, ComponentWrapper.class, ExpressionType.SIMPLE,
                "mini[ ]message from %string%");
    }

    private Expression<String> string;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!Skript.classExists("net.kyori.adventure.text.minimessage.MiniMessage")) {
            Skript.error("It appears MiniMessage isn't available on your server version.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        this.string = (Expression<String>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable ComponentWrapper @Nullable [] get(Event event) {
        String string = this.string.getSingle(event);
        if (this.string instanceof VariableString variableString) {
            string = variableString.toUnformattedString(event);
        }
        if (string == null) return null;
        return new ComponentWrapper[]{ComponentWrapper.fromMiniMessage(string)};
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
        return "mini message from " + this.string.toString(e, d);
    }

}
