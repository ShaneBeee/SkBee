package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class ExprBoundWorld extends SimplePropertyExpression<Bound, World> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprBoundWorld.class, World.class,
                "bound world", "bounds")
            .name("Bound - World")
            .description("Get the world of a bound.")
            .examples("set {world} to bound world of bound with id \"el-boundo\"")
            .since("3.8.0")
            .register();
    }

    @Override
    public World convert(Bound bound) {
        return bound.getWorld();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "bound world";
    }

    @Override
    public @NotNull Class<? extends World> getReturnType() {
        return World.class;
    }

}
