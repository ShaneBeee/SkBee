package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.command.EffectCommandEvent;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

@Name("Parse Effect")
@Description("This will parse a string as an effect, and execute it. Works the same as Skript's 'effect commands'.")
@Examples({"on join:",
        "\tparse effect \"give player a diamond sword\""})
@Since("INSERT VERSION")
public class EffParseEffect extends Effect {

    static {
        if (Skript.methodExists(ParserInstance.class, "get")) {
            Skript.registerEffect(EffParseEffect.class, "parse effect[s] %strings% [from %commandsender%]");
        }
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
        CommandSender sender = this.sender.getSingle(event);
        for (String s : this.effects.getArray(event)) {
            parseEffect(s, sender);
        }
    }

    private void parseEffect(String effect, CommandSender sender) {
        ParserInstance parserInstance = ParserInstance.get();
        parserInstance.setCurrentEvent("effect command", EffectCommandEvent.class);
        Effect parse = Effect.parse(effect, null);
        parserInstance.deleteCurrentEvent();
        if (parse != null) {
            TriggerItem.walk(parse, new EffectCommandEvent(sender, effect));
        }
    }

    @Override
    public String toString(Event e, boolean d) {
        return "parse effect '" + this.effects.toString(e, d) + "' from " + this.sender.toString(e, d);
    }

}
