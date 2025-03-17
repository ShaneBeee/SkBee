package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Dispatch Command")
@Description({"Similar to Skript's command effect, with the option to use any command sender",
    "(vs. being restricted to only player/console), and the option to temporarily attach a permission.",
    "The attached permission will only last for 1 tick, and automatically remove. This is not persistent."})
@Examples({"dispatch player command \"give %player% stick\" with permission \"minecraft.command.give\"",
    "dispatch (random element of all mobs) command \"/tell %player% hi\""})
@Since("3.10.0")
public class EffDispatchCommand extends Effect {

    private static final SkBee PLUGIN = SkBee.getPlugin();

    static {
        Skript.registerEffect(EffDispatchCommand.class,
            "dispatch %commandsender% command %string% [with permission %-string%]");
    }

    private Expression<CommandSender> commandSender;
    private Expression<String> command;
    private Expression<String> permission;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.commandSender = (Expression<CommandSender>) exprs[0];
        this.command = (Expression<String>) exprs[1];
        this.permission = (Expression<String>) exprs[2];
        return true;
    }

    @Override
    protected void execute(Event event) {
        CommandSender commandSender = this.commandSender.getSingle(event);
        String command = this.command.getSingle(event);
        if (commandSender == null || command == null) return;

        if (this.permission != null) {
            String permission = this.permission.getSingle(event);
            if (permission == null) return;

            commandSender.addAttachment(PLUGIN, permission, true, 1);
        }
        if (command.startsWith("/")) command = command.substring(1);

        Bukkit.dispatchCommand(commandSender, command);
    }

    @Override
    public String toString(@Nullable Event e, boolean d) {
        String perm = this.permission == null ? "" : " with permission " + this.permission.getSingle(e);
        return new SyntaxStringBuilder(e, d)
            .append("dispatch", this.commandSender, "command", this.command)
            .append(perm)
            .toString();
    }

}
