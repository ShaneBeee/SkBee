package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.bound.Bound;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

@Name("Bound - World")
@Description("Get the world of a bound.")
@Examples("set {world} to bound world of bound with id \"el-boundo\"")
@Since("INSERT VERSION")
public class ExprBoundWorld extends SimplePropertyExpression<Bound, World> {

    static {
        register(ExprBoundWorld.class, World.class, "bound world", "bounds");
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
