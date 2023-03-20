package com.shanebeestudios.skbee.elements.display.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.SkriptUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("DisplayEntity - Spawn")
@Description({"An effect to spawn display entities.",
        "NOTE: This is a TEMPORARY solution until Skript adds these entities."})
@Examples({"le spawn item display entity at player",
        "set item display of last spawned entity to diamond"})
@Since("INSERT VERSION")
public class EffTempSpawn extends Effect {

    static {
        Skript.registerEffect(EffTempSpawn.class,
                "(skbee|le) spawn (text|1:item|2:block) display [%directions% %locations%]");
    }

    private int pattern;
    private Expression<Location> locations;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.pattern = parseResult.mark;
        locations = Direction.combine((Expression<? extends Direction>) exprs[0], (Expression<? extends Location>) exprs[1]);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        EntityType entityType = pattern == 0 ? EntityType.TEXT_DISPLAY : pattern == 1 ? EntityType.ITEM_DISPLAY : EntityType.BLOCK_DISPLAY;
        for (Location location : this.locations.getArray(event)) {
            World world = location.getWorld();
            Entity entity = world.spawnEntity(location, entityType);
            SkriptUtils.setLastSpawned(entity);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "skbee spawn (text|item|block) display " + this.locations.toString(e, d);
    }

}
