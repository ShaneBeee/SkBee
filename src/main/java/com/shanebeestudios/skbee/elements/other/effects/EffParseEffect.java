package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

@Name("Parse Effect")
@Description({"This will parse a string as an effect, and then executes it",
    "If you provide a command sender it works the same as Skript's 'effect commands'.",
    "Otherwise it runs using the current event instance allowing local variable definition"})
@Examples({"on join:",
        "\tparse effect \"give player a diamond sword\""})
@Since("1.15.0")
public class EffParseEffect extends Effect {

    static {
        Skript.registerEffect(EffParseEffect.class, "parse effect[s] %strings% [from %-commandsender%]");
    }

    private Expression<String> effects;
    private Expression<CommandSender> sender;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        effects = (Expression<String>) exprs[0];
        sender = (Expression<CommandSender>) exprs[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        CommandSender sender = this.sender != null ? this.sender.getSingle(event) : null;
        if (sender == null) {
            for (String effect : this.effects.getArray(event)) {
                SkriptUtils.parseEffect(effect, event);
            }
        } else {
            for (String effect : this.effects.getArray(event)) {
                SkriptUtils.parseEffect(effect, sender);
            }
        }
    }

    @Override
    public String toString(Event e, boolean d) {
        if (this.sender == null)
            return "parse effect '" + this.effects.toString(e, d) + "'";
        return "parse effect '" + this.effects.toString(e, d) + "' from " + this.sender.toString(e, d);
    }

}
