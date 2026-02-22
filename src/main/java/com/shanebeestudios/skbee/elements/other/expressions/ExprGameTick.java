package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprGameTick extends SimplePropertyExpression<World, Long> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprGameTick.class, Long.class, "game[ ]tick[s]", "worlds")
                .name("Game Tick of World")
                .description("Represents the game ticks of a world,",
                        "essentially how many ticks this world has ticked from since creation.")
                .examples("set {_gt} to game ticks of world of player")
                .since("2.8.1")
                .register();
    }

    @Override
    public @Nullable Long convert(World world) {
        return world.getGameTime();
    }

    @Override
    public @NotNull Class<? extends Long> getReturnType() {
        return Long.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "game tick";
    }

}
