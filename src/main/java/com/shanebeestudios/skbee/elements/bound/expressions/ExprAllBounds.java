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
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Bound - All Bounds")
@Description("Get a list of non-temporary, temporary, or all bounds/ids")
@Examples({"set {_temporaryBounds::*} to all temporary bounds",
        "set {_nonTemporaryBounds::*} to nontemporary bounds",
        "loop all bounds:",
        "\tbroadcast loop-bound"})
@Since("1.15.0, 2.10.0 (temporary bounds)")
public class ExprAllBounds extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprAllBounds.class, Object.class, ExpressionType.SIMPLE,
                "[all [[of] the]] [-1:temporary|1:non[-| ]temporary] bound[s] [:id[s]]");
    }

    private static final BoundConfig boundConfig = SkBee.getPlugin().getBoundConfig();
    private boolean ID;
    private Kleenean boundType;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parse) {
        this.ID = parse.hasTag("id");
        this.boundType = Kleenean.get(parse.mark);
        return true;
    }

    @Nullable
    @Override
    protected Object[] get(Event event) {
        List<Bound> bounds = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (Bound bound : boundConfig.getBounds()) {
            if (bounds.contains(bound) || bounds.contains(bound.getId())) continue;
            if (boundType == Kleenean.FALSE && !bound.isTemporary()) continue;
            if (boundType == Kleenean.TRUE && bound.isTemporary()) continue;
            if (ID) {
                ids.add(bound.getId());
            } else {
                bounds.add(bound);
            }
        }
        return ID ? ids.toArray(new String[0]) : bounds.toArray(new Bound[0]);
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
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        String ID = this.ID ? " ids" : "s";
        return switch (boundType) {
            case TRUE -> "all non-temporary bound" + ID;
            case FALSE -> "all temporary bound" + ID;
            case UNKNOWN -> "all bound" + ID;
        };
    }

}
