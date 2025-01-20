package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.skript.base.SimpleExpression;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Chunk at Coords")
@Description({"Get a chunk using chunk coords.",
    "NOTE: Chunk coords are different than location coords.",
    "Chunk coords are basically location coords divided by 16.",
    "Optionally get the chunk without generating it (possibly doesn't load as well)."})
@Examples({"set {_chunk} to chunk at coords 1,1",
    "set {_chunk} to chunk at coords 1,1 in world \"world\"",
    "set {_chunk} to chunk at 50,50 in world \"world_nether\" without generating"})
@Since("2.14.0")
public class ExprChunkAt extends SimpleExpression<Chunk> {

    static {
        Skript.registerExpression(ExprChunkAt.class, Chunk.class, ExpressionType.COMBINED,
            "chunk at [coord[inate]s] %number%,[ ]%number% [(in|of) %world%] [nogen:without (generating|loading)]");
    }

    private Expression<Number> chunkX, chunkZ;
    private Expression<World> world;
    private boolean generate;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.chunkX = (Expression<Number>) exprs[0];
        this.chunkZ = (Expression<Number>) exprs[1];
        this.world = (Expression<World>) exprs[2];
        this.generate = !parseResult.hasTag("nogen");
        return true;
    }

    @Override
    protected Chunk @Nullable [] get(Event event) {
        World world = this.world.getSingle(event);
        Number x = this.chunkX.getSingle(event);
        Number z = this.chunkZ.getSingle(event);

        if (world == null || x == null || z == null) {
            return null;
        }

        Chunk chunkAt = world.getChunkAt(x.intValue(), z.intValue(), this.generate);
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
        SyntaxStringBuilder builder = new SyntaxStringBuilder(e, d);
        builder.append("chunk at coords [", this.chunkX, ",", this.chunkZ, "]");
        builder.append("in world '", this.world, "'");
        if (!this.generate) builder.append("without generating");
        return builder.toString();
    }

}
