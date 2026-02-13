package com.shanebeestudios.skbee.elements.other.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Chunk;
import org.bukkit.Chunk.LoadLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprChunkLoadLevel extends SimplePropertyExpression<Chunk, LoadLevel> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprChunkLoadLevel.class, LoadLevel.class, "[chunk] load level", "chunks")
            .name("Chunk Load Level")
            .description("Get the load level of a chunk. See type for description of levels. Requires Minecraft 1.19.4+")
            .examples("if load level of chunk at {_loc} = entity_ticking_level:",
                "\tdrop a diamond at {_loc}")
            .since("2.17.0")
            .register();
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
