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
        "\nfirst number = count, how many particles to spawn at once. (use '0' if you notice the particle kinda flies away.)",
        "\nparticle = the particle to spawn.",
        "\nusing = the data used for this particle (some particles like 'block', 'item' and 'dust' require more data).",
        "\nlocation = where you are going to spawn the particle.",
        "\noffset = a vector with the maximum random offset. The position of each particle will be randomized positively and negatively by the offset parameters on each axis.",
        "I believe some particles use the offset to set color. I'm not positive on this.",
        "\nextra = the extra data for this particle, depends on the particle used (normally speed).",
        "\nforce = whether to send the particle to players within an extended range and encourage ",
        "their client to render it regardless of settings (this only works when not using `for player[s]`) (default = false)",
        "\nfor %players% = will only send this particle to a player, not the whole server.",
        "\nRequires Minecraft 1.13+"})
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
                "(lerp|draw|make) %number% [of] %particle% [particle] [using %-itemtype/blockdata/dustoption/dusttransition/vibration" +
                        "/number%] %directions% %locations% [with offset %-vector%] [with extra %-number%] [(1¦with force)] [(for|to) %-players%]");
    }

    private Expression<Number> count;
    private Expression<Particle> particle;
    private Expression<Location> location;
    @Nullable
    private Expression<Vector> offset;
    private Expression<Number> extra;
    @Nullable
    private Expression<Object> data;
    @Nullable
    private Expression<Player> players;
    private boolean force;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        count = (Expression<Number>) exprs[0];
        particle = (Expression<Particle>) exprs[1];
        data = (Expression<Object>) exprs[2];
        location = Direction.combine((Expression<? extends Direction>) exprs[3], (Expression<? extends Location>) exprs[4]);
        offset = (Expression<Vector>) exprs[5];
        extra = (Expression<Number>) exprs[6];
        players = (Expression<Player>) exprs[7];
        force = parseResult.mark == 1;
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void execute(@NotNull Event e) {
        if (this.count == null || this.particle == null || this.location == null) return;

        boolean hasExtra = this.extra != null;
        Number countSingle = this.count.getSingle(e);
        int count = countSingle != null ? countSingle.intValue() : 0;

        Particle particle = this.particle.getSingle(e);
        Location[] locations = this.location.getArray(e);
        Vector offset = this.offset != null ? this.offset.getSingle(e) : new Vector(0, 0, 0);
        double extra = hasExtra ? this.extra.getSingle(e).doubleValue() : 1;
        Object data = this.data != null ? this.data.getSingle(e) : null;
        Player[] players = this.players != null ? this.players.getArray(e) : null;

        for (Location location : locations) {
            ParticleUtil.spawnParticle(players, particle, location, count, data, offset, extra, force);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String data = this.data != null ? "using " + this.data.toString(e, d) : "";
        String offset = this.offset != null ? "with offset " + this.offset.toString(e, d) : "";
        String extra = this.extra != null ? "with extra " + this.extra.toString(e, d) : "";
        String players = this.players != null ? "to " + this.players.toString(e, d) : "";

        return String.format("draw %s of %s %s %s %s %s %s", this.count.toString(e, d), this.particle.toString(e, d),
                data, this.location.toString(e, d), offset, extra, players);
    }

}
