package com.shanebeestudios.skbee.elements.fishing.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Fish Hook - Current")
@Description("Get the current fish hook attached to a player's fishing rod.")
@Examples("delete current fish hook of player")
@Since("2.8.4")
public class ExprFishHookOfPlayer extends SimplePropertyExpression<Player, Entity> {

    static {
        if (Skript.methodExists(HumanEntity.class, "getFishHook")) {
            register(ExprFishHookOfPlayer.class, Entity.class, "(current|attached) fish[ing] hook[s]", "players");
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
