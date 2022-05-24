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
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation") // Paper uses their own API
@Name("TextComponent - Send")
@Description({"Send text components to players/console. You can also broadcast components as well.",
        "The optional sender (supported in Minecraft 1.16.4+) allows you to send components from a specific player.",
        "This is useful to make sure players can block messages using 1.16.4's new player chat ignore system.",
        "As of INSERT VERSION you can also send action bar components to players and you can also send normal strings."})
@Examples({"set {_comp::1} to text component of \"hi player \"",
        "set {_comp::2} to text component of \"hover over me for a special message!\"",
        "set hover event of {_comp::2} to hover event to show \"OoO look ma I'm hovering!\"",
        "send component {_comp::*} to player",
        "send components {_comp::*} and \" ooo pretty!\" to player",
        "send actionbar component {_comp::1} to player"})
@Since("1.5.0")
public class EffSendComponent extends Effect {

    static {
        Skript.registerEffect(EffSendComponent.class,
                "send [(text|1¦action[[ ]bar])] component[s] %objects% [to %commandsenders%] [from %-player%]",
                "broadcast [text] component[s] %objects% [from %-player%]");
    }

    private Expression<Object> components;
    private Expression<CommandSender> receivers;
    @Nullable
    private Expression<Player> sender;
    private boolean action;
    private boolean broadcast;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        components = (Expression<Object>) exprs[0];
        receivers = matchedPattern == 0 ? (Expression<CommandSender>) exprs[1] : null;
        sender = (Expression<Player>) exprs[matchedPattern == 0 ? 2 : 1];
        action = matchedPattern == 0 && parseResult.mark == 1;
        broadcast = matchedPattern == 1;
        return true;
    }

    @Override
    protected void execute(Event e) {
        if (components == null) return;

        Player sender = this.sender != null ? this.sender.getSingle(e) : null;

        List<BaseComponent> components = new ArrayList<>();
        for (Object comp : this.components.getArray(e)) {
            if (comp instanceof BaseComponent) {
                components.add(((BaseComponent) comp));
            } else if (comp instanceof String) {
                components.add(new TextComponent(TextComponent.fromLegacyText((String) comp)));
            }
        }

        BaseComponent[] baseComponents = components.toArray(new BaseComponent[0]);

        if (broadcast) {
            broadcast(sender, baseComponents);
        } else {
            for (CommandSender player : this.receivers.getArray(e)) {
                sendMessage(player, sender, baseComponents);
            }
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

    private void broadcast(@Nullable Player sender, BaseComponent... components) {
        Server server = Bukkit.getServer();
        ConsoleCommandSender consoleSender = server.getConsoleSender();
        server.getOnlinePlayers().forEach(player -> sendMessage(player, sender, components));
        sendMessage(consoleSender, sender, components);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return String.format("%s %s component[s] %s to %s %s",
                broadcast ? "broadcast" : "send",
                action ? "action bar" : "text",
                components.toString(e, d),
                receivers.toString(e, d),
                sender != null ? "from " + sender.toString(e, d) : "");
    }

}
