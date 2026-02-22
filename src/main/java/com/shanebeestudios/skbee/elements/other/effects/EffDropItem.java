package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffDropItem extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffDropItem.class,
                "(make|force) %players% [to] drop [all:all of] [their] held item[s]")
            .name("Drop Held Item")
            .description("Forces the player to drop their currently held item.",
                "By default it will drop one of their held item, or you can optionally drop the whole stack.")
            .examples("make player drop all of held item",
                "force all players to drop all of their held items")
            .since("1.17.0")
            .register();
    }

    private Expression<Player> players;
    private boolean all;

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.players = (Expression<Player>) exprs[0];
        this.all = parseResult.hasTag("all");
        return true;
    }

    @Override
    protected void execute(Event event) {
        for (Player player : this.players.getArray(event)) {
            if (player.dropItem(all)) {
                // Appears the server doesn't update automatically
                player.updateInventory();
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String all = this.all ? " all of" : "";
        return "make " + this.players.toString(e, d) + " drop" + all + " held item";
    }

}
