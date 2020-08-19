package tk.shanebee.bee.elements.text.expressions;

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
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Name("Text Component - New Text Component")
@Description("Create a new text component. Can have hover and click events added to it.")
@Examples({"set {_comp::1} to text component from \"hi player \"",
        "set {_comp::2} to text component of \"hover over me for a special message!\"",
        "set hover event of {_comp::2} to hover event to show \"OoO look ma I'm hovering!\"",
        "send component {_comp::*} to player"})
@Since("1.5.0")
public class ExprTextComponent extends SimpleExpression<TextComponent> {

    static {
        Skript.registerExpression(ExprTextComponent.class, TextComponent.class, ExpressionType.COMBINED,
                "[a] [new] text component[s] (from|of) %strings%");
    }

    private Expression<String> strings;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        strings = (Expression<String>) exprs[0];
        return true;
    }

    @Nullable
    @Override
    protected TextComponent[] get(Event e) {
        List<TextComponent> components = new ArrayList<>();
        for (String string : this.strings.getArray(e)) {
            components.add(new TextComponent(string));
        }
        return components.toArray(new TextComponent[0]);
    }

    @Override
    public boolean isSingle() {
        return this.strings.isSingle();
    }

    @Override
    public Class<? extends TextComponent> getReturnType() {
        return TextComponent.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        return "a new text component from " + strings.toString(e, d);
    }

}
