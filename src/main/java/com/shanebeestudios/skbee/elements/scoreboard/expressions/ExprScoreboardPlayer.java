package com.shanebeestudios.skbee.elements.scoreboard.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.scoreboard.ScoreboardUtils;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Nullable;

public class ExprScoreboardPlayer extends SimplePropertyExpression<Player, Scoreboard> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprScoreboardPlayer.class, Scoreboard.class, "scoreboard", "players")
            .name("Scoreboard - Scoreboard of Player")
            .description("Get/set the scoreboard of players.",
                "By default all players share the same main scoreboard unless given a new scoreboard.",
                "Do note that custom scoreboards are not persistent.",
                "Reset will reset the player's scoreboard back to the main server scoreboard.")
            .examples("set {_scoreboard} to scoreboard of player",
                "set scoreboard of player to a new scoreboard",
                "set scoreboard of player to the vanilla scoreboard",
                "reset scoreboard of player")
            .since("3.9.0")
            .register();
    }

    @Override
    public @Nullable Scoreboard convert(Player player) {
        return player.getScoreboard();
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Scoreboard.class);
        if (mode == ChangeMode.RESET) return CollectionUtils.array();
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
        Scoreboard scoreboard = delta != null && delta[0] instanceof Scoreboard sb ? sb : null;
        for (Player player : getExpr().getArray(event)) {
            if (mode == ChangeMode.SET) {
                if (scoreboard == null) {
                    continue;
                }
                player.setScoreboard(scoreboard);
            } else if (mode == ChangeMode.RESET) {
                player.setScoreboard(ScoreboardUtils.getMainScoreboard());
            }
        }
    }

    @Override
    protected String getPropertyName() {
        return "scoreboard";
    }

    @Override
    public Class<? extends Scoreboard> getReturnType() {
        return Scoreboard.class;
    }

}
