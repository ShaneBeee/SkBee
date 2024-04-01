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
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Text Component - MiniMessage")
@Description({"Get a mini message from a string.",
        "These messages are still components, which you can still apply hover/click events to.",
        "You can also add optional tag resolvers. Essential you create a resolver to replace `<someString>` ",
        "in mini message with something else (See examples for more details).",
        "For more info check out the mini message page <link>https://docs.adventure.kyori.net/minimessage/format.html</link>"})
@Examples({"set {_m} to mini message from \"<rainbow>this is a rainbow message\"",
        "set {_m} to mini message from \"<gradient:##F30A0A:##0A2AF3>PRETTY MESSAGE FROM RED TO BLUE\"",
        "set {_m} to mini message from \"<red>This is a <green>test!\"",
        "send component mini message from \"<red>This is a <green>test!\" to all players",
        "",
        "# Create a component",
        "set {_i} to translate component of player's tool",
        "# Use this comonent in the resolver to replace \"<item>\" in the mini message",
        "set {_r::1} to resolver(\"item\", {_i})",
        "# setup the mini message with the replacement placeholder",
        "set {_m} to mini message from \"<rainbow> Hey guys check out my <item> aint she a beaut?\" with {_r::*}",
        "send component {_m}"})
@Since("2.4.0")
public class ExprMiniMessage extends SimpleExpression<ComponentWrapper> {

    static {
        Skript.registerExpression(ExprMiniMessage.class, ComponentWrapper.class, ExpressionType.SIMPLE,
                "mini[ ]message from %string% [with [resolver[s]] %-tagresolvers%]");
    }

    private Expression<String> string;
    private Expression<TagResolver> resolvers;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!Skript.classExists("net.kyori.adventure.text.minimessage.MiniMessage")) {
            Skript.error("It appears MiniMessage isn't available on your server version.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        this.string = (Expression<String>) exprs[0];
        this.resolvers = (Expression<TagResolver>) exprs[1];
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
        TagResolver[] resolvers = this.resolvers != null ? this.resolvers.getArray(event) : null;
        return new ComponentWrapper[]{ComponentWrapper.fromMiniMessage(string, resolvers)};
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
    public @NotNull String toString(Event e, boolean d) {
        String resolvers = this.resolvers != null ? (" with resolvers " + this.resolvers.toString(e, d)) : "";
        return "mini message from " + this.string.toString(e, d) + resolvers;
    }

}
