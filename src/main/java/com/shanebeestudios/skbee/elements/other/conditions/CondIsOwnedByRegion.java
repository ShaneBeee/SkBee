package com.shanebeestudios.skbee.elements.other.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.api.region.RegionUtils;
import com.shanebeestudios.skbee.api.skript.base.Condition;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Folia - Object Owned by Current Region")
@Description({"Returns whether the current thread is ticking a region and that the region being ticked owns the " +
    "entity/block/location/chunk at the specified world/position as included in the specified object.",
    "The chunk pattern accepts Chunk X/Z, not world position.",
    "This is useful to check before manipulating entities/blocks/ect which may not be in the same region as the caller."})
@Examples({"on break:",
    "\tif block at location(1,1,1) is owned by current region:",
    "\t\tset block at location(1,1,1) to stone"})
@Since("INSERT VERSION")
public class CondIsOwnedByRegion extends Condition {

    static {
        Skript.registerCondition(CondIsOwnedByRegion.class,
            "%entities/blocks/locations% (is|are)[neg:n't] owned by current region",
            "chunk at %integer%,[ ]%integer% (in|of) %world% is[neg:n't] owned by current region");
    }

    private int pattern;
    private Expression<?> objects;
    private Expression<Integer> x, z;
    private Expression<World> world;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setNegated(parseResult.hasTag("neg"));
        this.pattern = matchedPattern;
        if (matchedPattern == 0) {
            this.objects = exprs[0];
        } else {
            this.x = (Expression<Integer>) exprs[0];
            this.z = (Expression<Integer>) exprs[1];
            this.world = (Expression<World>) exprs[2];
        }
        return true;
    }


    @Override
    public boolean check(Event event) {
        if (this.pattern == 0) {
            return this.objects.check(event, RegionUtils::isOwnedByCurrentRegion, isNegated());
        } else {
            Integer x = this.x.getSingle(event);
            Integer z = this.z.getSingle(event);
            if (x == null || z == null) return false;
            return this.world.check(event, world -> RegionUtils.isOwnedByCurrentRegion(world, x, z), isNegated());
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        if (this.pattern == 0) {
            String plural = this.objects.isSingle() ? "is" : "are";
            return new SyntaxStringBuilder(event, debug)
                .append(this.objects)
                .append(isNegated() ? plural + " not" : plural)
                .append("owned by current region")
                .toString();
        } else {
            return new SyntaxStringBuilder(event, debug)
                .append("chunk at", this.x, ",", this.z)
                .append("in world", this.world)
                .append(isNegated() ? "is not" : "is")
                .append("owned by current region")
                .toString();
        }
    }

}
