package com.shanebeestudios.skbee.elements.bound.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.elements.bound.objects.Bound;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Bound - All Bounds")
@Description("Get a list of non-temporary, temporary, or all bounds, optionally in a specific world.")
@Examples({"set {_non-temporary::*} to all non-temporary bounds",
        "set {_temporary::*} to all temporary bounds",
        "set {_bounds::*} to all bounds in world of player",
        "loop all bounds:",
        "\tif {bounds::%loop-bound%::owner} = player:",
        "\t\tsend \"You own bound %loop-bound%\""})
@Since("1.15.0, INSERT VERSION (temporary bounds)")
public class ExprBoundAllBounds extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprBoundAllBounds.class, Object.class, ExpressionType.SIMPLE,
                "[(all [[of] the]|the)] [group:[:non[-| ]]temporary] bounds [(in|of) %-worlds%]",
                "[(all [[of] the]|the)] [group:[:non(-| )]temporary] bound ids [(in|of) %-worlds%]");
    }

    private enum Group {

        ALL("all"),
        NON_TEMPORARY("non temporary"),
        TEMPORARY("temporary");

        private final String name;

        Group(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    private boolean ID;
    private Group group;
    private Expression<World> world;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        ID = matchedPattern == 1;
        world = (Expression<World>) exprs[0];
        if (parseResult.hasTag("group")) {
            group = parseResult.hasTag("non") ? Group.NON_TEMPORARY : Group.TEMPORARY;
        } else {
            group = Group.ALL;
        }
        return true;
    }

    @Nullable
    @Override
    protected Object[] get(Event event) {
        World[] worlds = null;
        if (this.world != null) {
            worlds = this.world.getArray(event);
        }
        World[] finalWorlds = worlds;
        if (ID) {
            List<String> bounds = new ArrayList<>();
            SkBee.getPlugin().getBoundConfig().getBounds().forEach(bound -> {
                if (group == Group.TEMPORARY && !bound.isTemporary()) return;
                if (group == Group.NON_TEMPORARY && bound.isTemporary()) return;
                if (finalWorlds != null) {
                    for (World world : finalWorlds) {
                        if (bound.getWorld() == world) {
                            bounds.add(bound.getId());
                        }
                    }
                } else {
                    bounds.add(bound.getId());
                }
            });
            return bounds.toArray(new String[0]);
        } else {
            List<Bound> bounds = new ArrayList<>();
            SkBee.getPlugin().getBoundConfig().getBounds().forEach(bound -> {
                if (group == Group.TEMPORARY && !bound.isTemporary()) return;
                if (group == Group.NON_TEMPORARY && bound.isTemporary()) return;
                if (finalWorlds != null) {
                    for (World world : finalWorlds) {
                        if (bound.getWorld() == world) {
                            bounds.add(bound);
                        }
                    }
                } else {
                    bounds.add(bound);
                }
            });
            return bounds.toArray(new Bound[0]);
        }

    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        if (ID) {
            return String.class;
        }
        return Bound.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return group.getName() + " bound" + (ID ? " ids" : "s" + (this.world != null ? " in world[s] " + this.world.toString(e, d) : ""));
    }

}
