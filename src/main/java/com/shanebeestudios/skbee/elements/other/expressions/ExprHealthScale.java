package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Player Health Scale")
@Description({"Represents the scaled health of the player sent to the client.",
    "NOTE: This is not persistent, does not last restarts/relogs.",
    "NOTE: displayedHealth = (health * 2) / (max health * 2) * scale.",
    "NOTE: If the player is not currently health scaled, this will not be set."})
@Examples({"set health scale of all players to 10",
    "add 10 to health scale of player",
    "reset health scale of player",
    "set health scale of (\"BrettPlaysMC\" parsed as offline player) to 0.01"})
@Since("2.7.2")
public class ExprHealthScale extends SimplePropertyExpression<Player, Number> {

    static {
        register(ExprHealthScale.class, Number.class, "health scale", "players");
    }

    @Override
    public @Nullable Number convert(Player player) {
        if (!player.isHealthScaled()) return null;
        return player.getHealthScale();
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.RESET) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        Number changeNumber = delta != null && delta[0] instanceof Number ? ((Number) delta[0]) : 0;
        double change = Math.max(changeNumber.doubleValue(), 1);
        for (Player player : getExpr().getArray(event)) {
            boolean scaled = true;
            double oldScale = player.getHealthScale();
            switch (mode) {
                case SET -> oldScale = change;
                case ADD -> oldScale += change;
                case REMOVE -> oldScale -= change;
                case RESET -> {
                    oldScale = 20.0;
                    scaled = false;
                }
            }
            player.setHealthScale(oldScale);
            player.setHealthScaled(scaled);
        }
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "health scale";
    }

}
