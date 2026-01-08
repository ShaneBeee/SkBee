package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.Chunk;
import org.bukkit.Chunk.LoadLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Chunk Load Level")
@Description("Get the load level of a chunk. See type for description of levels. Requires Minecraft 1.19.4+")
@Examples({"if load level of chunk at {_loc} = entity_ticking_level:",
    "\tdrop a diamond at {_loc}"})
@Since("2.17.0")
public class ExprChunkLoadLevel extends SimplePropertyExpression<Chunk, LoadLevel> {

    static {
        register(ExprChunkLoadLevel.class, LoadLevel.class, "[chunk] load level", "chunks");
    }

    @Override
    public @Nullable LoadLevel convert(Chunk chunk) {
        return chunk.getLoadLevel();
    }

    @Override
    public @NotNull Class<? extends LoadLevel> getReturnType() {
        return LoadLevel.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "chunk load level";
    }

}
