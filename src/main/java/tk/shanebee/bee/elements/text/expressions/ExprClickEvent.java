package tk.shanebee.bee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

@Name("Text Component - Click Event")
@Description("Create a new click event. Supports run command, suggest command, open link and copy to clipboard.")
@Examples({"set {_t} to text component from \"Check out my cool website\"",
        "set hover event of {_t} to a new hover event showing \"Clicky clicky to go to spawn!\"",
        "set click event of {_t} to a new click event to open url \"https://my.cool.website\"",
        "send component {_t} to player"})
@Since("1.5.0")
public class ExprClickEvent extends SimpleExpression<ClickEvent> {

    static {
        Skript.registerExpression(ExprClickEvent.class, ClickEvent.class, ExpressionType.COMBINED,
                "[a] [new] click event to run command %string%",
                "[a] [new] click event to suggest command %string%",
                "[a] [new] click event to open (link|url) %string%",
                "[a] [new] click event to copy %string% to clipboard");
    }

    private int pattern;
    private Expression<Object> object;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.pattern = matchedPattern;
        object = (Expression<Object>) exprs[0];
        return true;
    }

    @Nullable
    @Override
    protected ClickEvent[] get(Event e) {
        if (object == null) return null;

        String value = (String) object.getSingle(e);
        Action action;

        switch (pattern) {
            case 1:
                action = Action.SUGGEST_COMMAND;
                break;
            case 2:
                action = Action.OPEN_URL;
                break;
            case 3:
                action = Action.COPY_TO_CLIPBOARD;
                break;
            default:
                action = Action.RUN_COMMAND;
        }
        return new ClickEvent[]{new ClickEvent(action, value)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ClickEvent> getReturnType() {
        return ClickEvent.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        String[] actions = new String[]{"run command", "suggest command", "open url", "copy to clipboard"};
        return "click event to " + actions[pattern] + " " + object.toString(e, d);
    }

}
