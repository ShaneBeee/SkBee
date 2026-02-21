package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class EffTransferCookieStore extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffTransferCookieStore.class,
                "store cookie %string% with key %namespacedkey/string% on %players%")
            .name("Transfer - Store Cookie")
            .description("Store a cookie on a player for transfer. Requires Minecraft 1.20.5+",
                "NOTE: Only a string less than 5120 characters is accepted.",
                "NOTE: Cookies are stored on players across server transfers.")
            .examples("store cookie \"look mah imma cookie\" with key \"my_id:super_mom_cookie\" on player")
            .since("3.5.0")
            .register();
    }

    private Expression<String> cookie;
    private Expression<?> key;
    private Expression<Player> players;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.cookie = (Expression<String>) exprs[0];
        this.key = exprs[1];
        this.players = (Expression<Player>) exprs[2];

        return true;
    }

    @Override
    protected void execute(Event event) {
        String cookie = this.cookie.getSingle(event);
        Object keyObject = this.key.getSingle(event);
        if (cookie == null || keyObject == null) {
            return;
        }

        NamespacedKey key = null;
        if (keyObject instanceof String string) {
            key = Util.getNamespacedKey(string, false);
        } else if (keyObject instanceof NamespacedKey namespacedKey) {
            key = namespacedKey;
        }
        if (key == null) {
            error("Invalid key: " + this.key.toString(event, true));
            return;
        }

        byte[] cookieBytes = cookie.getBytes();
        // Minecraft limit
        if (cookieBytes.length >= 5120) {
            error("Your cookie \uD83C\uDF6A is too big. Minecraft limits to 5120 characters.");
            return;
        }

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
