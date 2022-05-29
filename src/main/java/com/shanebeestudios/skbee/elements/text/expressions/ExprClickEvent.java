package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
@Name("Text Component - Click Event")
@Description("Create a new click event. Supports run command, suggest command, open link and copy to clipboard.")
@Examples({"set {_t} to text component from \"Check out my cool website\"",
        "set hover event of {_t} to a new hover event showing \"Clicky clicky to go to spawn!\"",
        "set click event of {_t} to a new click event to open url \"https://my.cool.website\"",
        "send component {_t} to player"})
@Since("1.5.0")
public class ExprClickEvent extends SimpleExpression<ClickEvent> {

    private static final boolean SUPPORTS_CLIPBOARD;

    static {
        SUPPORTS_CLIPBOARD = Skript.fieldExists(ClickEvent.Action.class, "COPY_TO_CLIPBOARD");
        Skript.registerExpression(ExprClickEvent.class, ClickEvent.class, ExpressionType.COMBINED,
                "[a] [new] click event to run command %string%",
                "[a] [new] click event to suggest command %string%",
                "[a] [new] click event to open (link|url) %string%",
                "[a] [new] click event to copy %string% to clipboard",
                "[a] [new] click event to change to page %number%");
    }

    private int pattern;
    private Expression<Object> object;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.pattern = matchedPattern;
        if (pattern == 3 && !SUPPORTS_CLIPBOARD) {
            Skript.error("'click event to copy %string% to clipboard' is not supported on your server version", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        object = (Expression<Object>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected ClickEvent[] get(Event event) {
        if (object == null) return null;

        Object value = object.getSingle(event);
        Action action;

        switch (pattern) {
            case 1 -> action = Action.SUGGEST_COMMAND;
            case 2 -> action = Action.OPEN_URL;
            case 3 -> action = Action.COPY_TO_CLIPBOARD;
            case 4 -> {
                action = Action.CHANGE_PAGE;
                value = "" + (((Number) object.getSingle(event)).intValue());
            }
            default -> action = Action.RUN_COMMAND;
        }
        return new ClickEvent[]{new ClickEvent(action, (String) value)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends ClickEvent> getReturnType() {
        return ClickEvent.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String[] actions = new String[]{"run command", "suggest command", "open url", "copy to clipboard", "change to page"};
        return "click event to " + actions[pattern] + " " + object.toString(e, d);
    }

}
