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
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Abort Creature Spawn")
@Description({"Abort a creature spawn event, preventing it from retrying more attempts after canceling.",
        "aborting should only be done if you're blanket blocking this entity type from spawning"})
@Examples({"on pre creature spawn of an animal",
        "\tcancel event",
        "\tabort creature spawn"})
@Since("INSERT VERSION")
public class EffAbortSpawn extends Effect {

    private static final boolean HAS_PRE_SPAWN_EVENT = Skript.classExists("com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent");

    static {
        Skript.registerEffect(EffAbortSpawn.class,
                "[:un]abort creature spawn");
    }

    private boolean abort;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPatteren, Kleenean isDelayed, ParseResult parseResult) {
        if (!HAS_PRE_SPAWN_EVENT) {
            Skript.error("In order to use the 'abort spawn' effect, you must be running a PaperMC server.");
            return false;
        }
        if (!getParser().isCurrentEvent(PreCreatureSpawnEvent.class)) {
            Skript.error("The 'abort spawn' effect can only be used an a pre creature spawn event!");
            return false;
        }
        abort = !parseResult.hasTag("un");
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (event instanceof PreCreatureSpawnEvent preCreatureSpawnEvent)
            preCreatureSpawnEvent.setShouldAbortSpawn(abort);
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return (abort ? "" : "un") + "abort creature spawn";
    }
}
