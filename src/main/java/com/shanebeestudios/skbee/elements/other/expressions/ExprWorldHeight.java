package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Min/Max World Height")
@Description("Get the min/max height of a world.")
@Examples("set {_height} to max height of world of player")
@Since("2.14.0")
public class ExprWorldHeight extends SimplePropertyExpression<World, Number> {

    static {
        register(ExprWorldHeight.class, Number.class, "(min|:max) height", "worlds");
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
