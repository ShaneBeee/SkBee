package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.event.server.TabCompleteEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Tab Completion Argument")
@Description("Get the argument or a list of all arguments in a tab complete event.")
@Examples({"on tab complete of \"/breakfast\":",
        "\tset tab completions for position 1 to \"toast\", \"eggs\" and \"waffles\"",
        "\tif tab arg-1 = \"toast\":",
        "\t\tset tab completions for position 2 to \"butter\", \"peanut_butter\" and \"jam\"",
        "\telse if tab arg-1 = \"eggs\":",
        "\t\tset tab completions for position 2 to \"sunny_side_up\", \"scrambled\" and \"over_easy\"",
        "", "set {_l::*} to tab args"})
@Since("1.7.0")
public class ExprTabCompletionArgs extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprTabCompletionArgs.class, String.class, ExpressionType.SIMPLE,
                "tab [complete] arg[ument](0¦s|1¦[(-| )]%number%)");
    }

    private int pattern;
    private Expression<Number> position;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(TabCompleteEvent.class)) {
            Skript.error("Tab completion arguments are only usable in a tab complete event.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        pattern = parseResult.mark;
        position = pattern == 1 ? (Expression<Number>) exprs[0] : null;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected String[] get(@NotNull Event e) {
        TabCompleteEvent event = ((TabCompleteEvent) e);
        String buffer = event.getBuffer();
        String[] buffers = buffer.split(" ");

        if (pattern == 0) {
            String[] args = new String[buffers.length - 1];
            if (buffers.length - 1 >= 0)
                System.arraycopy(buffers, 1, args, 0, buffers.length - 1);
            return args;
        } else if (pattern == 1) {
            int position = this.position.getSingle(e).intValue();
            if (buffers.length >= position + 1) {
                return new String[]{buffers[position]};
            }
        }
        return new String[0];
    }

    @Override
    public boolean isSingle() {
        return this.pattern == 1;
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String pos = pattern == 1 ? "-" + position.toString(e, d) : "s";
        return "tab complete arg" + pos;
    }

}
