package tk.shanebee.bee.elements.other.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
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
        "Some particles may be affected differently by these values, so lets break them down:",
        "\nfirst number = count, how many particles to spawn at once.",
        "\nparticle = the particle to spawn",
        "\nlocation = where you are going to spawn the particle",
        "\noffset = a vector with the maximum random offset. The position of each particle will be randomized positively and negatively by the offset parameters on each axis.",
        "\nextra = the extra data for this particle, depends on the particle used (normally speed)",
        "\nusing = the data used for this particle (some particles like 'block', 'item' and 'dust' require more data."})
@Examples({"play 3 of item particle at location of player using diamond",
        "play 1 of block particle at location of target block using dirt",
        "play 10 of poof at player with offset of vector(2, 2, 2) with extra 0.5"})
public class EffParticle extends Effect {

    static {
        if (Skript.isRunningMinecraft(1, 13)) {
            Skript.registerEffect(EffParticle.class,
                    "(spawn|play|show) %number% [of] %particle% [particle] at %location% [(for|to) %-players%]",
                    "(spawn|play|show) %number% [of] %particle% [particle] at %location% with offset of %vector% [(for|to) %-players%]",
                    "(spawn|play|show) %number% [of] %particle% [particle] at %location% with offset of %vector% with extra %number% [(for|to) %-players%]",
                    "(spawn|play|show) %number% [of] %particle% [particle] at %location% with offset of %vector% with extra %number% using %itemtype/blockdata/dustoption% [(for|to) %-players%]",
                    "(spawn|play|show) %number% [of] %particle% [particle] at %location% with offset of %vector% using %itemtype/blockdata/dustoption% [(for|to) %-players%]",
                    "(spawn|play|show) %number% [of] %particle% [particle] at %location% using %itemtype/blockdata/dustoption% [(for|to) %-players%]");
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

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        pattern = matchedPattern;
        count = (Expression<Number>) exprs[0];
        particle = (Expression<Particle>) exprs[1];
        location = (Expression<Location>) exprs[2];
        offset = (pattern != 0 && pattern != 5) ? (Expression<Vector>) exprs[3] : null;
        extra = pattern == 2 || pattern == 3 ? (Expression<Number>) exprs[4] : null;

        int p = 0;
        int d = 0;
        switch (pattern) {
            case 0:
                p = 3;
                break;
            case 1:
                p = 4;
                break;
            case 2:
                p = 5;
                break;
            case 3:
                p = 6;
                d = 5;
                break;
            case 4:
                p = 5;
                d = 4;
                break;
            case 5:
                p = 4;
                d = 3;
        }
        players = (Expression<Player>) exprs[p];
        data = d > 0 ? (Expression<Object>) exprs[d] : null;
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void execute(Event e) {
        if (this.count == null || this.particle == null || this.location == null) {
            return;
        }

        int count = this.count.getSingle(e).intValue();
        Particle particle = this.particle.getSingle(e);
        Location location = this.location.getSingle(e);
        Vector offset = this.offset != null ? this.offset.getSingle(e) : null;
        double extra = this.extra != null ? this.extra.getSingle(e).doubleValue() : 0.0d;
        Object data = this.data != null ? this.data.getSingle(e) : null;
        Player[] players = this.players != null ? this.players.getArray(e) : null;

        switch (pattern) {
            case 0:
                ParticleUtil.spawnParticle(players, particle, location, count);
                break;
            case 1:
                ParticleUtil.spawnParticle(players, particle, location, count, offset);
                break;
            case 2:
                ParticleUtil.spawnParticle(players, particle, location, count, offset, extra);
                break;
            case 3:
                ParticleUtil.spawnParticle(players, particle, location, count, offset, extra, data);
                break;
            case 4:
                ParticleUtil.spawnParticle(players, particle, location, count, offset, data);
                break;
            case 5:
                ParticleUtil.spawnParticle(players, particle, location, count, data);
        }

    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return null;
    }

}
