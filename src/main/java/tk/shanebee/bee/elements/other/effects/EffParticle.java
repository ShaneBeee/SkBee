package tk.shanebee.bee.elements.other.effects;

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
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.bee.api.util.ParticleUtil;

@Name("Particle Spawn")
@Description({"Spawn a particle. This system is more inline with how Bukkit deals with particles, hence the amount of patterns.",
        "Some particles may be affected differently by these values, so let's break them down:",
        "\nfirst number = count, how many particles to spawn at once.",
        "\nparticle = the particle to spawn.",
        "\nusing = the data used for this particle (some particles like 'block', 'item' and 'dust' require more data).",
        "\nlocation = where you are going to spawn the particle.",
        "\noffset = a vector with the maximum random offset. The position of each particle will be randomized positively and negatively by the offset parameters on each axis.",
        "I believe some particles use the offset to set color. I'm not positive on this.",
        "\nextra = the extra data for this particle, depends on the particle used (normally speed).",
        "\nRequires Minecraft 1.13+"})
@Examples({"play 3 of item particle using diamond at location of player",
        "play 1 of block particle using dirt at location of target block",
        "play 10 of poof at player offset by vector(2, 2, 2) with extra 0.5",
        "play 20 of dust using dustOption(blue, 10) at location above target block"})
@Since("1.9.0")
public class EffParticle extends Effect {

    static {
        if (Skript.isRunningMinecraft(1, 13)) {
            Skript.registerEffect(EffParticle.class,
                    "(spawn|play) %number% [of] %particle% [particle] [using %-itemtype/blockdata/dustoption%] %directions% %locations% [(for|to) %-players%]",
                    "(spawn|play) %number% [of] %particle% [particle] [using %-itemtype/blockdata/dustoption%] %directions% %locations% offset by %vector% [(for|to) %-players%]",
                    "(spawn|play) %number% [of] %particle% [particle] [using %-itemtype/blockdata/dustoption%] %directions% %locations% offset by %vector% with extra %number% [(for|to) %-players%]");
        }
    }

    private int pattern;
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

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        pattern = matchedPattern;
        count = (Expression<Number>) exprs[0];
        particle = (Expression<Particle>) exprs[1];
        data = (Expression<Object>) exprs[2];
        location = Direction.combine((Expression<? extends Direction>) exprs[3], (Expression<? extends Location>) exprs[4]);
        offset = pattern != 0 ? (Expression<Vector>) exprs[5] : null;
        extra = pattern == 2 ? (Expression<Number>) exprs[6] : null;
        players = (Expression<Player>) exprs[pattern + 5];
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void execute(@NotNull Event e) {
        if (this.count == null || this.particle == null || this.location == null) return;

        int count = this.count.getSingle(e).intValue();
        Particle particle = this.particle.getSingle(e);
        Location[] locations = this.location.getArray(e);
        Vector offset = this.offset != null ? this.offset.getSingle(e) : null;
        double extra = this.extra != null ? this.extra.getSingle(e).doubleValue() : 0.0d;
        Object data = this.data != null ? this.data.getSingle(e) : null;
        Player[] players = this.players != null ? this.players.getArray(e) : null;

        for (Location location : locations) {
            switch (pattern) {
                case 0:
                    ParticleUtil.spawnParticle(players, particle, location, count, data);
                    break;
                case 1:
                    ParticleUtil.spawnParticle(players, particle, location, count, offset, data);
                    break;
                case 2:
                    ParticleUtil.spawnParticle(players, particle, location, count, offset, extra, data);
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String data = this.data != null ? "using " + this.data.toString(e, d) : "";
        String offset = this.offset != null ? "offset by " + this.offset.toString(e, d) : "";
        String extra = this.extra != null ? "with extra " + this.extra.toString(e, d) : "";
        String players = this.players != null ? "to " + this.players.toString(e, d) : "";

        return String.format("spawn %s of %s %s %s %s %s %s", this.count.toString(e, d), this.particle.toString(e, d),
                data, this.location.toString(e, d), offset, extra, players);
    }

}
