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
@Description({"An effect to spawn display/interaction entities.",
        "NOTE: This is a TEMPORARY solution until Skript adds these entities."})
@Examples({"le spawn item display at player",
        "set display item of last spawned entity to diamond"})
@Since("2.8.0")
public class EffTempSpawn extends Effect {

    static {
        Skript.registerEffect(EffTempSpawn.class,
                "(skbee|le) spawn ((text|1:item|2:block) display|3:interaction) [entity] [%directions% %locations%]");
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
        EntityType entityType = switch (pattern) {
            case 1 -> EntityType.ITEM_DISPLAY;
            case 2 -> EntityType.BLOCK_DISPLAY;
            case 3 -> EntityType.INTERACTION;
            default -> EntityType.TEXT_DISPLAY;
        };
        for (Location location : this.locations.getArray(event)) {
            World world = location.getWorld();
            Entity entity = world.spawnEntity(location, entityType);
            SkriptUtils.setLastSpawned(entity);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String entityType = switch (pattern) {
            case 1 -> "item display";
            case 2 -> "block display";
            case 3 -> "interaction";
            default -> "text display";
        };
        return "skbee spawn " + entityType + " display " + this.locations.toString(e, d);
    }

}
