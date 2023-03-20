package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Game Tick of World")
@Description({"Represents the game ticks of a world,",
        "essentially how many ticks this world has ticked from since creation."})
@Examples("set {_gt} to game ticks of world of player")
@Since("INSERT VERSION")
public class ExprGameTick extends SimplePropertyExpression<World, Long> {

    static {
        register(ExprGameTick.class, Long.class, "game[ ]tick[s]", "worlds");
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
