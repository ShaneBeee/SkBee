package com.shanebeestudios.skbee.elements.text.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("TextComponent - Send")
@Description({"Send text components to players/console. You can also broadcast components as well.",
    "As of 1.16.0 you can also send action bar components to players and you can also send normal strings.",
    "",
    "The optional sender (supported in Minecraft 1.16.4+) allows you to send components from a specific player.",
    "This is useful to make sure players can block messages using MC 1.16.4's new player chat ignore system.",
    "**NOTE**: `from %player%` is now deprecated (apparently hasn't worked in a long time)"})
@Examples({"set {_comp::1} to text component of \"hi player \"",
    "set {_comp::2} to text component of \"hover over me for a special message!\"",
    "set hover event of {_comp::2} to hover event showing \"OoO look ma I'm hovering!\"",
    "send component {_comp::*} to player",
    "send components {_comp::*} and \" ooo pretty!\" to player",
    "send actionbar component {_comp::1} to player"})
@Since("1.5.0")
public class EffSendComponent extends Effect {

    static {
        Skript.registerEffect(EffSendComponent.class,
            "send [(text|1:action[[ ]bar])] component[s] %objects% [to %commandsenders%] [from:from %-player%]",
            "broadcast [text] component[s] %objects% [from:from %-player%]");
    }

    private Expression<Object> components;
    private Expression<CommandSender> receivers;
    private boolean action;
    private boolean broadcast;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        components = LiteralUtils.defendExpression(exprs[0]);
        receivers = matchedPattern == 0 ? (Expression<CommandSender>) exprs[1] : null;
        if (parseResult.hasTag("from")) {
            Skript.warning("'from %player%' is no longer supported and will do nothing.");
        }
        action = matchedPattern == 0 && parseResult.mark == 1;
        broadcast = matchedPattern == 1;
        return LiteralUtils.canInitSafely(components);
    }

    @Override
    protected void execute(Event event) {
        if (components == null) return;

        for (Object object : this.components.getArray(event)) {
            ComponentWrapper component = ComponentWrapper.empty();
            if (object instanceof ComponentWrapper componentWrapper) {
                component = componentWrapper;
            } else if (object instanceof String string) {
                component.append(ComponentWrapper.fromText(string));
            } else {
                String string = Classes.toString(object);
                component.append(ComponentWrapper.fromText(string));
            }

            if (this.broadcast) {
                component.broadcast();
            } else {
                for (CommandSender receiver : this.receivers.getArray(event)) {
                    if (this.action) {
                        component.sendActionBar(receiver);
                    } else {
                        component.sendMessage(receiver);
                    }
                }
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        SyntaxStringBuilder builder = new SyntaxStringBuilder(e, d)
            .append(this.broadcast ? "broadcast" : "send")
            .append(this.action ? "action bar" : "text")
            .append(this.components.isSingle() ? "component" : "components")
            .append(this.components);
        if (this.receivers != null) {
            builder.append("to", this.receivers);
        }
        return builder.toString();
    }

}
