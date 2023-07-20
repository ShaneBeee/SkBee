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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Bound - Bounds in world")
@Description("Get a list of non-temporary, temporary, or all bounds/ids inside a world.")
@Examples({"set {_temporaryBounds::*} to temporary bounds in world of player",
        "set {_nonTemporaryBounds::*} to nontemporary bounds in world(\"world_nether\")",
        "loop all bounds in {worlds::*}:",
        "\tbroadcast loop-bound"})
@Since("2.15.0")
public class ExprBoundsInWorld extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprBoundsInWorld.class, Object.class, ExpressionType.SIMPLE,
                "[all [[of] the]|the] [-1:temporary|1:non[-| ]temporary] bound[s] [:id[s]] in %worlds%");
    }

    private static final BoundConfig boundConfig = SkBee.getPlugin().getBoundConfig();
    private Expression<World> worlds;
    private boolean ID;
    private Kleenean boundType;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parse) {
        this.worlds = (Expression<World>) exprs[0];
        this.ID = parse.hasTag("id");
        this.boundType = Kleenean.get(parse.mark);
        return true;
    }

    @Override
    protected @Nullable Object[] get(Event event) {
        List<Bound> bounds = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (World world : worlds.getArray(event)) {
            for (Bound bound : boundConfig.getBoundsIn(world)) {
                if (bounds.contains(bound) || bounds.contains(bound.getId())) continue;
                if (boundType == Kleenean.FALSE && !bound.isTemporary()) continue;
                if (boundType == Kleenean.TRUE && bound.isTemporary()) continue;
                if (ID) {
                    ids.add(bound.getId());
                } else {
                    bounds.add(bound);
                }
            }
        }
        return ID ? ids.toArray(new String[0]) : bounds.toArray(new Bound[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<?> getReturnType() {
        if (ID) {
            return String.class;
        } else {
            return Bound.class;
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String ID = this.ID ? " ids" : "s";
        String inWorld = " in world " + worlds.toString(event, debug);
        return switch (boundType) {
            case TRUE -> "all non-temporary bound";
            case FALSE -> "all temporary bound";
            case UNKNOWN -> "all bound";
        } + ID + inWorld;
    }

}
