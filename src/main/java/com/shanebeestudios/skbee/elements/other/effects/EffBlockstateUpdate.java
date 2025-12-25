package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("BlockState - Update/Apply")
@Description({"Attempts to update the block represented by this state.",
    "Unless force is applied, this will not modify the state of a block if it is no longer",
    "the same type as it was when this state was taken.",
    "You can optionally apply this blockstate to another block.",
    "If done without physics updates, it will not trigger a physics update on surrounding blocks which",
    "could cause them to update or disappear."})
@Examples({"set {_state} to blockstate of event-block",
    "set event-block to air",
    "wait 1 minute",
    "force update {_state} without physics updates",
    "",
    "force apply {_state} to target block"})
@Since("2.13.0")
public class EffBlockstateUpdate extends Effect {

    static {
        Skript.registerEffect(EffBlockstateUpdate.class,
            "[:force] update %blockstates% [physics:without (neighbour|physics) updates]",
            "[:force] apply %blockstates% (to|into|onto) %block/location% [physics:without (neighbour|physics) updates]");
    }

    private boolean force;
    private boolean physics;
    private Expression<BlockState> blockStates;
    private Expression<?> location;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.force = parseResult.hasTag("force");
        this.physics = !parseResult.hasTag("physics");
        this.blockStates = (Expression<BlockState>) exprs[0];
        if (matchedPattern == 1) {
            this.location = exprs[1];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        Location location = null;

        if (this.location != null) {
            Object locExpr = this.location.getSingle(event);
            if (locExpr instanceof Location loc) location = loc;
            else if (locExpr instanceof Block block) location = block.getLocation();
        }
        for (BlockState blockState : this.blockStates.getArray(event)) {
            if (location != null) blockState = blockState.copy(location);
            blockState.update(this.force, this.physics);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        SyntaxStringBuilder builder = new SyntaxStringBuilder(e, d);
        builder.append(this.force ? "force" : "");

        if (this.location == null) {
            builder.append("update", this.blockStates);
        } else {
            builder.append("apply", this.blockStates);
            builder.append("to", this.location);
        }

        if (this.physics) {
            builder.append("without neighbour updates");
        }

        return builder.toString();
    }

}
