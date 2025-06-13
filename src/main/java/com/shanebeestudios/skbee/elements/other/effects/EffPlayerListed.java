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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("List/Unlist Players")
@Description("List/unlist a player for a player. (show/hide them in PlayerList for each other). Requires PaperMC 1.20.1+")
@Examples({"unlist all players for player",
    "list (all players in world of player) for player"})
@Since("2.17.0")
public class EffPlayerListed extends Effect {

    static {
        if (Skript.methodExists(Player.class, "isListed", Player.class)) {
            Skript.registerEffect(EffPlayerListed.class, "[:un]list %players% for %players%");
        }
    }

    private boolean unlist;
    private Expression<Player> players;
    private Expression<Player> toUnlist;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.unlist = parseResult.hasTag("un");
        this.players = (Expression<Player>) exprs[1];
        this.toUnlist = (Expression<Player>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        for (Player player : this.players.getArray(event)) {
            for (Player toUnlist : this.toUnlist.getArray(event)) {
                if (this.unlist) {
                    player.unlistPlayer(toUnlist);
                } else {
                    try {
                        player.listPlayer(toUnlist);
                    } catch (IllegalStateException ignore) {
                        // Paper throws an error if the player cant see the other player?!?!?
                    }
                }
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String list = this.unlist ? "unlist " : "list ";
        return list + this.toUnlist.toString(e, d) + " for " + this.players.toString(e, d);
    }

}
