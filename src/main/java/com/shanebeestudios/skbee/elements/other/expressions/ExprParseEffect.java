package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Parse Effect - With Return")
@Description({"This will parse a string as an effect and then execute it and returns whether or not it executed.",
    "If you provide a command sender it works the same as Skript's 'effect commands'.",
    "Otherwise it runs using the current event allowing you to use event-values"})
@Examples({"command /parse <string>:",
    "\ttrigger:",
    "\t\tif parse effect arg-1 = false:",
    "\t\t\tsend \"ERROR: %arg-1%\""})
@Since("2.15.0")
public class ExprParseEffect extends SimpleExpression<Boolean> {

    static {
        Skript.registerExpression(ExprParseEffect.class, Boolean.class, ExpressionType.COMBINED,
            "parse effect[s] %strings% [from %-commandsender%]");
    }

    private Expression<String> effects;
    private Expression<CommandSender> sender;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.effects = (Expression<String>) exprs[0];
        this.sender = (Expression<CommandSender>) exprs[1];
        return true;
    }

    @Override
    protected @Nullable Boolean[] get(Event event) {
        boolean parsed = true;
        CommandSender sender = this.sender != null ? this.sender.getSingle(event) : null;
        if (sender == null) {
            for (String string : this.effects.getArray(event)) {
                if (!SkriptUtils.parseEffect(string, event)) {
                    parsed = false;
                }
            }
        } else {
            for (String string : this.effects.getArray(event)) {
                if (!SkriptUtils.parseEffect(string, sender, event)) {
                    parsed = false;
                }
            }
        }
        return new Boolean[]{parsed};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        SyntaxStringBuilder builder = new SyntaxStringBuilder(e, d)
            .append("parse effect `", this.effects, "'");
        if (this.sender != null) {
            builder.append("from", this.sender);
        }

        return builder.toString();
    }

}
