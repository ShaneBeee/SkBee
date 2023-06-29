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

@Name("Chunk at Coords")
@Description("Get a chunk using chunk coords.")
@Examples({"set {_chunk} to chunk at 1,1",
        "set {_chunk} to chunk at 1,1 in world \"world\""})
@Since("INSERT VERSION")
public class ExprChunkAt extends SimpleExpression<Chunk> {

    static {
        Skript.registerExpression(ExprChunkAt.class, Chunk.class, ExpressionType.COMBINED,
                "chunk at %number%,[ ]%number% [(in|of) %world%]");
    }

    private Expression<Number> chunkX, chunkZ;
    private Expression<World> world;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.chunkX = (Expression<Number>) exprs[0];
        this.chunkZ = (Expression<Number>) exprs[1];
        this.world = (Expression<World>) exprs[2];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Chunk[] get(Event event) {
        World world = this.world.getSingle(event);
        Number chunkXNum = this.chunkX.getSingle(event);
        Number chunkZNum = this.chunkZ.getSingle(event);

        if (world == null || chunkXNum == null || chunkZNum == null) return null;

        int chunkX = chunkXNum.intValue();
        int chunkZ = chunkZNum.intValue();

        Chunk chunkAt = world.getChunkAt(chunkX, chunkZ);
        return new Chunk[]{chunkAt};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Chunk> getReturnType() {
        return Chunk.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String world = this.world != null ? ("in " + this.world.toString(e,d)) : "";
        return "chunk at " + this.chunkX.toString(e,d) + "," + this.chunkZ.toString(e,d) + world;
    }

}
