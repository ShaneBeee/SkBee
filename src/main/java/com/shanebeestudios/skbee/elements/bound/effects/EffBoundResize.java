package com.shanebeestudios.skbee.elements.bound.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.api.registration.Registration;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class EffBoundResize extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffBoundResize.class,
                "resize [:full] bound %bound% (within|between) %block/location% and %block/location%",
                "resize full bound %bound%")
            .name("Bound - Resize")
            .description("Resize a current bound.",
                "Full will mark the bound to use the lowest/highest points of the world.",
                "The second pattern will mark as a full bound without changing the locations.",
                "",
                "NOTE: World of a bound cannot be changed.",
                "",
                "**SPECIAL NOTE**:",
                "- When using locations = The bound resizing will use the locations you pass thru",
                "- When using blocks = The bound resizing will extend the x/y/z axes by 1 to fully include those blocks.")
            .examples("resize bound bound with id \"test\" between {_l1} and {_l1}",
                "resize bound bound with id \"test\" between block at{_l1} and block at {_l1}",
                "resize full bound bound with id \"test\"")
            .since("2.5.3")
            .register();
    }

    private Expression<Bound> bound;
    private Expression<?> point1, point2;
    private boolean full;
    private int pattern;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.full = matchedPattern == 1 || parseResult.hasTag("full");
        this.bound = (Expression<Bound>) exprs[0];
        if (matchedPattern == 0) {
            this.point1 = exprs[1];
            this.point2 = exprs[2];
        }
        this.pattern = matchedPattern;
        return true;
    }

    @Override
    protected void execute(Event event) {
        Bound bound = this.bound.getSingle(event);
        if (bound == null) return;
        if (this.pattern == 1) {
            bound.setFull(true);
            return;
        }
        Object point1 = this.point1.getSingle(event);
        Object point2 = this.point2.getSingle(event);
        if (point1 == null || point2 == null) return;

        boolean usingBlocks = false;
        Location lesser;
        Location greater;
        if (point1 instanceof Location loc1) lesser = loc1;
        else if (point1 instanceof Block block1) {
            lesser = block1.getLocation();
            usingBlocks = true;
        } else return;

        if (point2 instanceof Location loc2) {
            greater = loc2;
            usingBlocks = false;
        } else if (point2 instanceof Block block2) {
            greater = block2.getLocation();
        } else return;

        World world = bound.getWorld();
        if (world == null || lesser.getWorld() != world || greater.getWorld() != world) return;

        bound.resize(lesser, greater, usingBlocks);
        if (this.full) bound.setFull(true);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        if (this.pattern == 1) {
            return "resize full bound " + this.bound.toString(e, d);
        }
        String full = this.full ? "full " : "";
        return "resize " + full + "bound " + this.bound.toString(e, d) + " within " +
            this.point1.toString(e, d) + " and " + this.point2.toString(e, d);
    }

}
