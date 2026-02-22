package com.shanebeestudios.skbee.elements.text.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import net.kyori.adventure.audience.Audience;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffSendComponent extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffSendComponent.class,
                "send [(text|1:action[[ ]bar])] component[s] %objects% [to %audiences%]",
                "broadcast [text] component[s] %objects%")
            .name("TextComponent - Send")
            .description("Send text components to audiences. You can also broadcast components as well.",
                "As of 1.16.0 you can also send action bar components to players and you can also send normal strings.",
                "`to %audiences%` = An audience is anything that can receieve a component (players, entities, console, worlds, server, etc).",
                "",
                "The optional sender (supported in Minecraft 1.16.4+) allows you to send components from a specific player.",
                "This is useful to make sure players can block messages using MC 1.16.4's new player chat ignore system.")
            .examples("set {_comp::1} to text component of \"hi player \"",
                "set {_comp::2} to text component of \"hover over me for a special message!\"",
                "set hover event of {_comp::2} to hover event showing \"OoO look ma I'm hovering!\"",
                "send component {_comp::*} to player",
                "send components {_comp::*} and \" ooo pretty!\" to player",
                "send actionbar component {_comp::1} to player",
                "",
                "# Send a message to your fellow teammates",
                "command /teambroadcast <msg:string>:",
                "\ttrigger:",
                "\t\tset {_team} to team of player",
                "\t\tset {_mini} to mini message from \"[TeamMessage from %player%] %{_msg}%\"",
                "\t\tsend component {_mini} to {_team}")
            .since("1.5.0")
            .register();
    }

    private Expression<Object> components;
    private Expression<Audience> audiences;
    private boolean action;
    private boolean broadcast;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.components = LiteralUtils.defendExpression(exprs[0]);
        this.audiences = matchedPattern == 0 ? (Expression<Audience>) exprs[1] : null;
        this.action = matchedPattern == 0 && parseResult.mark == 1;
        this.broadcast = matchedPattern == 1;
        return LiteralUtils.canInitSafely(components);
    }

    @Override
    protected void execute(Event event) {
        if (components == null) return;

        for (Object object : this.components.getArray(event)) {
            ComponentWrapper component;
            if (object instanceof ComponentWrapper componentWrapper) {
                component = componentWrapper;
            } else if (object instanceof String string) {
                component = ComponentWrapper.fromText(string);
            } else {
                String string = Classes.toString(object);
                component = ComponentWrapper.fromText(string);
            }

            if (this.broadcast) {
                component.broadcast();
            } else {
                for (Audience audience : this.audiences.getArray(event)) {
                    if (this.action) {
                        component.sendActionBar(audience);
                    } else {
                        component.sendMessage(audience);
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
        if (this.audiences != null) {
            builder.append("to", this.audiences);
        }
        return builder.toString();
    }

}
