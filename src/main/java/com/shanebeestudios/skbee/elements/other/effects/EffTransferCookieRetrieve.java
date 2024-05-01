package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Transfer - Retrieve Cookie")
@Description({"Retrieve a cookie from a player and store it in a variable. Requires Minecraft 1.20.5+",
    "Due to the retrieval process happening async, this needs to be stored in a variable.",
    "While the cookie is being retrieved, your proceeding code will wait.",
    "NOTE: Cookies are stored across server transfers."})
@Examples({"retrieve cookie with key \"my_id:super_mom_cookie\" from player and store in {_cookie}",
    "broadcast {_cookie}"})
@Since("3.5.0")
public class EffTransferCookieRetrieve extends Effect {

    static {
        if (Skript.methodExists(Player.class, "isTransferred")) {
            Skript.registerEffect(EffTransferCookieRetrieve.class,
                "retrieve cookie with key %namespacedkey/string% from %player% and store in %object%");
        }
    }

    private Expression<?> key;
    private Expression<Player> player;
    private Expression<?> object;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.key = exprs[0];
        this.player = (Expression<Player>) exprs[1];
        this.object = exprs[2];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        // We'll walk instead
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
            this.object.change(event, new Object[]{cookie}, ChangeMode.SET);
            walkNext(localVars, event, next);
        }).exceptionally(throwable -> {
            walkNext(localVars, event, next);
            return null;
        });
        return null;
    }

    private static void walkNext(Object localVars, Event event, TriggerItem next) {
        //re-set local variables
        if (localVars != null) Variables.setLocalVariables(event, localVars);

        // walk next trigger
        if (next != null) TriggerItem.walk(next, event);

        // remove local vars as we're now done
        Variables.removeLocals(event);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return String.format("retrieve cookie with key %s from %s and store in %s",
            this.key.toString(e, d), this.player.toString(e, d), this.object.toString(e, d));
    }

}
