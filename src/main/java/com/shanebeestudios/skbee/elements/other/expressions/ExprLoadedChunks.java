package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Loaded Chunks")
@Description("Represents all currently loaded chunks in a world.")
@Examples({"loop loaded chunks of world of player:",
        "set {_chunks::*} to loaded chunks of world \"world\""})
@Since("1.13.0")
public class ExprLoadedChunks extends SimpleExpression<Chunk> {

    static {
        Skript.registerExpression(ExprLoadedChunks.class, Chunk.class, ExpressionType.COMBINED,
                "[(all [[of] the]|the)] loaded chunks of %world%");
    }

    private Expression<World> world;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        world = (Expression<World>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Nullable
    @Override
    protected Chunk[] get(Event event) {
        World world = this.world.getSingle(event);
        if (world != null) {
            return world.getLoadedChunks();
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends Chunk> getReturnType() {
        return Chunk.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "loaded chunks of world '" + world.toString(e, d) + "'";
    }

}
