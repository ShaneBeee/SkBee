package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.elements.other.sections.SecTransferCookieRetrieve;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprTransferCookie extends SimpleExpression<String> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprTransferCookie.class, String.class,
                "transfer cookie")
            .name("Transfer - Transfer Cookie")
            .description("Represents the cookie data in a retrieve cookie section.")
            .examples("command /server <string>:",
                "\ttrigger:",
                "\t\tstore cookie \"%uuid of player%-transfer\" with key \"transfer\" on player",
                "\t\ttransfer player to arg-1",
                "",
                "on connect:",
                "\t# only do a cookie check if player was transferred",
                "\tif player is transferred:",
                "\t\tretrieve cookie with key \"transfer\" from player:",
                "\t\t\tif transfer cookie = \"%uuid of player%-transfer\":",
                "\t\t\t\t# stop code if cookie is correct",
                "\t\t\t\tstop",
                "\t\t# kick player if cookie is missing or incorrect",
                "\t\tkick player due to \"&cIllegal Transfer\"")
            .since("3.5.3")
            .register();
    }

    private static String lastTransferCookie = null;

    public static void setLastTransferCookie(String transferCookie) {
        lastTransferCookie = transferCookie;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!ParserInstance.get().isCurrentSection(SecTransferCookieRetrieve.class)) {
            Skript.error("'transfer cookie' can only be used in a 'retrieve cookie' section.");
            return false;
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected String @Nullable [] get(Event event) {
        if (lastTransferCookie != null) return new String[]{lastTransferCookie};
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "transfer cookie";
    }

}
