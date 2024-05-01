package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("Transfer - Store Cookie")
@Description({"Store a cookie on a player for transfer. Requires Minecraft 1.20.5+",
    "NOTE: Only a string less than 5120 characters is accepted.",
    "NOTE: Cookies are stored on players across server transfers."})
@Examples("store cookie \"look mah imma cookie\" with key \"my_id:super_mom_cookie\" on player")
@Since("3.5.0")
public class EffTransferCookieStore extends Effect {

    static {
        if (Skript.methodExists(Player.class, "isTransferred")) {
            Skript.registerEffect(EffTransferCookieStore.class,
                "store cookie %string% with key %namespacedkey/string% on %players%");
        }
    }

    private Expression<String> cookie;
    private Expression<?> key;
    private Expression<Player> players;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.cookie = (Expression<String>) exprs[0];
        this.key = exprs[1];
        this.players = (Expression<Player>) exprs[2];

        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        String cookie = this.cookie.getSingle(event);
        Object keyObject = this.key.getSingle(event);
        if (cookie == null || keyObject == null) return;

        NamespacedKey key;
        if (keyObject instanceof String string) {
            key = Util.getNamespacedKey(string, false);
        } else if (keyObject instanceof NamespacedKey namespacedKey) {
            key = namespacedKey;
        } else {
            return;
        }
        if (key == null) return;

        byte[] cookieBytes = cookie.getBytes();
        // Minecraft limit
        if (cookieBytes.length >= 5120) return;

        for (Player player : this.players.getArray(event)) {
            player.storeCookie(key, cookieBytes);
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return String.format("store cookie %s with key %s on %s",
            this.cookie.toString(e, d), this.key.toString(e, d), this.players.toString(e, d));
    }

}
