package com.shanebeestudios.skbee.elements.worldborder.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("WorldBorder - Location Within")
@Description("Check if a location is within a world border.")
@Examples("if location of player is within world border of world of player:")
@Since("1.17.0")
public class CondWorldBorderInside extends Condition {

    static {
        PropertyCondition.register(CondWorldBorderInside.class, "within [border] %worldborder%", "locations");
    }

    private Expression<Location> locations;
    private Expression<WorldBorder> worldBorder;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        this.locations = (Expression<Location>) exprs[0];
        this.worldBorder = (Expression<WorldBorder>) exprs[1];
        setNegated(i == 1);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean check(Event event) {
        return locations.check(event,
                location -> worldBorder.check(event,
                        worldBorder -> worldBorder.isInside(location), isNegated()));
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String not = isNegated() ? " is/are not" : " is/are";
        return this.locations.toString(e, d) + not + " within " + this.worldBorder.toString(e, d);
    }

}
