package com.shanebeestudios.skbee.elements.particle.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.particle.ParticleUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Particle Spawn")
@Description({"Spawn a particle. This system is more inline with how Bukkit deals with particles, hence the choices in the pattern.",
    "Some particles may be affected differently by these values, so let's break them down:",
    "`first number` = count, how many particles to spawn at once. (use '0' if you notice the particle kinda flies away.)",
    "`particle` = the particle to spawn.",
    "`using` = the data used for this particle (some particles like 'block', 'item' and 'dust' require more data).",
    "`location` = where you are going to spawn the particle.",
    "`delta` = a vector with the maximum random offset. The position of each particle will be randomized positively and negatively by the offset parameters on each axis.",
    "Some particles use the delta to set color/direction if count is set to 0.",
    "`extra` = the extra data for this particle, depends on the particle used (normally speed).",
    "`force` = whether to send the particle to players within an extended range and encourage ",
    "their client to render it regardless of settings (this only works when not using `for player[s]`) (default = false)",
    "`for %players%` = will only send this particle to a player, not the whole server."})
@Examples({"make 3 of item particle using diamond at location of player",
    "make 1 of block particle using dirt at location of target block",
    "make 10 of poof at player with offset vector(2, 2, 2) with extra 0.5",
    "draw 20 of dust using dustOption(blue, 10) at location above target block",
    "draw 1 of dust_color_transition using dustTransition(blue, green, 3) at location of player",
    "draw 1 of vibration using vibration({loc1}, {loc2}, 1 second) at {loc1} with force",
    "make 0 of shriek using 1 above target block of player",
    "make 1 of sculk_charge using 0.1 at {_loc} with force"})
@Since("1.9.0")
public class EffParticle extends Effect {

    static {
        Skript.registerEffect(EffParticle.class,
            "(lerp|draw|make) %number% [of] %particle% [particle] [using %-object%] %directions% %locations% " +
                "[with (delta|offset) %-vector%] [with extra %-number%] [(1:with force)] [(for|to) %-players%]");
    }

    private Expression<Number> count;
    private Expression<Particle> particle;
    private Expression<Location> location;
    @Nullable
    private Expression<Vector> delta;
    private Expression<Number> extra;
    @Nullable
    private Expression<?> data;
    @Nullable
    private Expression<Player> players;
    private boolean force;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        count = (Expression<Number>) exprs[0];
        particle = (Expression<Particle>) exprs[1];
        data = LiteralUtils.defendExpression(exprs[2]);
        location = Direction.combine((Expression<? extends Direction>) exprs[3], (Expression<? extends Location>) exprs[4]);
        delta = (Expression<Vector>) exprs[5];
        extra = (Expression<Number>) exprs[6];
        players = (Expression<Player>) exprs[7];
        force = parseResult.mark == 1;
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        if (this.count == null || this.particle == null || this.location == null) return;

        Number countSingle = this.count.getSingle(event);
        int count = countSingle != null ? countSingle.intValue() : 0;

        Particle particle = this.particle.getSingle(event);
        if (particle == null) return;

        Vector delta = new Vector(0, 0, 0);
        if (this.delta != null) {
            Vector offsetSingle = this.delta.getSingle(event);
            if (offsetSingle != null) delta = offsetSingle;
        }
        double extra = 1;
        if (this.extra != null) {
            Number extraSingle = this.extra.getSingle(event);
            extra = extraSingle != null ? extraSingle.doubleValue() : 1;
        }

        Object data = this.data != null ? this.data.getSingle(event) : null;
        Player[] players = this.players != null ? this.players.getArray(event) : null;

        for (Location location : this.location.getArray(event)) {
            ParticleUtil.spawnParticle(particle, players, location, count, data, delta, extra, force);
        }
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String data = this.data != null ? "using " + this.data.toString(e, d) : "";
        String offset = this.delta != null ? "with delta " + this.delta.toString(e, d) : "";
        String extra = this.extra != null ? "with extra " + this.extra.toString(e, d) : "";
        String players = this.players != null ? "to " + this.players.toString(e, d) : "";

        return String.format("draw %s of %s %s %s %s %s %s", this.count.toString(e, d), this.particle.toString(e, d),
            data, this.location.toString(e, d), offset, extra, players);
    }

}
