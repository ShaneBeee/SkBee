package com.shanebeestudios.skbee.elements.text.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.chat.SignedMessage;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprSignedMessage extends SimpleExpression<SignedMessage> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprSignedMessage.class, SignedMessage.class,
                "signed [chat] message")
            .name("Signed Chat Message")
            .description("Get the signed chat message from the async chat event.",
                "This can be used to delete message from clients.")
            .examples("#Simplified Example",
                "on async chat:",
                "\tset {_msg} to signed chat message",
                "\t#...a little while later",
                "\tremove all players from {_msg}",
                "",
                "#Extended Example With Chat Formatting",
                "on async chat:",
                "\t#setup chat format",
                "\tset {_m::1} to text component from \"&7[&cx&7]\"",
                "\tset {_m::2} to text component from \" &b%player% &7» \"",
                "\tset {_m::3} to async chat message",
                "",
                "\t#setup option to delete message when clicking the \"[x]\"",
                "\tset {_msg} to signed chat message",
                "\tcreate callback for {_m::1}:",
                "\t\tif player has permission \"admin.chat.deletemessage\":",
                "\t\t\tremove all players from {_msg}",
                "",
                "\t#set chat format to our format",
                "\tset {_format} to merge components {_m::*}",
                "\tset async chat format to {_format}")
            .since("3.5.0")
            .register();
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentEvent(AsyncChatEvent.class)) {
            Skript.error("'" + parseResult.expr + "' can only be used in the Async Chat Event.");
            return false;
        }
        return true;
    }

    @Override
    protected SignedMessage @Nullable [] get(Event event) {
        if (event instanceof AsyncChatEvent chatEvent) return new SignedMessage[]{chatEvent.signedMessage()};
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends SignedMessage> getReturnType() {
        return SignedMessage.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "signed message";
    }

}
