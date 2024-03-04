package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.block.BlockState;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("BlockState - Update")
@Description({"Attempts to update the block represented by this state.",
        "Unless force is applied, this will not modify the state of a block if it is no longer",
        "the same type as it was when this state was taken.",
        "If done without physics updates, it will not trigger a physics update on surrounding blocks which",
        "could cause them to update or disappear."})
@Examples({"set {_state} to blockstate of event-block",
        "set event-block to air",
        "wait 1 minute",
        "force update {_state} without physics updates"})
@Since("2.13.0")
public class EffBlockstateUpdate extends Effect {

    static {
        Skript.registerEffect(EffBlockstateUpdate.class,
                "[:force] update %blockstates% [physics:without (neighbour|physics) updates]");
    }

    private boolean force;
    private boolean physics;
    private Expression<BlockState> blockStates;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.force = parseResult.hasTag("force");
        this.physics = !parseResult.hasTag("physics");
        this.blockStates = (Expression<BlockState>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        for (BlockState blockState : this.blockStates.getArray(event)) {
            blockState.update(this.force, this.physics);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String force = this.force ? "force " : "";
        String blockstate = this.blockStates.toString(e, d);
        String physics = this.physics ? " without neighbour updates" : "";
        return force + "update " + blockstate + physics;
    }

}
