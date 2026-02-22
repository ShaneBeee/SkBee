package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffAbortSpawn extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffAbortSpawn.class, "[:un]abort creature spawn")
            .name("Abort Creature Spawn")
            .description("Abort a creature spawn event, preventing it from retrying more attempts after canceling.",
                "Aborting should only be done if you're blanket blocking this entity type from spawning.")
            .examples("on pre creature spawn of an animal:",
                "\tcancel event",
                "\tabort creature spawn")
            .since("2.16.0")
            .register();
    }

    private boolean abort;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPatteren, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().isCurrentEvent(PreCreatureSpawnEvent.class)) {
            Skript.error("The 'abort spawn' effect can only be used an a pre creature spawn event!");
            return false;
        }
        this.abort = !parseResult.hasTag("un");
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (event instanceof PreCreatureSpawnEvent preCreatureSpawnEvent)
            preCreatureSpawnEvent.setShouldAbortSpawn(this.abort);
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return (this.abort ? "" : "un") + "abort creature spawn";
    }

}
