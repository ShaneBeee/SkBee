package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprWorldHeight extends SimplePropertyExpression<World, Number> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprWorldHeight.class, Number.class, "(min|:max) height", "worlds")
            .name("Min/Max World Height")
            .description("Get the min/max height of a world.")
            .examples("set {_height} to max height of world of player")
            .since("2.14.0")
            .register();
    }

    private boolean max;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.max = parseResult.hasTag("max");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Number convert(World world) {
        return this.max ? world.getMaxHeight() : world.getMinHeight();
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return (this.max ? "max" : "min") + " height";
    }

}
