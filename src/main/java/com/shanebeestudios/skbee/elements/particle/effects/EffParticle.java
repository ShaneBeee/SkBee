package com.shanebeestudios.skbee.elements.particle.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.particle.ParticleUtil;
import com.shanebeestudios.skbee.api.registration.Registration;
import com.shanebeestudios.skbee.api.skript.base.Effect;
import com.shanebeestudios.skbee.api.particle.ParticleWrapper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffParticle extends Effect {

    public static void register(Registration reg) {
        reg.newEffect( EffParticle.class,
            "(lerp|draw|make) %number% [of] %minecraftparticle% [particle] [using %-object%] %directions% %locations% " +
                "[with (delta|offset) %-vector%] [with extra %-number%] [(1:with force)] [(for|to) %-players%]")
            .register();
    }

    private Expression<Number> count;
    private Expression<ParticleWrapper> particle;
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
        particle = (Expression<ParticleWrapper>) exprs[1];
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

        ParticleWrapper particle = this.particle.getSingle(event);
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
            ParticleUtil.spawnParticle(particle.getParticle(), players, location, count, data, delta, extra, force);
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
