package com.shanebeestudios.skbee.elements.other.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.effects.Delay;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Section;
import com.shanebeestudios.skbee.api.util.Util;
import com.shanebeestudios.skbee.elements.other.expressions.ExprTransferCookie;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SecTransferCookieRetrieve extends Section {

    public static void register(Registration reg) {
        if (Skript.methodExists(Player.class, "retrieveCookie", NamespacedKey.class)) {
            reg.newSection(SecTransferCookieRetrieve.class,
                    "retrieve cookie with key %namespacedkey/string% [from %player%]")
                .name("Transfer - Retrieve Cookie")
                .description("Retrieve a cookie from a player.",
                    "Due to the retrieval process happening async, this will delay proceeding code.",
                    "While the cookie is being retrieved, the following code will wait.",
                    "NOTE: Cookies are stored across server transfers.")
                .examples("command /server <string>:",
                    "\ttrigger:",
                    "\t\tstore cookie \"%uuid of player%-transfer\" with key \"transfer\" on player",
                    "\t\ttransfer player to arg-1",
                    "",
                    "# Connect event is recommended over join event",
                    "# This way if you have to kick the player it's done before they join",
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
    }

    private Expression<?> key;
    private Expression<Player> player;

    @SuppressWarnings("unchecked")
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

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        TriggerItem next = getNext();

        // Let's save you guys for later after the cookie has loaded
        Object localVars = Variables.copyLocalVariables(event);

        Player player = this.player.getSingle(event);
        Object keyObject = this.key.getSingle(event);
        if (player == null || keyObject == null) {
            return null;
        }

        NamespacedKey key = null;
        if (keyObject instanceof String string) {
            key = Util.getNamespacedKey(string, false);
        } else if (keyObject instanceof NamespacedKey namespacedKey) {
            key = namespacedKey;
        }
        if (key == null) {
            error("Key is invalid: " + keyObject);
            return next;
        }

        player.retrieveCookie(key).thenAccept(bytes -> {
            Delay.addDelayedEvent(event); // Delay event to make sure kick effect still works
            ExprTransferCookie.setLastTransferCookie(bytes != null ? new String(bytes) : null);
            // re-set local variables
            if (localVars != null) Variables.setLocalVariables(event, localVars);

            if (this.first != null) {
                // Walk the section if there is one
                TriggerItem.walk(this.first, event);
            }

            // remove local vars as we're now done
            Variables.removeLocals(event);
            // Clear cookie data before moving on
            ExprTransferCookie.setLastTransferCookie(null);
        });
        return null;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return String.format("retrieve cookie with key %s from %s",
            this.key.toString(e, d), this.player.toString(e, d));
    }

}
