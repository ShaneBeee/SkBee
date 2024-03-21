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
import com.shanebeestudios.skbee.api.bound.Bound;
import com.shanebeestudios.skbee.config.BoundConfig;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Name("Bound - All Bounds")
@Description("Get a list of non-temporary, temporary, or all bounds/ids. Optionally inside of a specific set of worlds.")
@Examples({"set {_temporaryBounds::*} to temporary bounds in world of player",
        "set {_nonTemporaryBounds::*} to nontemporary bounds in world(\"world_nether\")",
        "loop all bounds:",
        "loop all bounds in {worlds::*}:",
        "loop all bounds in world of player:",
        "loop all bounds in world \"world\"",
        "\tbroadcast loop-bound"})
@Since("2.15.0")
public class ExprBoundsAll extends SimpleExpression<Object> {

    private static final BoundConfig BOUND_CONFIG = SkBee.getPlugin().getBoundConfig();

    static {
        Skript.registerExpression(ExprBoundsAll.class, Object.class, ExpressionType.SIMPLE,
                "[all] [1:temporary|2:non[-| ]temporary] bound[s] [id:id[s]] [in %-worlds%]");
    }

    private Expression<World> worlds;
    private boolean ids;
    private int pattern;

    @SuppressWarnings({"unchecked", "NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parse) {
        this.worlds = (Expression<World>) exprs[0];
        this.ids = parse.hasTag("id");
        this.pattern = parse.mark;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Object[] get(Event event) {
        List<Bound> bounds = new ArrayList<>();
        List<String> ids = new ArrayList<>();

        if (this.worlds == null) {
            loopAllBounds(bounds, ids, BOUND_CONFIG.getBounds());
        } else {
            for (World world : this.worlds.getArray(event)) {
                loopAllBounds(bounds, ids, BOUND_CONFIG.getBoundsIn(world));
            }
        }
        return this.ids ? ids.toArray(new String[0]) : bounds.toArray(new Bound[0]);
    }

    private void loopAllBounds(List<Bound> boundList, List<String> idList, Collection<Bound> bounds) {
        for (Bound bound : bounds) {
            if (boundList.contains(bound) || idList.contains(bound.getId())) continue;
            if (this.pattern == 1 && !bound.isTemporary()) continue;
            if (this.pattern == 2 && bound.isTemporary()) continue;
            if (this.ids) {
                idList.add(bound.getId());
            } else {
                boundList.add(bound);
            }
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return this.ids ? String.class : Bound.class;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String ID = this.ids ? " ids" : "s";
        String inWorld = this.worlds != null ? (" in world " + worlds.toString(e, d)) : "";
        return switch (this.pattern) {
            case 1 -> "all temporary bound";
            case 2 -> "all non-temporary bound";
            default -> "all bound";
        } + ID + inWorld;
    }

}
