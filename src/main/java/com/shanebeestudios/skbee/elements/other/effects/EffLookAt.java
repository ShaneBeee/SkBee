package com.shanebeestudios.skbee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.util.Util;
import io.papermc.paper.entity.LookAnchor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Entity Look At")
@Description({"Make a mob/player look at a specific location.",
        "\nNOTE: This will only work on MOBS and players.",
        "\nRequires a PaperMC server."})
@Examples({"make last spawned entity look at player",
        "make player look at {loc}",
        "make all players look at {loc}"})
@Since("2.7.1")
public class EffLookAt extends Effect {

    private static final boolean HAS_METHOD = Skript.methodExists(Mob.class, "lookAt", Location.class);

    static {
        Skript.registerEffect(EffLookAt.class,
                "make %entities% (look at|face) %location%");
    }

    private Expression<Entity> entities;
    private Expression<Location> lookAt;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!HAS_METHOD) {
            Skript.error("This effect is not supported by you server, it requires a PaperMC server.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        Util.skript27Warning("look at", "effect");
        this.entities = (Expression<Entity>) exprs[0];
        this.lookAt = (Expression<Location>) exprs[1];
        return true;
    }

    @SuppressWarnings({"NullableProblems", "UnstableApiUsage"})
    @Override
    protected void execute(Event event) {
        Location location = this.lookAt.getSingle(event);
        if (location == null) return;
        double x = location.x();
        double y = location.y();
        double z = location.z();

        for (Entity entity : this.entities.getArray(event)) {
            if (entity instanceof Mob mob) {
                mob.lookAt(x, y, z);
            } else if (entity instanceof Player player) {
                player.lookAt(x, y, z, LookAnchor.EYES);
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "make " + this.entities.toString(e, d) + " look at " + this.lookAt.toString(e, d);
    }

}
