package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("BlockFace - Direction")
@Description({"Gets a skript direction using a blockface with an optional distance value.",
    "Not defining a distance will default it to 0.",
    "**NOTE:** this will not work using the `self_face` block face, as Skript makes it NaN."})
@Examples({"set {_raytrace} to raytrace from player with max distance {_maxDistance}",
    "set {_direction} to direction of (hit blockface of {_raytrace})",
    "spawn zombie {_direction} player"})
@Since("INSERT VERSION")
public class ExprBlockfaceDirection extends SimpleExpression<Direction> {

    static {
        Skript.registerExpression(ExprBlockfaceDirection.class, Direction.class, ExpressionType.COMBINED,
            "direction of %blockface% [with distance %-number%]");
    }

    private Expression<BlockFace> blockFace;
    private Expression<Number> distance;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.blockFace = (Expression<BlockFace>) expressions[0];
        this.distance = (Expression<Number>) expressions[1];
        return true;
    }

    @Override
    protected Direction @Nullable [] get(Event event) {
        BlockFace blockFace = this.blockFace.getSingle(event);
        Number distance = this.distance != null ? this.distance.getOptionalSingle(event).orElse(0) : 0;
        if (blockFace == null) return new Direction[0];
        return new Direction[]{ new Direction(blockFace, distance.doubleValue())};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Direction> getReturnType() {
        return Direction.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        if (this.distance != null)
            return "direction of " + this.blockFace.toString(event, debug) + " with distance " + this.distance.toString(event, debug);
        return "direction of " + this.blockFace.toString(event, debug);
    }

}
