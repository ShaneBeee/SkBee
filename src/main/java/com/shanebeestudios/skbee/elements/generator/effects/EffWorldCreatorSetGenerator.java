package com.shanebeestudios.skbee.elements.generator.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.generator.ChunkGen;
import com.shanebeestudios.skbee.api.generator.ChunkGenManager;
import com.shanebeestudios.skbee.elements.worldcreator.objects.BeeWorldCreator;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("ChunkGenerator - WorldCreator Generator")
@Description("Set the chunk generator of a world creator.")
@Examples({"on load:",
        "\tif world \"mars\" is not loaded:",
        "\t\tset {_w} to world creator named \"mars\"",
        "\t\tset chunk generator of {_w} to chunk generator with id \"mars\"",
        "\t\tload world from creator {_w}"})
@Since("3.5.0")
public class EffWorldCreatorSetGenerator extends Effect {

    static {
        Skript.registerEffect(EffWorldCreatorSetGenerator.class,
                "set chunk generator of %worldcreator% to chunk generator with id %string%");
    }

    private Expression<BeeWorldCreator> worldCreator;
    private Expression<String> id;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.worldCreator = (Expression<BeeWorldCreator>) exprs[0];
        this.id = (Expression<String>) exprs[1];
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        BeeWorldCreator worldCreator = this.worldCreator.getSingle(event);
        String id = this.id.getSingle(event);
        if (worldCreator == null || id == null) return;

        ChunkGen chunkGen = ChunkGenManager.getByID(id);
        if (chunkGen == null) return;
        worldCreator.setChunkGenerator(chunkGen.getChunkGenerator());
        worldCreator.setBiomeProvider(chunkGen.getBiomeGenerator());
        // Prevent autoloading world before generator is created
        worldCreator.setLoadOnStart(false);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String creator = this.worldCreator.toString(e, d);
        String id = this.id.toString(e, d);
        return "set chunk generator of " + creator + " to chunk generator with id " + id;
    }

}
