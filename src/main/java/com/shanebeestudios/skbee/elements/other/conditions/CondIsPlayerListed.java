package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Is Player Listed")
@Description("Check if a player is listed (shown in PlayerList) for another player. Requires PaperMC 1.20.1+")
@Examples("if player is listed for {_p}:")
@Since("INSERT VERSION")
public class CondIsPlayerListed extends Condition {

    static {
        if (Skript.methodExists(Player.class, "isListed", Player.class)) {
            Skript.registerCondition(CondIsPlayerListed.class, "%players% (is|are) listed for %players%",
                    "%players% (is|are)(n't| not) listed for %players%");
        }
    }

    private boolean not;
    private Expression<Player> players;
    private Expression<Player> listed;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.not = matchedPattern == 1;
        this.players = (Expression<Player>) exprs[1];
        this.listed = (Expression<Player>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean check(Event event) {
        for (Player player : this.players.getArray(event)) {
            for (Player listed : this.listed.getArray(event)) {
                if (player.isListed(listed) && this.not) return false;
                else if (!player.isListed(listed) && !this.not) return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String plural = this.listed.isSingle() ? " is" : " are";
        String neg = this.not ? " not" : "";
        return this.listed.toString(e, d) + plural + neg + " listed for " + this.players.toString(e, d);
    }

}
