package com.shanebeestudios.skbee.elements.fishing.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprFishHookOfPlayer extends SimplePropertyExpression<Player, Entity> {

    public static void register(Registration reg) {
        if (Skript.methodExists(HumanEntity.class, "getFishHook")) {
            reg.newPropertyExpression(ExprFishHookOfPlayer.class, Entity.class,
                "(current|attached) fish[ing] hook[s]", "players")
                .name("Fish Hook - Current")
                .description("Get the current fish hook attached to a player's fishing rod.")
                .examples("delete current fish hook of player")
                .since("2.8.4")
                .register();
        }
    }

    @Override
    public @Nullable Entity convert(Player player) {
        return player.getFishHook();
    }

    @Override
    public @NotNull Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "current fish hook";
    }

}
