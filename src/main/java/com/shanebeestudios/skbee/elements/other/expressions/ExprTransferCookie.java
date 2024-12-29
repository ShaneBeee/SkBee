package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.elements.other.sections.SecTransferCookieRetrieve;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Transfer - Transfer Cookie")
@Description("Represents the cookie data in a transfer section.")
@Examples({"command /server <string>:",
    "\ttrigger:",
    "\t\tstore cookie \"%uuid of player%-transfer\" with key \"transfer\" on player",
    "\t\ttransfer player to arg-1",
    "",
    "on join:",
    "\t# only do a cookie check if player was transferred",
    "\tif player is transferred:",
    "\t\tretrieve cookie with key \"transfer\" from player:",
    "\t\t\tif transfer cookie = \"%uuid of player%-transfer\":",
    "\t\t\t\t# stop code if cookie is correct",
    "\t\t\t\tstop",
    "\t\t# kick player if cookie is missing or incorrect",
    "\t\tkick player due to \"&cIllegal Transfer\""})
@Since("3.5.3")
public class ExprTransferCookie extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprTransferCookie.class, String.class, ExpressionType.SIMPLE,
            "transfer cookie");
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
