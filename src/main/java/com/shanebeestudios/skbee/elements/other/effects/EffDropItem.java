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
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Drop Held Item")
@Description({"Forces the player to drop their currently held item.",
        "By default it will drop one of their held item, or you can optionally drop the whole stack."})
@Examples({"make player drop all of held item",
        "force all players to drop all of their held items"})
@Since("1.17.0")
public class EffDropItem extends Effect {

    static {
        Skript.registerEffect(EffDropItem.class,
                "(make|force) %players% [to] drop [all:all of] [their] held item[s]");
    }

    private Expression<Player> players;
    private boolean all;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.players = (Expression<Player>) exprs[0];
        this.all = parseResult.hasTag("all");
        return true;
    }

    @SuppressWarnings("NullableProblems")
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
