package com.shanebeestudios.skbee.elements.text.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation") // Paper uses their own API
@Name("TextComponent - Send")
@Description({"Send text components to players/console.",
        "The optional sender (supported in Minecraft 1.16.4+) allows you to send components from a specific player.",
        "This is useful to make sure players can block messages using 1.16.4's new player chat ignore system.",
        "You can also send action bar components to players."})
@Examples({"set {_comp::1} to text component of \"hi player \"",
        "set {_comp::2} to text component of \"hover over me for a special message!\"",
        "set hover event of {_comp::2} to hover event to show \"OoO look ma I'm hovering!\"",
        "send component {_comp::*} to player"})
@Since("1.5.0")
public class EffSendComponent extends Effect {

    static {
        Skript.registerEffect(EffSendComponent.class, "send [(text|1¦action[ bar])] component[s] %basecomponents% [to %commandsenders%] [from %-player%]");
    }

    private Expression<BaseComponent> components;
    private Expression<CommandSender> players;
    @Nullable
    private Expression<Player> sender;
    private boolean action;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        components = (Expression<BaseComponent>) exprs[0];
        players = (Expression<CommandSender>) exprs[1];
        sender = (Expression<Player>) exprs[2];
        action = parseResult.mark == 1;
        return true;
    }

    @Override
    protected void execute(Event e) {
        if (components == null || players == null) return;

        Player sender = this.sender != null ? this.sender.getSingle(e) : null;

        BaseComponent[] components = this.components.getArray(e);
        for (CommandSender player : this.players.getArray(e)) {
            sendMessage(player, sender, components);
        }
    }

    private void sendMessage(CommandSender receiver, Player sender, BaseComponent... components) {
        if (action && receiver instanceof Player) {
            ((Player) receiver).sendMessage(ChatMessageType.ACTION_BAR, components);
        } else if (sender != null) {
            receiver.spigot().sendMessage(sender.getUniqueId(), components);
        } else {
            receiver.spigot().sendMessage(components);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return String.format("send %s component[s] %s to %s %s",
                action ? "action bar" : "text",
                components.toString(e, d),
                players.toString(e, d),
                sender != null ? "from " + sender.toString(e, d) : "");
    }

}
