package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

public class EffParseEffect extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffParseEffect.class, "parse effect[s] %strings% [from %-commandsender%]")
            .name("Parse Effect")
            .description("This will parse a string as an effect, and then executes it",
                "If you provide a command sender it works the same as Skript's 'effect commands'.",
                "Otherwise it runs using the current event allowing you to use event-values")
            .examples("on join:",
                "\tparse effect \"give player a diamond sword\"")
            .since("1.15.0")
            .register();
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
                SkriptUtils.parseEffect(effect, sender, event);
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
