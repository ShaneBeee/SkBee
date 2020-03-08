package tk.shanebee.bee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import org.bukkit.block.Beehive;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.EntityBlockStorage;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.util.List;

@Name("EntityBlockStorage - Add/Release entities")
@Description({"Add/release entities to/from blocks which can store entities.",
        "When releasing bees at night/during rain, they will immediately go back in their hive, use the optional timespan to keep them outside.",
        "You can optionally put the released entities into a variable (see examples).",
        "As of 1.15 this only includes beehives/bee nests! Requires Spigot/Paper 1.15.2+"})
@Examples({"add last spawned bee to storage of target block of player",
        "release all entities from storage of target block of player",
        "release all entities from storage of event-block for 2 minutes",
        "release all entities from storage of target block of player for 1 minute into {_bees::*}"})
@Since("1.0.0")
public class EffEntityBlockStorage extends Effect {

    static {
        if (Skript.classExists("org.bukkit.block.EntityBlockStorage")) {
            Skript.registerEffect(EffEntityBlockStorage.class,
                    "release [all] entities from [storage of] %blocks% [for %-timespan%] [into %-objects%]",
                    "add %entities% to [storage of] %block%");
        }
    }

    @SuppressWarnings("null")
    private Expression<Entity> entities;
    private Expression<Block> blocks;
    private Expression<Timespan> timespan;
    private Expression<?> var;
    private boolean release;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        this.entities = i == 0 ? null : (Expression<Entity>) exprs[0];
        this.blocks = (Expression<Block>) exprs[i];
        this.release = i == 0;
        this.timespan = release ? (Expression<Timespan>) exprs[1] : null;
        this.var = release ? exprs[2] : null;
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void execute(Event event) {
        if (this.release) {
            if (this.blocks == null) return;
            long ticks = this.timespan != null ? this.timespan.getSingle(event).getTicks_i() : 0;
            for (Block block : this.blocks.getArray(event)) {
                BlockState state = block.getState();
                if (state instanceof EntityBlockStorage<?>) {
                    List<Entity> entities = ((EntityBlockStorage<Entity>) state).releaseEntities();
                    for (Entity entity : entities) {
                        if (entity instanceof Bee && ticks > 0) {
                            ((Bee) entity).setCannotEnterHiveTicks(((int) ticks));;
                        }
                    }
                    if (var != null) {
                        var.change(event, entities.toArray(), Changer.ChangeMode.SET);
                    }
                }
            }
        } else {
            if (this.blocks == null || this.entities == null) return;
            BlockState state = this.blocks.getSingle(event).getState();

            for (Entity entity : this.entities.getArray(event)) {
                if (state instanceof Beehive && entity instanceof Bee) {
                    ((Beehive) state).addEntity((Bee) entity);
                    state.update();
                }
            }
        }
    }

    @Override
    public String toString(Event e, boolean d) {
        String time = this.timespan != null ? " for " + this.timespan.toString(e, d) : "";
        String var = this.var != null ? " into " + this.var.toString(e, d) : "";
        return this.release ? "Release all entities from " + this.blocks.toString(e, d) + time + var:
                "Add " + this.entities.toString(e, d) + " to storage of " + this.blocks.toString(e, d);
    }

}
