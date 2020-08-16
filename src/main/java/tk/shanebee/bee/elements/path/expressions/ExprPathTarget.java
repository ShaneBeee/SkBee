package tk.shanebee.bee.elements.path.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.destroystokyo.paper.entity.Pathfinder;
import com.destroystokyo.paper.entity.Pathfinder.PathResult;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Name("Pathfinding - Path Target")
@Description({"Set the path of an entity to target a specific location, with an optional speed",
        "Get the location of the paths end.",
        "Delete will stop the entity from pathfinding."})
@Examples({"set path target of event-entity to player", "set path target with speed 1.5 to location of above player",
        "set path targets of all entities to location of player",
        "delete path target of event-entity"})
@Since("INSERT VERSION")
public class ExprPathTarget extends SimplePropertyExpression<LivingEntity, Location> {

    static {
        register(ExprPathTarget.class, Location.class, "[final] path target[s] [with speed %-number%]", "livingentities");
    }

    @Nullable
    private Expression<Number> speed;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        setExpr((Expression<LivingEntity>) exprs[1]);
        speed = (Expression<Number>) exprs[0];
        Long i;

        return true;
    }

    @Nullable
    @Override
    public Location convert(@NotNull LivingEntity mob) {
        if (mob instanceof Mob) {
            Pathfinder pathfinder = ((Mob) mob).getPathfinder();
            if (pathfinder.hasPath()) {
                PathResult result = pathfinder.getCurrentPath();
                return result != null ? result.getFinalPoint() : mob.getLocation();
            }
        }
        return mob.getLocation();
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(ChangeMode mode) {
        switch (mode) {
            case SET:
            case DELETE:
                return CollectionUtils.array(Location.class);
        }
        return null;
    }

    @Override
    public void change(@NotNull Event e, @Nullable Object[] delta, @NotNull ChangeMode mode) {
        Location location = delta != null ? (Location) delta[0] : null;
        Number number = this.speed != null ? this.speed.getSingle(e) : 0;
        for (LivingEntity entity : getExpr().getArray(e)) {
            if (!(entity instanceof Mob)) return;

            Mob mob = ((Mob) entity);
            switch (mode) {
                case SET:
                    if (location != null)
                        if (number != null && number.doubleValue() != 0.0)
                            mob.getPathfinder().moveTo(location, number.doubleValue());
                        else
                            mob.getPathfinder().moveTo(location);
                    break;
                case DELETE:
                    mob.getPathfinder().stopPathfinding();
            }
        }
    }

    @Override
    public @NotNull Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "path target";
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String speed = this.speed != null ? " with speed " + this.speed.toString(e, d) : "";
        return "path target" + speed + " of " + getExpr().toString(e, d);
    }
}
