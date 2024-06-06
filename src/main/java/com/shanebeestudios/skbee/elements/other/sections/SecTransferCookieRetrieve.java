package com.shanebeestudios.skbee.elements.other.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.elements.other.expressions.ExprTransferCookie;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("Transfer - Retrieve Cookie")
@Description({"Retrieve a cookie from a player. Requires Minecraft 1.20.5+",
    "Due to the retrieval process happening async, this will delay proceeding code.",
    "While the cookie is being retrieved, the following code will wait.",
    "If there is no available cookie, the section will not execute.",
    "NOTE: Cookies are stored across server transfers."})
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
public class SecTransferCookieRetrieve extends Section {

    static {
        Skript.registerSection(SecTransferCookieRetrieve.class,
            "retrieve cookie with key %namespacedkey/string% from %player%");
    }

    private Expression<?> key;
    private Expression<Player> player;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        this.key = exprs[0];
        this.player = (Expression<Player>) exprs[1];
        ParserInstance parserInstance = ParserInstance.get();
        Kleenean hasDelayBefore = parserInstance.getHasDelayBefore();
        parserInstance.setHasDelayBefore(Kleenean.TRUE);
        assert parserInstance.getCurrentEvents() != null;
        loadCode(sectionNode);
        parserInstance.setHasDelayBefore(hasDelayBefore);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        TriggerItem next = getNext();

        //Let's save you guys for later after the cookie has loaded
        Object localVars = Variables.removeLocals(event);

        Player player = this.player.getSingle(event);
        Object keyObject = this.key.getSingle(event);
        if (player == null || keyObject == null) return next;

        NamespacedKey key;
        if (keyObject instanceof String string) {
            key = Util.getNamespacedKey(string, false);
        } else if (keyObject instanceof NamespacedKey namespacedKey) {
            key = namespacedKey;
        } else {
            return next;
        }
        if (key == null) return next;

        player.retrieveCookie(key).thenAccept(bytes -> {
            String cookie = new String(bytes);
            ExprTransferCookie.setLastTransferCookie(cookie);
            walkNext(localVars, event, first, next);
        }).exceptionally(throwable -> {
            walkNext(localVars, event, null, next);
            return null;
        });
        return null;
    }

    private static void walkNext(Object localVars, Event event, @Nullable TriggerItem sec, TriggerItem next) {
        //re-set local variables
        if (localVars != null) Variables.setLocalVariables(event, localVars);

        // walk section
        if (sec != null) {
            // walk the section
            TriggerItem.walk(sec, event);
            // Clear cookie data before moving on
            ExprTransferCookie.setLastTransferCookie(null);
        }

        // walk next trigger
        else if (next != null) TriggerItem.walk(next, event);

        // remove local vars as we're now done
        Variables.removeLocals(event);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return String.format("retrieve cookie with key %s from %s",
            this.key.toString(e, d), this.player.toString(e, d));
    }

}
