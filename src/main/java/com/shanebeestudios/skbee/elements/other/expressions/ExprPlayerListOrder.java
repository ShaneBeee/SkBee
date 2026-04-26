package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import com.shanebeestudios.skbee.api.skript.base.SimplePropertyExpression;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprPlayerListOrder extends SimplePropertyExpression<Player, Number> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprPlayerListOrder.class, Number.class,
                "player list order", "players")
            .name("Player List Order")
            .description("Get/set the relative order that the player is shown on the in-game player list.")
            .examples("set player list order of player to 100")
            .since("3.21.0")
            .register();
    }

    @Override
    public @Nullable Number convert(Player player) {
        return player.getPlayerListOrder();
    }

    @Override
    public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Number.class);
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {

        if (delta == null || delta.length == 0 || !(delta[0] instanceof Number num)) {
            return;
        }

        int order = Math.max(0, num.intValue());

        for (Player player : getExpr().getArray(event)) {
            player.setPlayerListOrder(order);
        }
    }

    @Override
    protected String getPropertyName() {
        return "player list order";
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

}
