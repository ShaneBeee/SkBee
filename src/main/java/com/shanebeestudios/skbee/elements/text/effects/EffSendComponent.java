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
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Name("TextComponent - Send")
@Description({"Send text components to players (or console, not sure which server version this started on). ",
        "The optional sender (supported in Minecraft 1.16.4+) allows you to send components from a specific player.",
        "This is useful to make sure players can block messages using 1.16.4's new player chat ignore system."})
@Examples({"set {_comp::1} to text component of \"hi player \"",
        "set {_comp::2} to text component of \"hover over me for a special message!\"",
        "set hover event of {_comp::2} to hover event to show \"OoO look ma I'm hovering!\"",
        "send component {_comp::*} to player"})
@Since("1.5.0")
public class EffSendComponent extends Effect {

    private static final boolean SUPPORTS_COMMAND_SENDER;
    private static final boolean SUPPORTS_SENDER;

    static {
        SUPPORTS_COMMAND_SENDER = Skript.classExists("org.bukkit.command.CommandSender$Spigot");
        SUPPORTS_SENDER = SUPPORTS_COMMAND_SENDER &&
                Skript.methodExists(CommandSender.Spigot.class, "sendMessage", UUID.class, BaseComponent.class);
        if (SUPPORTS_SENDER) {
            Skript.registerEffect(EffSendComponent.class, "send [text] component[s] %basecomponents% [to %commandsenders%] [from %-player%]");
        } else if (SUPPORTS_COMMAND_SENDER) {
            Skript.registerEffect(EffSendComponent.class, "send [text] component[s] %basecomponents% [to %commandsenders%]");
        } else {
            Skript.registerEffect(EffSendComponent.class, "send [text] component[s] %basecomponents% [to %players%]");
        }
    }

    private Expression<BaseComponent> components;
    private Expression<CommandSender> players;
    @Nullable
    private Expression<Player> sender;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        components = (Expression<BaseComponent>) exprs[0];
        players = (Expression<CommandSender>) exprs[1];
        sender = SUPPORTS_SENDER ? (Expression<Player>) exprs[2] : null;
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
        if (SUPPORTS_SENDER && sender != null) {
            receiver.spigot().sendMessage(sender.getUniqueId(), components);
        } else if (SUPPORTS_COMMAND_SENDER) {
            receiver.spigot().sendMessage(components);
        } else if (receiver instanceof Player) {
            ((Player) receiver).spigot().sendMessage(components);
        }

    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return String.format("send component[s] %s to %s %s",
                components.toString(e, d),
                players.toString(e, d),
                sender != null ? "from " + sender.toString(e, d) : "");
    }

}
